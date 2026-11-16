"""生成 Espetro UI 用的颗粒纹理（256x256 RGBA，白色、极低 alpha）。

alpha 在 0..A_MAX 之间均匀随机，叠加后形成细腻的胶片颗粒。
"""
import os
import random

from PIL import Image

A_MAX = 34
SIZE = 256
OUT = os.path.join(
    os.path.dirname(os.path.abspath(__file__)),
    "..", "src", "main", "resources", "assets", "espetro", "textures", "gui", "ui", "noise.png",
)

random.seed(20261019)
img = Image.new("RGBA", (SIZE, SIZE))
px = img.load()
for y in range(SIZE):
    for x in range(SIZE):
        # 三角分布：多数像素很淡，少量稍亮，避免整体发灰。
        a = int(abs(random.triangular(0, A_MAX, 0)))
        v = 255 if random.random() < 0.5 else 210
        px[x, y] = (v, v, v, a)
os.makedirs(os.path.dirname(OUT), exist_ok=True)
img.save(OUT, optimize=True)
print("wrote", os.path.normpath(OUT), os.path.getsize(OUT), "bytes")
