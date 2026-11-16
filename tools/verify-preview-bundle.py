"""Verify packaged previews against the selected client, including decoded pixels."""
import argparse
from io import BytesIO
import hashlib
import json
from pathlib import Path
import zipfile


def main():
    from PIL import Image
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('jar', type=Path)
    parser.add_argument('--client', type=Path, required=True)
    parser.add_argument('--report', type=Path, required=True)
    args = parser.parse_args()
    checks = []
    with zipfile.ZipFile(args.jar) as archive:
        assert archive.testzip() is None, 'Invalid jar CRC'
        rows = archive.read('espetro_previews/manifest.tsv').decode('utf-8').splitlines()
        for row in rows:
            if not row or row.startswith('#'):
                continue
            digest, size, target = row.split('\t')
            if target.startswith('EsFactions/'):
                resource = 'assets/espetro/textures/gui/factions/' + target[len('EsFactions/'):]
            else:
                assert target.startswith('EsWorld/')
                resource = 'espetro_previews/files/' + target
            encoded = archive.read(resource)
            original = (args.client / target).read_bytes()
            assert len(encoded) == int(size) and hashlib.sha256(encoded).hexdigest() == digest, target
            with Image.open(BytesIO(original)) as left, Image.open(BytesIO(encoded)) as right:
                assert left.size == right.size and left.convert('RGBA').tobytes() == right.convert('RGBA').tobytes(), target
                dimensions = left.size
            checks.append(dict(target=target, resource=resource, bytes=len(encoded), dimensions=dimensions,
                               identical_pixels=True, sha256=digest))
        expected = {path.relative_to(args.client).as_posix()
                    for directory in ('EsFactions', 'EsWorld')
                    for path in (args.client / directory).rglob('*')
                    if path.is_file() and path.suffix.lower() in ('.png', '.jpg', '.jpeg')}
        assert {check['target'] for check in checks} == expected, 'Missing client preview images'
        report = dict(jar=str(args.jar), bytes=args.jar.stat().st_size,
                      sha256=hashlib.sha256(args.jar.read_bytes()).hexdigest(),
                      crc='passed', all_client_images='passed', images=checks)
        args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding='utf-8')
        print(f'Verified {len(checks)} previews, {report["bytes"]} byte jar, SHA256 {report["sha256"]}')


if __name__ == '__main__':
    main()
