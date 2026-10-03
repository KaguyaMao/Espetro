$ids = Get-Content (Join-Path $env:TEMP 'codex_tacz_ids.txt')
$ids += Get-Content (Join-Path $env:TEMP 'codex_tacz_ammo_ids.txt')
$baseItems = @('tacz:modern_kinetic_gun','tacz:ammo','taczmagazines:magazine')
$json = Get-Content -Raw -Encoding UTF8 'C:\Users\Administrator\Desktop\RU_test.json' | ConvertFrom-Json
$missing = @()
foreach ($c in $json.classes.PSObject.Properties) {
    foreach ($v in $c.Value.variants.PSObject.Properties) {
        foreach ($cmd in $v.Value.commands) {
            if ($cmd -match 'GunId:"([^"]+)"') { $g = $Matches[1]; if ($ids -notcontains $g) { $missing += "GUN $($c.Name)/$($v.Name): $g" } }
            if ($cmd -match 'AttachmentId:"([^"]+)"') { $a = $Matches[1]; if ($ids -notcontains $a) { $missing += "ATT $($c.Name)/$($v.Name): $a" } }
            if ($cmd -match 'AmmoId:"([^"]+)"') { $am = $Matches[1]; if ($ids -notcontains $am) { $missing += "AMMO $($c.Name)/$($v.Name): $am" } }
            if ($cmd -match '^([a-z0-9_]+:[a-z0-9_]+)\{') { $mi = $Matches[1]; if ($ids -notcontains $mi -and $baseItems -notcontains $mi -and $mi -notmatch '^minecraft:') { $missing += "ITEM $($c.Name)/$($v.Name): $mi" } }
        }
        if ($v.Value.resupply -and $v.Value.resupply.items) {
            foreach ($it in $v.Value.resupply.items) {
                if ($it.id -match 'AmmoId:"([^"]+)"') { $am = $Matches[1]; if ($ids -notcontains $am) { $missing += "AMMO-R $($c.Name)/$($v.Name): $am" } }
                if ($it.id -match '^([a-z0-9_]+:[a-z0-9_]+)\{') { $mi = $Matches[1]; if ($ids -notcontains $mi -and $baseItems -notcontains $mi -and $mi -notmatch '^minecraft:') { $missing += "ITEM-R $($c.Name)/$($v.Name): $mi" } }
            }
        }
    }
}
$missing | Sort-Object -Unique
