"""Check a resource-only slim release against its preserved predecessor."""
import argparse
from collections import Counter
from io import BytesIO
import hashlib
import json
from pathlib import Path
import re
import zipfile


def verify(before, after):
    from PIL import Image
    prefix = 'assets/espetro/ui-bundle/'
    with zipfile.ZipFile(before) as old, zipfile.ZipFile(after) as new:
        assert new.testzip() is None, 'CRC check failed'
        names = [e.filename for e in new.infolist() if not e.is_dir()]
        assert all(n == 1 for n in Counter(names).values()), 'Duplicate jar entries'
        previous = {e.filename for e in old.infolist() if not e.is_dir()}
        current = set(names)
        removed = sorted(previous - current)
        assert not current - previous, 'Unexpected new runtime files'
        assert all(name.startswith(prefix + 'files/config/fancymenu/assets/') for name in removed), removed
        changed_pngs = []
        unchanged_classes = 0
        for name in sorted(current & previous):
            left, right = old.read(name), new.read(name)
            if name.endswith('.class'):
                assert left == right, f'Runtime class changed: {name}'
                unchanged_classes += 1
            if left == right:
                continue
            if name in (prefix + 'manifest.tsv', 'META-INF/MANIFEST.MF'):
                continue
            assert name.endswith('.png'), f'Unexpected runtime change: {name}'
            with Image.open(BytesIO(left)) as source, Image.open(BytesIO(right)) as result:
                assert source.size == result.size, f'Image resized: {name}'
                assert source.convert('RGBA').tobytes() == result.convert('RGBA').tobytes(), f'Pixels changed: {name}'
            changed_pngs.append(name)
        managed = set()
        for row in new.read(prefix + 'manifest.tsv').decode('utf-8').splitlines():
            if not row or row.startswith('#'):
                continue
            digest, size, path = row.split('\t')
            data = new.read(prefix + 'files/' + path)
            assert len(data) == int(size) and hashlib.sha256(data).hexdigest() == digest, path
            managed.add(path)
        shipped = {name[len(prefix + 'files/'):] for name in names if name.startswith(prefix + 'files/')}
        assert shipped == managed, 'Unmanifested or missing menu payload'
        for name in names:
            if name.startswith(prefix + 'files/') and name.endswith(('.txt', '.json')):
                for reference in re.findall(r'\[source:local\]/(config/fancymenu/[^\r\n]+)', new.read(name).decode('utf-8')):
                    reference = reference.strip()
                    assert reference in managed or any(p.startswith(reference.rstrip('/') + '/') for p in managed), reference
        old_manifest = old.read(prefix + 'files/config/fancymenu/slideshows/myslideshow/properties.txt')
        assert new.read(prefix + 'files/config/fancymenu/slideshows/myslideshow/properties.txt') == old_manifest
        return dict(before_bytes=Path(before).stat().st_size, after_bytes=Path(after).stat().st_size,
                    sha256=hashlib.sha256(Path(after).read_bytes()).hexdigest(),
                    removed=removed, identical_runtime_classes=unchanged_classes,
                    identical_pixel_pngs=changed_pngs, managed_menu_files=len(managed),
                    crc='passed', menu_references='passed', source_and_result_pixels='identical')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('before', type=Path)
    parser.add_argument('after', type=Path)
    parser.add_argument('--report', type=Path, required=True)
    args = parser.parse_args()
    report = verify(args.before, args.after)
    args.report.write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding='utf-8')
    print(json.dumps({key: value for key, value in report.items() if not isinstance(value, list)}, ensure_ascii=False))
