// patch-party-assign.mjs — 修复组队分配：计数 bug + 先补散人再判平衡 + 成员过滤
import fs from 'node:fs';
const F = 'src/main/java/org/espetro/team/PartyManager.java';
let text = fs.readFileSync(F, 'utf8');
const eol = text.includes('\r\n') ? '\r\n' : '\n';
const lines = text.split(/\r?\n/);

// 定位方法（从签名到配对的右花括号）
const start = lines.findIndex((l) => /public Map<UUID, String> computeTeamAssignment\(/.test(l));
if (start < 0) { console.error('❌ 未找到 computeTeamAssignment'); process.exit(1); }
let depth = 0, end = -1;
for (let i = start; i < lines.length; i++) {
  for (const ch of lines[i]) {
    if (ch === '{') depth++;
    else if (ch === '}') { depth--; if (depth === 0) { end = i; break; } }
  }
  if (end >= 0) break;
}
if (end < 0) { console.error('❌ 未找到方法结尾'); process.exit(1); }
console.log(`方法范围: ${start + 1} - ${end + 1} 行（共 ${end - start + 1} 行）`);

const body = [
  '    public Map<UUID, String> computeTeamAssignment(List<ServerPlayer> allPlayers) {',
  '        Map<UUID, String> result = new LinkedHashMap<>();',
  '        List<UUID> unassigned = new ArrayList<>();',
  '',
  '        // 只统计本局实际参与的玩家，避免离线/观战成员混进队内计数。',
  '        Set<UUID> participating = new HashSet<>();',
  '        for (ServerPlayer p : allPlayers) {',
  '            participating.add(p.getUUID());',
  '        }',
  '',
  '        // 按队伍分组',
  '        Map<UUID, List<UUID>> partyGroups = new LinkedHashMap<>();',
  '        Set<UUID> handled = new HashSet<>();',
  '        for (ServerPlayer p : allPlayers) {',
  '            UUID uid = p.getUUID();',
  '            if (handled.contains(uid)) continue;',
  '            PartyData party = getPartyByMember(uid);',
  '            if (party != null) {',
  '                List<UUID> members = new ArrayList<>();',
  '                for (UUID m : party.members) {',
  '                    if (participating.contains(m)) members.add(m);',
  '                }',
  '                if (members.isEmpty()) {',
  '                    unassigned.add(uid);',
  '                    handled.add(uid);',
  '                    continue;',
  '                }',
  '                partyGroups.computeIfAbsent(party.partyId, k -> new ArrayList<>())',
  '                    .addAll(members);',
  '                handled.addAll(members);',
  '            } else {',
  '                unassigned.add(uid);',
  '                handled.add(uid);',
  '            }',
  '        }',
  '',
  '        int attack = 0, defend = 0;',
  '        // 按队伍大小降序排列；整队一起入队，保证同队玩家在同一阵营。',
  '        List<List<UUID>> sortedGroups = new ArrayList<>(partyGroups.values());',
  '        sortedGroups.sort((a, b) -> Integer.compare(b.size(), a.size()));',
  '',
  '        for (List<UUID> group : sortedGroups) {',
  '            if (attack <= defend) {',
  '                for (UUID uid : group) result.put(uid, "ATTACK");',
  '                attack += group.size();',
  '            } else {',
  '                for (UUID uid : group) result.put(uid, "DEFEND");',
  '                defend += group.size();',
  '            }',
  '        }',
  '',
  '        // 先补齐"无组队玩家"，拿到真实的双方人数，之后再判断是否需要拆队。',
  '        // （原先在补齐之前就判平衡，且计数器在 result.remove 之后读取导致永不递减，',
  '        //   于是只要初始人数差 >2 就会把全部组队逐个拆散——同队玩家因此被分到两边。）',
  '        for (UUID uid : unassigned) {',
  '            if (attack <= defend) {',
  '                result.put(uid, "ATTACK");',
  '                attack++;',
  '            } else {',
  '                result.put(uid, "DEFEND");',
  '                defend++;',
  '            }',
  '        }',
  '        unassigned.clear();',
  '',
  '        // 平衡检查：真实差距 >2 时，才拆散"人多一方里最大的那支队伍"，其余整队保留。',
  '        while (Math.abs(attack - defend) > 2 && !partyGroups.isEmpty()) {',
  '            String overTeam = attack > defend ? "ATTACK" : "DEFEND";',
  '            PartyData largestParty = null;',
  '            for (UUID pid : partyGroups.keySet()) {',
  '                PartyData p = partiesByOwner.get(pid);',
  '                if (p == null) continue;',
  '                boolean onOverTeam = false;',
  '                for (UUID m : p.members) {',
  '                    if (overTeam.equals(result.get(m))) { onOverTeam = true; break; }',
  '                }',
  '                if (!onOverTeam) continue;',
  '                if (largestParty == null || p.members.size() > largestParty.members.size()) {',
  '                    largestParty = p;',
  '                }',
  '            }',
  '            if (largestParty == null) break;',
  '',
  '            // 拆散该队伍：必须先取回原阵营再删除，否则计数不会递减（原 bug）。',
  '            for (UUID m : largestParty.members) {',
  '                String prev = result.remove(m);',
  '                if (prev == null) continue;',
  '                unassigned.add(m);',
  '                if ("ATTACK".equals(prev)) attack--;',
  '                else defend--;',
  '            }',
  '            partyGroups.remove(largestParty.partyId);',
  '        }',
  '',
  '        // 被拆散的成员按人数平衡逐个补齐',
  '        for (UUID uid : unassigned) {',
  '            if (attack <= defend) {',
  '                result.put(uid, "ATTACK");',
  '                attack++;',
  '            } else {',
  '                result.put(uid, "DEFEND");',
  '                defend++;',
  '            }',
  '        }',
  '',
  '        return result;',
  '    }',
];
const out = [...lines.slice(0, start), ...body, ...lines.slice(end + 1)].join(eol);
fs.writeFileSync(F, out, 'utf8');
console.log('✓ 已替换 computeTeamAssignment');
const check = fs.readFileSync(F, 'utf8').split(/\r?\n/);
const s2 = check.findIndex((l) => /public Map<UUID, String> computeTeamAssignment\(/.test(l));
console.log('=== 新方法（前 12 行） ===');
for (let i = s2; i < s2 + 12; i++) console.log((i + 1) + ': ' + check[i]);
