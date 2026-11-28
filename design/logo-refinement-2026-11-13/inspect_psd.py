from pathlib import Path
from struct import unpack_from
import json

data = Path('C:/Users/Administrator/Desktop/stmlogo.psd').read_bytes()
pos = 26
for section in range(2):
    n = unpack_from('>I', data, pos)[0]
    pos += 4 + n
layer_section = unpack_from('>I', data, pos)[0]
pos += 4
layer_info = unpack_from('>I', data, pos)[0]
pos += 4
count = abs(unpack_from('>h', data, pos)[0])
pos += 2
layers = []
for i in range(count):
    bounds = unpack_from('>iiii', data, pos)
    pos += 16
    nc = unpack_from('>H', data, pos)[0]
    pos += 2
    channels = []
    for c in range(nc):
        cid, length = unpack_from('>hI', data, pos)
        channels.append((cid, length))
        pos += 6
    blend = data[pos:pos+8].decode('latin1')
    opacity, clipping, flags = data[pos+8:pos+11]
    pos += 12
    extra = unpack_from('>I', data, pos)[0]
    pos += 4
    end = pos+extra
    for skip in range(2):
        n = unpack_from('>I', data, pos)[0]
        pos += 4+n
    nl = data[pos]
    name = data[pos+1:pos+1+nl].decode('utf-8', errors='replace')
    pos += ((nl+1+3)//4)*4
    tags = []
    blocks = {}
    while pos+12 <= end:
        sig = data[pos:pos+4]
        key = data[pos+4:pos+8].decode('latin1')
        length = unpack_from('>I', data, pos+8)[0]
        block = data[pos+12:pos+12+length]
        if key == 'luni':
            name = block[4:].decode('utf-16-be')
        tags.append((key, length))
        blocks[key] = block.hex()
        pos += 12+length+(length%2)
    layers.append(dict(name=name,bounds=bounds,blend=blend,opacity=opacity,flags=flags,channels=channels,tags=tags,blocks=blocks))
    pos=end
print('LAYERS',count)
for layer in layers:
    print({k:v for k,v in layer.items() if k!='blocks'})
Path(__file__).with_name('psd-layers.json').write_text(json.dumps(layers,ensure_ascii=False,indent=2),encoding='utf-8')
