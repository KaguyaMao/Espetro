"""Refresh the jar-owned menu bundle from an explicitly selected client instance.

Usage: python tools/package-client-menus.py --client <game directory>
Only visual layouts/assets are managed. User preferences and runtime databases stay local.
"""
from pathlib import Path
import argparse
import hashlib
import importlib.util
import json
import re


def runtime_files(source):
    """Keep layouts and directory-loaded slideshow assets; prune unreferenced assets."""
    selected = []
    for name in ('customization', 'slideshows', 'custom_locals', 'panoramas'):
        selected.extend(p for p in (source / name).rglob('*') if p.is_file())
    selected.extend(source / name for name in ('customizablemenus.txt', 'custom_gui_screens.txt',
                    'audio_element_controller_metas.json') if (source / name).is_file())
    text = '\n'.join(p.read_text(encoding='utf-8') for p in selected
                     if p.suffix.lower() in ('.txt', '.json'))
    references = set(re.findall(r'\[source:local\]/(config/fancymenu/[^\r\n]+)', text))
    references = {value.strip() for value in references}
    for path in references:
        relative = Path(path).relative_to('config/fancymenu')
        if '..' in relative.parts or not (source / relative).exists():
            raise ValueError(f'Missing or unsafe menu resource reference: {path}')
    assets = [p for p in (source / 'assets').rglob('*') if p.is_file()]
    for path in assets:
        relative = 'config/fancymenu/' + path.relative_to(source).as_posix()
        if any(relative == ref or relative.startswith(ref.rstrip('/') + '/') for ref in references):
            selected.append(path)
    # Never ship editor/OS residue, even when it was copied into a live asset folder.
    residue = re.compile(r'(?i)(\.bak(?:[-.]|$)|\.old$|\.orig$|\.tmp$|\.log$|~$|\.DS_Store$|Thumbs\.db$)')
    return [p for p in selected if not residue.search(p.name)]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--client', type=Path, required=True)
    parser.add_argument('--lossless-png', action='store_true', help='Re-encode PNGs with identical RGBA pixels (requires Pillow)')
    parser.add_argument('--report', type=Path)
    args = parser.parse_args()
    source = args.client.resolve() / 'config/fancymenu'
    if not (source / 'customization/title_screen_layout.txt').is_file():
        raise SystemExit('No configured FancyMenu title screen in this client')
    output = Path(__file__).resolve().parents[1] / 'src/main/resources/assets/espetro/ui-bundle'
    selected = runtime_files(source)
    original_bytes = sum(p.stat().st_size for p in selected)
    changes = []
    optimize = None
    if args.lossless_png:
        spec = importlib.util.spec_from_file_location('png_lossless', Path(__file__).with_name('png-lossless.py'))
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        optimize = module.optimized_png
    expected = set()
    rows = ['# Espetro managed menu bundle v1: SHA256<TAB>bytes<TAB>game-relative path']
    for src in sorted(selected):
        relative = 'config/fancymenu/' + src.relative_to(source).as_posix()
        destination = output / 'files' / relative
        destination.parent.mkdir(parents=True, exist_ok=True)
        data = src.read_bytes()
        if optimize and src.suffix.lower() == '.png' and len(data) >= 128 * 1024:
            optimized = optimize(data)
            if len(optimized) < len(data):
                changes.append(dict(path=relative, before=len(data), after=len(optimized), identical_rgba=True))
                print(f'{src.name}: {len(data)} -> {len(optimized)}', flush=True)
            data = optimized
        if src.resolve() != destination.resolve() or data != destination.read_bytes():
            destination.write_bytes(data)
        expected.add(destination)
        rows.append(hashlib.sha256(data).hexdigest() + '\t'
                    + str(len(data)) + '\t' + relative)
    # Clean stale packaged payloads only, never touch the client's live configuration.
    for old in (output / 'files').rglob('*'):
        if old.is_file() and old not in expected:
            old.resolve(strict=True).relative_to((output / 'files').resolve(strict=True))
            old.unlink()
    (output / 'manifest.tsv').write_text('\n'.join(rows) + '\n', encoding='utf-8')
    packaged_bytes = sum(p.stat().st_size for p in expected)
    if args.report:
        args.report.write_text(json.dumps(dict(files=len(expected), before_selected_bytes=original_bytes,
                                              packaged_bytes=packaged_bytes, optimized=changes),
                                         indent=2, ensure_ascii=False), encoding='utf-8')
    print(f'Packaged {len(selected)} menu files ({packaged_bytes} bytes)')


if __name__ == '__main__':
    main()
