"""Re-encode PNGs only when smaller, checking identical decoded RGBA pixels."""
from io import BytesIO
from pathlib import Path
import struct


def optimized_png(data):
    # Pillow is needed only for the explicit offline maintenance operation.
    from PIL import Image, PngImagePlugin
    chunks = PngImagePlugin.PngInfo()
    offset = 8
    # Preserve color interpretation/resolution metadata. No resampling/quantization.
    while offset < len(data):
        size = struct.unpack_from('>I', data, offset)[0]
        kind = data[offset + 4:offset + 8]
        payload = data[offset + 8:offset + 8 + size]
        if kind in (b'sRGB', b'gAMA', b'cHRM', b'pHYs', b'sBIT'):
            chunks.add(kind, payload)
        offset += size + 12
    with Image.open(BytesIO(data)) as original:
        if getattr(original, 'n_frames', 1) != 1:
            return data  # Leave animated PNG semantics untouched.
        original.load()
        image = original
        if original.mode == 'RGBA' and original.getextrema()[-1] == (255, 255):
            image = original.convert('RGB')
        output = BytesIO()
        options = dict(format='PNG', optimize=True, compress_level=9, pnginfo=chunks)
        for key in ('icc_profile', 'exif', 'transparency'):
            if key in original.info:
                options[key] = original.info[key]
        image.save(output, **options)
        encoded = output.getvalue()
        if len(encoded) >= len(data):
            return data
        with Image.open(BytesIO(encoded)) as check:
            if check.size != original.size or check.convert('RGBA').tobytes() != original.convert('RGBA').tobytes():
                raise ValueError('PNG pixel identity check failed')
        return encoded


if __name__ == '__main__':
    import argparse
    import json
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('root', type=Path)
    parser.add_argument('--report', type=Path, required=True)
    args = parser.parse_args()
    root = args.root.resolve(strict=True)
    changes = []
    for path in sorted(root.rglob('*.png')):
        if path.stat().st_size < 128 * 1024:
            continue
        path.resolve(strict=True).relative_to(root)
        data = path.read_bytes()
        result = optimized_png(data)
        if len(result) < len(data):
            path.write_bytes(result)
            changes.append(dict(path=str(path), before=len(data), after=len(result), identical_rgba=True))
            print(f'{path.name}: {len(data)} -> {len(result)}', flush=True)
    args.report.write_text(json.dumps(changes, indent=2, ensure_ascii=False), encoding='utf-8')
