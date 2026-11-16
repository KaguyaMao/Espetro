"""Bundle all faction/map image files from an explicitly selected client."""
import argparse
import hashlib
import importlib.util
import json
from pathlib import Path


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--client', type=Path, required=True)
    parser.add_argument('--report', type=Path, required=True)
    args = parser.parse_args()
    client = args.client.resolve(strict=True)
    resources = Path(__file__).resolve().parents[1] / 'src/main/resources'
    bundle = resources / 'espetro_previews'
    spec = importlib.util.spec_from_file_location('png_lossless', Path(__file__).with_name('png-lossless.py'))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    rows = ['# Espetro bundled previews v1: SHA256<TAB>bytes<TAB>game-relative image path']
    report = []
    for directory in ('EsFactions', 'EsWorld'):
        source = client / directory
        if not source.is_dir():
            raise ValueError(f'Missing client image directory: {source}')
        # Only image files: no world regions, saves, JSON configuration or backups.
        for path in sorted(source.rglob('*')):
            if not path.is_file() or path.suffix.lower() not in ('.png', '.jpg', '.jpeg'):
                continue
            path.resolve(strict=True).relative_to(source.resolve(strict=True))
            relative = path.relative_to(client).as_posix()
            suffix = path.relative_to(source).as_posix()
            if directory == 'EsFactions':
                if any(c not in 'abcdefghijklmnopqrstuvwxyz0123456789/._-' for c in suffix):
                    raise ValueError(f'Faction resource filename must be lowercase ASCII: {suffix}')
                resource = 'assets/espetro/textures/gui/factions/' + suffix
            else:
                resource = 'espetro_previews/files/' + relative
            original = path.read_bytes()
            encoded = module.optimized_png(original) if path.suffix.lower() == '.png' else original
            destination = resources / resource
            destination.parent.mkdir(parents=True, exist_ok=True)
            destination.write_bytes(encoded)
            digest = hashlib.sha256(encoded).hexdigest()
            rows.append(f'{digest}\t{len(encoded)}\t{relative}')
            report.append(dict(source=str(path), target=relative, resource=resource,
                               before=len(original), after=len(encoded), sha256=digest,
                               original_sha256=hashlib.sha256(original).hexdigest(), identical_pixels=True))
            print(f'{relative}: {len(original)} -> {len(encoded)}', flush=True)
    if not report:
        raise ValueError('No client preview images found')
    bundle.mkdir(parents=True, exist_ok=True)
    (bundle / 'manifest.tsv').write_text('\n'.join(rows) + '\n', encoding='utf-8')
    args.report.write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding='utf-8')
    print(f'Bundled {len(report)} previews, {sum(r["after"] for r in report)} bytes', flush=True)


if __name__ == '__main__':
    main()
