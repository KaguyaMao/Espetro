"""校验生成的载具车速工作簿：结构、类型、数值与 CSV 是否一致。"""

import csv
import os
import sys

from openpyxl import load_workbook

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CSV_PATH = os.path.join(ROOT, "docs", "vehicle-speed-report-kubejs.csv")
XLSX_PATH = os.environ.get("VEHICLE_XLSX") or os.path.join(ROOT, "docs", "vehicle-speed-kubejs.xlsx")

problems = []


def check(cond, msg):
    if not cond:
        problems.append(msg)


wb = load_workbook(XLSX_PATH)
check(wb.sheetnames == ["汇总", "载具明细", "档位分组", "换算说明"], f"sheet 名异常: {wb.sheetnames}")

ws = wb["载具明细"]
header = [c.value for c in ws[1]]
expected_head = ["载具", "命名空间:ID", "引擎类型", "Increment", "Decrement", "前进档 Rf", "倒车档 Rb",
                 "转向", "极速 km/h", "冲刺 km/h", "倒车极速 km/h", "0-1s 加速 m/s²", "油门到底 s",
                 "达 95% s", "Speed", "SpeedRate", "覆盖来源"]
check(header == expected_head, f"表头不符: {header}")

data_rows = list(ws.iter_rows(min_row=2, values_only=True))
check(len(data_rows) == 60, f"明细行数 {len(data_rows)} != 60")

with open(CSV_PATH, encoding="utf-8-sig", newline="") as fh:
    csv_rows = list(csv.DictReader(fh))
check(len(csv_rows) == 60, f"CSV 行数 {len(csv_rows)} != 60")

csv_by_id = {f'{r["命名空间"]}:{r["ID"]}': r for r in csv_rows}
name_only = 0
for row in data_rows:
    nsid = row[1]
    check(nsid in csv_by_id, f"明细里的 {nsid} 不在 CSV 中")
    src = csv_by_id.get(nsid)
    if src is None:
        continue
    csv_top = src["极速(km/h)"]
    xlsx_top = row[8]
    if csv_top in ("", None):
        check(xlsx_top is None, f"{nsid} 极速应空却为 {xlsx_top}")
    else:
        check(xlsx_top is not None and abs(float(csv_top) - float(xlsx_top)) < 1e-6,
              f"{nsid} 极速不一致 csv={csv_top} xlsx={xlsx_top}")
    if row[0] == row[1].split(":")[1]:
        name_only += 1

check(name_only == 0, f"有 {name_only} 台载具没有中文名（回落成 ID）")

# 类型单元格必须是文本而不是数字
bad_types = [r for r in data_rows if not isinstance(r[2], str)]
check(not bad_types, f"{len(bad_types)} 行引擎类型不是文本")

# 汇总表合计行
sum_ws = wb["汇总"]
totals = [r for r in sum_ws.iter_rows(values_only=True) if r and r[0] == "合计"]
check(totals and totals[0][1] == 60, f"汇总合计行异常: {totals}")

# 档位分组：每行台数之和应等于明细里可换算台数
grp_ws = wb["档位分组"]
counts = [r[10] for r in grp_ws.iter_rows(min_row=3, values_only=True) if isinstance(r[10], int)]
detail_convertible = sum(1 for r in data_rows if r[8] is not None)
check(sum(counts) == detail_convertible,
      f"档位分组台数之和 {sum(counts)} != 可换算明细 {detail_convertible}")

print(f"明细 {len(data_rows)} 行，可换算 {detail_convertible} 台，档位分组 {len(counts)} 组（合计 {sum(counts)} 台）")
if problems:
    print("发现问题：")
    for p in problems:
        print(" -", p)
    sys.exit(1)
print("校验通过")
