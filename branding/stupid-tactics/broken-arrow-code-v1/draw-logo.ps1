param(
    [string]$OutputDirectory = $PSScriptRoot
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$culture = [System.Globalization.CultureInfo]::InvariantCulture
$utf8 = [System.Text.UTF8Encoding]::new($false)

function Format-Coordinate([double]$Value) {
    return $Value.ToString('0.###', $culture)
}

function Convert-OutlineToSvg([System.Drawing.Drawing2D.GraphicsPath]$Outline) {
    $points = $Outline.PathPoints
    $types = $Outline.PathTypes
    $commands = [System.Text.StringBuilder]::new()
    $i = 0
    while ($i -lt $points.Length) {
        $kind = $types[$i] -band 7
        $point = $points[$i]
        if ($kind -eq 0) {
            [void]$commands.Append("M$(Format-Coordinate $point.X),$(Format-Coordinate $point.Y)")
        } elseif ($kind -eq 1) {
            [void]$commands.Append("L$(Format-Coordinate $point.X),$(Format-Coordinate $point.Y)")
        } elseif ($kind -eq 3) {
            $control2 = $points[$i + 1]
            $end = $points[$i + 2]
            [void]$commands.Append("C$(Format-Coordinate $point.X),$(Format-Coordinate $point.Y) $(Format-Coordinate $control2.X),$(Format-Coordinate $control2.Y) $(Format-Coordinate $end.X),$(Format-Coordinate $end.Y)")
            $i += 2
        } else {
            throw "Unexpected GDI+ point type: $kind"
        }
        if (($types[$i] -band 128) -ne 0) {
            [void]$commands.Append('Z')
        }
        $i++
    }
    return $commands.ToString()
}

function New-LetteringPath {
    param(
        [string]$Text,
        [string]$Family,
        [System.Drawing.FontStyle]$Style,
        [double]$X,
        [double]$Y,
        [double]$Width,
        [double]$Height
    )
    $fontFamily = [System.Drawing.FontFamily]::new($Family)
    $outline = [System.Drawing.Drawing2D.GraphicsPath]::new()
    try {
        $outline.AddString($Text, $fontFamily, [int]$Style, [single]1000,
            [System.Drawing.PointF]::new(0, 0), [System.Drawing.StringFormat]::GenericTypographic)
        $bounds = $outline.GetBounds()
        $scaleX = $Width / $bounds.Width
        $scaleY = $Height / $bounds.Height
        $matrix = [System.Drawing.Drawing2D.Matrix]::new(
            [single]$scaleX, [single]0, [single]0, [single]$scaleY,
            [single]($X - $bounds.X * $scaleX), [single]($Y - $bounds.Y * $scaleY))
        try { $outline.Transform($matrix) } finally { $matrix.Dispose() }
        return Convert-OutlineToSvg $outline
    } finally {
        $outline.Dispose()
        $fontFamily.Dispose()
    }
}

function New-TrackedLettering {
    param(
        [string]$Text,
        [string]$Family,
        [System.Drawing.FontStyle]$Style,
        [double]$CenterX,
        [double]$Y,
        [double]$Height,
        [double]$Tracking
    )
    $fontFamily = [System.Drawing.FontFamily]::new($Family)
    $outlines = [System.Collections.Generic.List[System.Drawing.Drawing2D.GraphicsPath]]::new()
    $widths = [System.Collections.Generic.List[double]]::new()
    try {
        foreach ($character in $Text.ToCharArray()) {
            $glyph = [System.Drawing.Drawing2D.GraphicsPath]::new()
            $glyph.AddString([string]$character, $fontFamily, [int]$Style, [single]1000,
                [System.Drawing.PointF]::new(0, 0), [System.Drawing.StringFormat]::GenericTypographic)
            $bounds = $glyph.GetBounds()
            $scale = $Height / $bounds.Height
            $outlines.Add($glyph)
            $widths.Add($bounds.Width * $scale)
        }
        $total = ($widths | Measure-Object -Sum).Sum + ($outlines.Count - 1) * $Tracking
        $cursor = $CenterX - $total / 2
        $result = [System.Text.StringBuilder]::new()
        for ($i = 0; $i -lt $outlines.Count; $i++) {
            $glyph = $outlines[$i]
            $bounds = $glyph.GetBounds()
            $scale = $Height / $bounds.Height
            $matrix = [System.Drawing.Drawing2D.Matrix]::new(
                [single]$scale, [single]0, [single]0, [single]$scale,
                [single]($cursor - $bounds.X * $scale), [single]($Y - $bounds.Y * $scale))
            try { $glyph.Transform($matrix) } finally { $matrix.Dispose() }
            [void]$result.Append((Convert-OutlineToSvg $glyph))
            $cursor += $widths[$i] + $Tracking
        }
        return $result.ToString()
    } finally {
        foreach ($glyph in $outlines) { $glyph.Dispose() }
        $fontFamily.Dispose()
    }
}

function New-LogoSvg([string]$Ink, [bool]$CutMarks = $true) {
    $upper = New-LetteringPath -Text 'STUPID' -Family 'Bahnschrift' -Style Regular -X 80 -Y 70 -Width 1240 -Height 166
    $lower = New-LetteringPath -Text 'TACTICS' -Family 'Impact' -Style Regular -X 80 -Y 256 -Width 1240 -Height 230
    $chinese = New-TrackedLettering -Text '胡闹战场' -Family 'DengXian' -Style Regular -CenterX 700 -Y 550 -Height 68 -Tracking 18
    $minecraft = New-TrackedLettering -Text 'MINECRAFT' -Family 'Arial' -Style Bold -CenterX 700 -Y 659 -Height 25 -Tracking 8
    $mask = if ($CutMarks) { 'mask="url(#stencil-cuts)"' } else { '' }
    return @"
<svg xmlns="http://www.w3.org/2000/svg" width="1400" height="760" viewBox="0 0 1400 760" role="img" aria-labelledby="title desc">
  <title id="title">Stupid Tactics — 胡闹战场 — Minecraft</title>
  <desc id="desc">Code-drawn monochrome military wordmark. All lettering is editable vector outlines; three small cuts are defined by a separate SVG mask.</desc>
  <defs>
    <mask id="stencil-cuts" maskUnits="userSpaceOnUse" x="0" y="0" width="1400" height="760">
      <rect width="1400" height="760" fill="white"/>
      <path d="M78 254H132L110 280H78Z" fill="black"/>
      <path d="M541 467L568 458L561 486H537Z" fill="black"/>
      <path d="M1285 478L1322 461V489H1295Z" fill="black"/>
    </mask>
  </defs>
  <g fill="$Ink" fill-rule="nonzero">
    <path id="stupid" d="$upper"/>
    <path id="tactics" $mask d="$lower"/>
    <path id="chinese-name" d="$chinese"/>
    <path id="minecraft-subtitle" opacity="0.7" d="$minecraft"/>
  </g>
</svg>
"@
}

function New-PreviewSvg([string]$LogoSvg) {
    $inner = [regex]::Replace($LogoSvg, '(?s)^.*?<svg[^>]*>|</svg>\s*$', '')
    return @"
<svg xmlns="http://www.w3.org/2000/svg" width="1600" height="1000" viewBox="0 0 1600 1000">
  <defs>
    <radialGradient id="preview-bg" cx="86%" cy="27%" r="100%">
      <stop offset="0" stop-color="#222824"/>
      <stop offset="0.58" stop-color="#111613"/>
      <stop offset="1" stop-color="#080D0B"/>
    </radialGradient>
  </defs>
  <rect width="1600" height="1000" fill="url(#preview-bg)"/>
  <g transform="translate(100 120)">$inner</g>
</svg>
"@
}

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$primary = New-LogoSvg '#E8EDE2'
$darkInk = New-LogoSvg '#131A16'
$small = New-LogoSvg '#E8EDE2' $false
[System.IO.File]::WriteAllText((Join-Path $OutputDirectory 'stupid-tactics-hunaobattlefield.svg'), $primary, $utf8)
[System.IO.File]::WriteAllText((Join-Path $OutputDirectory 'stupid-tactics-hunaobattlefield-dark-ink.svg'), $darkInk, $utf8)
[System.IO.File]::WriteAllText((Join-Path $OutputDirectory 'stupid-tactics-small.svg'), $small, $utf8)
[System.IO.File]::WriteAllText((Join-Path $OutputDirectory 'preview.svg'), (New-PreviewSvg $primary), $utf8)
Write-Output "Wrote outlined SVG assets to $OutputDirectory"
