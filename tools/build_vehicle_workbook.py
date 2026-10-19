"""把 kubejs/data 覆盖的那批载具动力数据写成 Excel 工作簿。

输入：docs/vehicle-speed-report-kubejs.csv（由 tools/vehicle-speed-report.mjs --kubejs-only 生成）
输出：docs/vehicle-speed-kubejs.xlsx

工作表：
  1. 汇总          —— 按引擎类型的统计
  2. 载具明细      —— 全量 60 台，含原始字段与换算结果
  3. 档位分组      —— 同数值合并，便于横向比较
  4. 换算说明      —— 公式、系数、口径
"""

import csv
import os
import re

from openpyxl import Workbook
from openpyxl.styles import Alignment, Border, Font, PatternFill, Side
from openpyxl.utils import get_column_letter
from openpyxl.worksheet.table import Table, TableStyleInfo

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CSV_PATH = os.path.join(ROOT, "docs", "vehicle-speed-report-kubejs.csv")
OUT_PATH = os.environ.get("VEHICLE_XLSX") or os.path.join(ROOT, "docs", "vehicle-speed-kubejs.xlsx")

# lang 文件里没有条目的载具，手工补名字
EXTRA_NAMES = {
    "fcp:humveem2": "悍马 M2",
    "fcp:matv_9in1": "MATV 9合1",
    "dragonrise_reforge:sx1_a": "SX-1A（局部补丁）",
}

# 不参与换算的原因
NO_SPEED_REASON = {
    "Fixed": "固定武器，不能开动",
    "Aircraft": "飞行载具，速度由 Speed/SpeedRate 与飞行阻力曲线决定，不是地面速度",
    "Helicopter": "直升机，同上",
    "Tom6": "定点发射器",
    "无引擎字段": "没有 EngineInfo（部分覆盖文件是局部补丁）",
}

# ---------------- 样式 ----------------
HEADER_FILL = PatternFill("solid", fgColor="1F2933")
HEADER_FONT = Font(name="Microsoft YaHei", size=10, bold=True, color="FFFFFF")
BODY_FONT = Font(name="Microsoft YaHei", size=10)
NOTE_FONT = Font(name="Microsoft YaHei", size=9, color="55606B")
TITLE_FONT = Font(name="Microsoft YaHei", size=14, bold=True, color="1F2933")
BAND_FILL = PatternFill("solid", fgColor="F2F4F6")
THIN = Side(style="thin", color="D5D9DD")
BORDER = Border(left=THIN, right=THIN, top=THIN, bottom=THIN)
CENTER = Alignment(horizontal="center", vertical="center")
LEFT = Alignment(horizontal="left", vertical="center")


def load_rows():
    with open(CSV_PATH, "r", encoding="utf-8-sig", newline="") as fh:
        return list(csv.reader(fh))


def num(value):
    if value in ("", None):
        return None
    try:
        return float(value)
    except ValueError:
        return value


def build():
    raw = load_rows()
    header, body = raw[0], raw[1:]
    idx = {name: i for i, name in enumerate(header)}

    rows = []
    for r in body:
        ns = r[idx["命名空间"]]
        vid = r[idx["ID"]]
        display = r[idx["中文名"]] or EXTRA_NAMES.get(f"{ns}:{vid}") or vid
        rows.append(
            {
                "ns": ns,
                "id": vid,
                "name": display,
                "type": r[idx["引擎类型"]] or "无引擎字段",
                "inc": num(r[idx["Increment"]]),
                "dec": num(r[idx["Decrement"]]),
                "rf": num(r[idx["MaxForwardSpeedRate"]]),
                "rb": num(r[idx["MaxBackwardSpeedRate"]]),
                "steer": num(r[idx["SteeringSpeed"]]),
                "speed": num(r[idx["Speed"]]),
                "speed_rate": num(r[idx["SpeedRate"]]),
                "top_bps": num(r[idx["极速(b/s)"]]),
                "top": num(r[idx["极速(km/h)"]]),
                "sprint": num(r[idx["冲刺极速(km/h)"]]),
                "reverse": num(r[idx["倒车极速(km/h)"]]),
                "accel": num(r[idx["0-1s加速度(m/s²)"]]),
                "throttle": num(r[idx["油门到底(s)"]]),
                "t95": num(r[idx["达95%极速(s)"]]),
                "src": r[idx["覆盖来源"]],
            }
        )

    typerank = {"Track": 0, "Wheel": 1, "WheelChair": 2, "Ship": 3, "AirShip": 4,
                "Helicopter": 5, "Aircraft": 6, "Tom6": 7, "Fixed": 8}
    rows.sort(key=lambda x: (typerank.get(x["type"], 9), -(x["top"] or 0), x["id"]))

    wb = Workbook()

    # ---------------- Sheet 1：汇总 ----------------
    ws = wb.active
    ws.title = "汇总"
    ws["A1"] = "服务端 kubejs 载具动力参数汇总"
    ws["A1"].font = TITLE_FONT
    ws["A2"] = "数据来源：kubejs/data/<ns>/sbw/vehicles/*.json（60 份覆盖）+ 世界数据包 sbw_shotpos"
    ws["A2"].font = NOTE_FONT
    ws["A3"] = "换算依据：Superb Warfare VehicleEngineUtils.kt（平地直行、满油门、无伤）；1 block/tick = 72 km/h"
    ws["A3"].font = NOTE_FONT

    ws.append([])
    head = ["引擎类型", "台数", "可换算", "极速最低", "极速最高", "极速中位", "0-1s 加速范围 m/s²", "油门到底范围 s"]
    ws.append(head)
    header_row = ws.max_row
    for c in range(1, len(head) + 1):
        cell = ws.cell(row=header_row, column=c)
        cell.fill = HEADER_FILL
        cell.font = HEADER_FONT
        cell.alignment = CENTER
        cell.border = BORDER

    order = ["Track", "Wheel", "WheelChair", "Ship", "AirShip", "Helicopter", "Aircraft", "Tom6", "Fixed", "无引擎字段"]
    for t in order:
        group = [r for r in rows if r["type"] == t]
        if not group:
            continue
        tops = sorted(r["top"] for r in group if r["top"])
        accs = sorted(r["accel"] for r in group if r["accel"] is not None)
        throttles = sorted(r["throttle"] for r in group if r["throttle"] is not None)
        median = tops[len(tops) // 2] if tops else None
        ws.append([
            t,
            len(group),
            len(tops),
            min(tops) if tops else "—",
            max(tops) if tops else "—",
            median if median is not None else "—",
            f"{min(accs):.2f} ~ {max(accs):.2f}" if accs else "—",
            f"{min(throttles):.1f} ~ {max(throttles):.1f}" if throttles else "—",
        ])
    ws.append(["合计", len(rows), sum(1 for r in rows if r["top"]), "", "", "", "", ""])

    for r in range(header_row + 1, ws.max_row + 1):
        for c in range(1, len(head) + 1):
            cell = ws.cell(row=r, column=c)
            cell.font = BODY_FONT
            cell.border = BORDER
            cell.alignment = LEFT if c == 1 else CENTER
        ws.cell(row=r, column=4).number_format = "0.0"
        ws.cell(row=r, column=5).number_format = "0.0"
        ws.cell(row=r, column=6).number_format = "0.0"
    for r in range(header_row + 1, ws.max_row + 1):
        if (r - header_row) % 2 == 0:
            for c in range(1, len(head) + 1):
                ws.cell(row=r, column=c).fill = BAND_FILL
    for c, w in zip(range(1, 9), [16, 8, 9, 11, 11, 11, 18, 18]):
        ws.column_dimensions[get_column_letter(c)].width = w

    # ---------------- Sheet 2：载具明细 ----------------
    ws2 = wb.create_sheet("载具明细")
    cols = [
        ("载具", "name", 44, LEFT),
        ("命名空间:ID", "nsid", 36, LEFT),
        ("引擎类型", "type", 12, CENTER),
        ("Increment", "inc", 13, CENTER),
        ("Decrement", "dec", 13, CENTER),
        ("前进档 Rf", "rf", 13, CENTER),
        ("倒车档 Rb", "rb", 13, CENTER),
        ("转向", "steer", 8, CENTER),
        ("极速 km/h", "top", 13, CENTER),
        ("冲刺 km/h", "sprint", 13, CENTER),
        ("倒车极速 km/h", "reverse", 17, CENTER),
        ("0-1s 加速 m/s²", "accel", 16, CENTER),
        ("油门到底 s", "throttle", 13, CENTER),
        ("达 95% s", "t95", 11, CENTER),
        ("Speed", "speed", 9, CENTER),
        ("SpeedRate", "speed_rate", 13, CENTER),
        ("覆盖来源", "src", 12, CENTER),
    ]
    ws2.append([c[0] for c in cols])
    for c in range(1, len(cols) + 1):
        cell = ws2.cell(row=1, column=c)
        cell.fill = HEADER_FILL
        cell.font = HEADER_FONT
        cell.alignment = CENTER
        cell.border = BORDER
    ws2.freeze_panes = "A2"

    for r in rows:
        ws2.append([
            r["name"], f'{r["ns"]}:{r["id"]}', r["type"], r["inc"], r["dec"], r["rf"], r["rb"],
            r["steer"], r["top"], r["sprint"], r["reverse"], r["accel"], r["throttle"], r["t95"],
            r["speed"], r["speed_rate"], r["src"],
        ])
    for row_i in range(2, ws2.max_row + 1):
        for col_i, (_, _, _, align) in enumerate(cols, start=1):
            cell = ws2.cell(row=row_i, column=col_i)
            cell.font = BODY_FONT
            cell.border = BORDER
            cell.alignment = align
        for col_i in (9, 10, 11, 12, 13, 14):
            ws2.cell(row=row_i, column=col_i).number_format = "0.0" if col_i != 12 else "0.00"
        if row_i % 2 == 0:
            for col_i in range(1, len(cols) + 1):
                ws2.cell(row=row_i, column=col_i).fill = BAND_FILL
    for col_i, (_, _, width, _) in enumerate(cols, start=1):
        ws2.column_dimensions[get_column_letter(col_i)].width = width

    table = Table(displayName="VehicleDetail", ref=f"A1:{get_column_letter(len(cols))}{ws2.max_row}")
    table.tableStyleInfo = TableStyleInfo(name="TableStyleLight1", showRowStripes=False)
    ws2.add_table(table)

    # ---------------- Sheet 3：档位分组 ----------------
    ws3 = wb.create_sheet("档位分组")
    ws3["A1"] = "同数值合并（Rf/Rb/Inc/Dec 完全一致为一组）"
    ws3["A1"].font = NOTE_FONT
    head3 = ["引擎类型", "极速 km/h", "冲刺", "倒车", "0-1s m/s²", "油门到底 s", "Rf", "Rb", "Inc", "Dec", "台数", "代表载具"]
    ws3.append([])
    ws3.append(head3)
    hrow3 = ws3.max_row
    for c in range(1, len(head3) + 1):
        cell = ws3.cell(row=hrow3, column=c)
        cell.fill = HEADER_FILL
        cell.font = HEADER_FONT
        cell.alignment = CENTER
        cell.border = BORDER

    for t in ["Track", "Wheel", "WheelChair", "Ship", "AirShip"]:
        group = [r for r in rows if r["type"] == t and r["top"]]
        if not group:
            continue
        buckets = {}
        for r in group:
            key = (r["inc"], r["dec"], r["rf"], r["rb"])
            buckets.setdefault(key, []).append(r)
        ordered = sorted(buckets.values(), key=lambda g: -g[0]["top"])
        for g in ordered:
            first = g[0]
            names = "、".join(x["name"] for x in g[:4])
            if len(g) > 4:
                names += f" 等 {len(g)} 台"
            ws3.append([t, first["top"], first["sprint"], first["reverse"], first["accel"],
                        first["throttle"], first["rf"], first["rb"], first["inc"], first["dec"],
                        len(g), names])
        ws3.append([])

    for row_i in range(hrow3 + 1, ws3.max_row + 1):
        if ws3.cell(row=row_i, column=1).value in (None, ""):
            continue
        for col_i in range(1, len(head3) + 1):
            cell = ws3.cell(row=row_i, column=col_i)
            cell.font = BODY_FONT
            cell.border = BORDER
            cell.alignment = LEFT if col_i in (1, 12) else CENTER
        for col_i, fmt in ((2, "0.0"), (3, "0.0"), (4, "0.0"), (5, "0.00"), (6, "0.00")):
            ws3.cell(row=row_i, column=col_i).number_format = fmt
    for col_i, width in zip(range(1, 13), [12, 12, 10, 10, 14, 13, 9, 9, 9, 9, 8, 54]):
        ws3.column_dimensions[get_column_letter(col_i)].width = width

    # ---------------- Sheet 4：换算说明 ----------------
    ws4 = wb.create_sheet("换算说明")
    ws4["A1"] = "速度与加速度换算说明"
    ws4["A1"].font = TITLE_FONT

    notes = [
        ("", ""),
        ("字段含义", ""),
        ("MaxForwardSpeedRate / MaxBackwardSpeedRate", "目标速度率 R，是速度上限的直接输入，不是 km/h。"),
        ("Increment / Decrement", "油门（power）的爬升 / 回落速率，不是车速加速度。"),
        ("SteeringSpeed", "转向速率（本轮已全部减半）。"),
        ("", ""),
        ("物理公式（平地、直行、满油门、无伤）", ""),
        ("履带 / 轮式", "v ← v × 1.05 × (0.54 + 0.25) = 0.8295v，然后 v += 0.15 × R × power"),
        ("WheelChair", "阻尼 0.924，推力 0.15 × R × power"),
        ("船 Ship（水中）", "f = 0.835 − 0.04 × 浸没 + 0.005；v ← v × 1.04 × f，然后 v += 0.11 × R × power"),
        ("飞艇 AirShip", "阻尼 ≈ 0.99，推力 0.01 × R × power（近似值）"),
        ("冲刺倍率", "履带 1.25 / 轮式 1.3 / 船 1.3"),
        ("", ""),
        ("单位换算", ""),
        ("速度", "1 block/tick = 20 block/s = 72 km/h（1 格 = 1 米）"),
        ("加速度", "1 block/tick² = 400 m/s²"),
        ("极速速算", "履带/轮式 ≈ 63.3 × Rf km/h；WheelChair ≈ 142 × Rf；船 ≈ 53.8 × Rf；飞艇 ≈ 72 × Rf"),
        ("", ""),
        ("列口径", ""),
        ("极速 km/h", "逐 tick 模拟到稳态后的水平速度（取最后 100 tick 平均）"),
        ("冲刺 km/h", "sprintInputDown 时 maxPower 提升后的稳态速度"),
        ("倒车极速 km/h", "Rb 档位对应的稳态速度"),
        ("0-1s 加速 m/s²", "起步 1 秒后的速度增量，相当于该秒内的平均加速度"),
        ("油门到底 s", "power 达到 99% 所需时间"),
        ("达 95% s", "水平速度达到极速 95% 所需时间"),
        ("", ""),
        ("注意", "模拟假设平地、直行、无伤、无漂移；坡度、障碍、受损阻力会让实际值低于表中数值。"),
    ("本轮标定", "极速按 War Thunder 地面载具数据设定；达到极速的时间按动力类型统一：燃气轮机约 14 s、柴油机约 20 s（指达到极速 95%）。"),
    ]

    skipped = [x for x in rows if not x["top"]]
    notes.append(("", ""))
    notes.append((f"未换算的载具（共 {len(skipped)} 台）", ""))
    by_reason = {}
    for x in skipped:
        by_reason.setdefault(x["type"], []).append(x["name"])
    for t in order:
        if t in by_reason:
            notes.append((t, f'{NO_SPEED_REASON.get(t, "")}：' + "、".join(sorted(by_reason[t]))))
    notes.append(("sx1_a 说明", "服务器上的局部补丁文件只写了 UpStep/TurretPos/BarrelPos，没有 EngineInfo；如需可用车辆数据请补全或删掉该文件。"))

    r = 2
    for key, value in notes:
        ws4.cell(row=r, column=1, value=key).font = Font(name="Microsoft YaHei", size=10, bold=bool(value) and not key.startswith(" "))
        body = ws4.cell(row=r, column=2, value=value)
        body.font = BODY_FONT
        body.alignment = Alignment(horizontal="left", vertical="center", wrap_text=True)
        ws4.cell(row=r, column=1).alignment = LEFT
        if value:
            # 中文按 2 个字符宽度估算，避免长列表被右侧截断
            est = sum(2 if ord(ch) > 127 else 1 for ch in str(value))
            lines = max(1, (est + 87) // 88)
            ws4.row_dimensions[r].height = 15.5 * lines + 3
        r += 1
    ws4.column_dimensions["A"].width = 46
    ws4.column_dimensions["B"].width = 92

    wb.save(OUT_PATH)
    print(f"已写入 {OUT_PATH}")
    print(f"明细 {len(rows)} 行；工作表：{wb.sheetnames}")


if __name__ == "__main__":
    build()
