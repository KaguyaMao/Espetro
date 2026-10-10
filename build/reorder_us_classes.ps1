param(
  [Parameter(Mandatory = $true)]
  [string]$Path,
  [Parameter(Mandatory = $true)]
  [string[]]$Order
)

$raw = [IO.File]::ReadAllText($Path, [Text.UTF8Encoding]::new($false))
$classesMatch = [regex]::Match($raw, '"classes"\s*:\s*\{')
if (-not $classesMatch.Success) {
  throw "classes object not found: $Path"
}

$classesOpen = $classesMatch.Index + $classesMatch.Value.LastIndexOf('{')
$depth = 1
$inString = $false
$escaped = $false
$classesClose = -1
for ($i = $classesOpen + 1; $i -lt $raw.Length; $i++) {
  $ch = $raw[$i]
  if ($inString) {
    if ($escaped) {
      $escaped = $false
    } elseif ($ch -eq '\') {
      $escaped = $true
    } elseif ($ch -eq '"') {
      $inString = $false
    }
    continue
  }
  if ($ch -eq '"') {
    $inString = $true
  } elseif ($ch -eq '{') {
    $depth++
  } elseif ($ch -eq '}') {
    $depth--
    if ($depth -eq 0) {
      $classesClose = $i
      break
    }
  }
}
if ($classesClose -lt 0) {
  throw "classes object is not closed: $Path"
}

$contentOffset = $classesOpen + 1
$content = $raw.Substring($contentOffset, $classesClose - $contentOffset)
$matches = [regex]::Matches($content, '(?m)^    "([A-Z0-9_]+)"\s*:\s*\{')
$blocks = [ordered]@{}
foreach ($match in $matches) {
  $id = $match.Groups[1].Value
  $blockStart = $match.Index
  $valueOpen = $match.Index + $match.Value.LastIndexOf('{')
  $valueDepth = 1
  $valueInString = $false
  $valueEscaped = $false
  $valueClose = -1
  for ($i = $valueOpen + 1; $i -lt $content.Length; $i++) {
    $ch = $content[$i]
    if ($valueInString) {
      if ($valueEscaped) {
        $valueEscaped = $false
      } elseif ($ch -eq '\') {
        $valueEscaped = $true
      } elseif ($ch -eq '"') {
        $valueInString = $false
      }
      continue
    }
    if ($ch -eq '"') {
      $valueInString = $true
    } elseif ($ch -eq '{') {
      $valueDepth++
    } elseif ($ch -eq '}') {
      $valueDepth--
      if ($valueDepth -eq 0) {
        $valueClose = $i
        break
      }
    }
  }
  if ($valueClose -lt 0) {
    throw "class object is not closed: $id"
  }
  $blocks[$id] = $content.Substring($blockStart, $valueClose - $blockStart + 1)
}

if ($blocks.Count -ne $Order.Count) {
  throw "class count mismatch: parsed=$($blocks.Count), requested=$($Order.Count)"
}
foreach ($id in $Order) {
  if (-not $blocks.Contains($id)) {
    throw "requested class is missing: $id"
  }
}

$newline = if ($raw.Contains("`r`n")) { "`r`n" } else { "`n" }
$orderedBlocks = foreach ($id in $Order) { $blocks[$id] }
$newContent = $newline + ($orderedBlocks -join (',' + $newline)) + $newline + '  '
$updated = $raw.Substring(0, $contentOffset) + $newContent + $raw.Substring($classesClose)
[IO.File]::WriteAllText($Path, $updated, [Text.UTF8Encoding]::new($false))
