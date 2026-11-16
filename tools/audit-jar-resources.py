"""Inventory a release jar without extracting or modifying it."""
import argparse
from collections import defaultdict
import hashlib
import json
from pathlib import Path
import re
import zipfile


def audit(jar):
    groups = defaultdict(lambda: [0, 0, 0])
    hashes = defaultdict(list)
    suspicious = []
    with zipfile.ZipFile(jar) as archive:
        entries = [entry for entry in archive.infolist() if not entry.is_dir()]
        for entry in entries:
            prefix = '/'.join(entry.filename.split('/')[:4])
            stat = groups[prefix]
            stat[0] += 1
            stat[1] += entry.file_size
            stat[2] += entry.compress_size
            if re.search(r'(?i)(backup|\.bak(?:[-.]|$)|\.old$|\.orig$|\.psd$|\.blend$|\.xcf$|\.zip$|\.log$|\.tmp$|\.DS_Store$|Thumbs\.db$|~$)', entry.filename):
                suspicious.append([entry.filename, entry.file_size, entry.compress_size])
            if entry.file_size > 1024:
                hashes[hashlib.sha256(archive.read(entry)).hexdigest()].append(
                    [entry.filename, entry.file_size, entry.compress_size])
        return dict(jar=str(jar), bytes=Path(jar).stat().st_size, entries=len(entries),
                    uncompressed=sum(e.file_size for e in entries),
                    largest=[[e.filename, e.file_size, e.compress_size] for e in
                             sorted(entries, key=lambda e: e.compress_size, reverse=True)[:45]],
                    groups=sorted([[key, *value] for key, value in groups.items()],
                                  key=lambda row: row[3], reverse=True),
                    suspicious=suspicious,
                    duplicates=[value for value in hashes.values() if len(value) > 1])


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('jar', type=Path)
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args()
    result = audit(args.jar)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, indent=2, ensure_ascii=False), encoding='utf-8')
    print(json.dumps({key: result[key] for key in ('bytes', 'entries', 'largest', 'suspicious')}, ensure_ascii=False))
