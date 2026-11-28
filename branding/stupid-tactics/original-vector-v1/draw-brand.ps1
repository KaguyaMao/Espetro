param([string]$OutputDirectory = $PSScriptRoot)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$invariant = [System.Globalization.CultureInfo]::InvariantCulture
$utf8 = [System.Text.UTF8Encoding]::new($false)

function Svg-Number([double]$Value) {
    return $Value.ToString('0.###', $invariant)
}

function Convert-GlyphOutline([System.Drawing.Drawing2D.GraphicsPath]$Shape) {
    $points = $Shape.PathPoints
    $types = $Shape.PathTypes
    $path = [System.Text.StringBuilder]::new()
    for ($i = 0; $i -lt $points.Length; $i++) {
        $point = $points[$i]
        switch ($types[$i] -band 7) {
            0 { [void]$path.Append("M$(Svg-Number $point.X),$(Svg-Number $point.Y)") }
            1 { [void]$path.Append("L$(Svg-Number $point.X),$(Svg-Number $point.Y)") }
            3 {
                $second = $points[$i + 1]
                $end = $points[$i + 2]
                [void]$path.Append("C$(Svg-Number $point.X),$(Svg-Number $point.Y) $(Svg-Number $second.X),$(Svg-Number $second.Y) $(Svg-Number $end.X),$(Svg-Number $end.Y)")
                $i += 2
            }
            default { throw 'Unsupported outline point.' }
        }
        if (($types[$i] -band 128) -ne 0) { [void]$path.Append('Z') }
    }
    return $path.ToString()
}

function Draw-Lettering {
    param(
        [string]$Text,
        [string]$Family,
        [System.Drawing.FontStyle]$Style,
        [double]$X,
        [double]$Y,
        [double]$Height,
        [double]$Tracking,
        [string]$Ink,
        [string]$Accent,
        [int]$AccentIndex = -1
    )
    $font = [System.Drawing.FontFamily]::new($Family)
    $cursor = $X
    $result = [System.Text.StringBuilder]::new()
    try {
        $characters = $Text.ToCharArray()
        for ($index = 0; $index -lt $characters.Length; $index++) {
            $character = [string]$characters[$index]
            if ($character -eq ' ') {
                $cursor += $Height * 0.56
                continue
            }
            $shape = [System.Drawing.Drawing2D.GraphicsPath]::new()
            try {
                $shape.AddString($character, $font, [int]$Style, [single]1000,
                    [System.Drawing.PointF]::new(0,0), [System.Drawing.StringFormat]::GenericTypographic)
                $bounds = $shape.GetBounds()
                $scale = $Height / $bounds.Height
                $matrix = [System.Drawing.Drawing2D.Matrix]::new(
                    [single]$scale, [single]0, [single]0, [single]$scale,
                    [single]($cursor - $bounds.X * $scale), [single]($Y - $bounds.Y * $scale))
                try { $shape.Transform($matrix) } finally { $matrix.Dispose() }
                $fill = if ($index -eq $AccentIndex) { $Accent } else { $Ink }
                $data = Convert-GlyphOutline $shape
                [void]$result.Append("<path fill=`"$fill`" d=`"$data`"/>")
                $cursor += $bounds.Width * $scale + $Tracking
            } finally { $shape.Dispose() }
        }
    } finally { $font.Dispose() }
    return [pscustomobject]@{ Svg = $result.ToString(); Width = $cursor - $X - $Tracking }
}

function Draw-FieldMark([string]$Ink, [string]$Accent) {
    # A 56-unit structural grid. The disconnected upper-right tile is the only
    # deliberate irregularity. This geometry is drawn directly, not from a font.
    return @"
<g id="field-mark" fill="$Ink">
  <path id="s-route" d="M0 0H168V56H56V112H168V280H0V224H112V168H0Z"/>
  <path id="t-position" d="M200 0H368V56H312V280H256V56H200Z"/>
  <path id="off-grid-tile" fill="$Accent" d="M400-16H456V40H400Z"/>
</g>
"@
}

function Draw-Lockup([string]$Ink, [string]$Accent, [string]$Muted) {
    $mark = Draw-FieldMark $Ink $Accent
    $chinese = Draw-Lettering -Text '胡闹战场' -Family 'Microsoft YaHei' -Style Bold `
        -X 516 -Y 114 -Height 146 -Tracking 18 -Ink $Ink -Accent $Accent -AccentIndex 1
    $english = Draw-Lettering -Text 'STUPID TACTICS' -Family 'Arial' -Style Bold `
        -X 521 -Y 298 -Height 38 -Tracking 3.4 -Ink $Ink -Accent $Accent
    $platform = Draw-Lettering -Text 'MINECRAFT' -Family 'Arial' -Style Regular `
        -X 521 -Y 368 -Height 18 -Tracking 6 -Ink $Muted -Accent $Accent
    return @"
<g id="brand-lockup" fill-rule="nonzero">
  <g transform="translate(96 157) scale(0.76)">$mark</g>
  <g id="chinese-wordmark">$($chinese.Svg)</g>
  <g id="english-name">$($english.Svg)</g>
  <g id="platform-subtitle">$($platform.Svg)</g>
</g>
"@
}

function New-SvgDocument {
    param([string]$Width, [string]$Height, [string]$ViewBox, [string]$Body, [string]$Description)
    return @"
<svg xmlns="http://www.w3.org/2000/svg" width="$Width" height="$Height" viewBox="$ViewBox" role="img" aria-labelledby="brand-title brand-desc">
  <title id="brand-title">胡闹战场 · Stupid Tactics Minecraft</title>
  <desc id="brand-desc">$Description</desc>
  $Body
</svg>
"@
}

function Save-Svg([string]$Name, [string]$Source) {
    [System.IO.File]::WriteAllText((Join-Path $OutputDirectory $Name), $Source, $utf8)
}

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$ink = '#1C2930'
$accent = '#ED6547'
$muted = '#667571'
$paper = '#F3F1E7'
$reverseInk = '#F3F1E7'
$reverseAccent = '#FF8266'

$primary = Draw-Lockup $ink $accent $muted
$reverse = Draw-Lockup $reverseInk $reverseAccent '#A6B4AF'
$mono = Draw-Lockup $ink $ink $ink
$description = 'Original code-drawn identity: an S-shaped block route and a T-shaped position with one displaced tile. Chinese name is primary, followed by the English name and Minecraft subtitle. All visible text is outlined.'

Save-Svg 'hunaobattlefield-primary.svg' (New-SvgDocument '1240' '500' '0 0 1240 500' $primary $description)
Save-Svg 'hunaobattlefield-reverse.svg' (New-SvgDocument '1240' '500' '0 0 1240 500' $reverse $description)
Save-Svg 'hunaobattlefield-monochrome.svg' (New-SvgDocument '1240' '500' '0 0 1240 500' $mono $description)
Save-Svg 'hunaobattlefield-symbol.svg' (New-SvgDocument '504' '352' '-24 -40 504 352' (Draw-FieldMark $ink $accent) 'Standalone ST field mark with a displaced coral tile.')

$avatar = "<rect x=`"-24`" y=`"-112`" width=`"504`" height=`"504`" fill=`"$ink`"/><g transform=`"translate(0 8)`">$(Draw-FieldMark $reverseInk $reverseAccent)</g>"
Save-Svg 'hunaobattlefield-avatar.svg' (New-SvgDocument '504' '504' '-24 -112 504 504' $avatar 'Square avatar, using the original field mark on a dark solid ground.')

$preview = "<rect width=`"1400`" height=`"680`" fill=`"$paper`"/><g transform=`"translate(80 90)`">$primary</g>"
Save-Svg 'preview.svg' (New-SvgDocument '1400' '680' '0 0 1400 680' $preview 'Primary original identity displayed on warm paper.')
$reversePreview = "<rect width=`"1400`" height=`"680`" fill=`"$ink`"/><g transform=`"translate(80 90)`">$reverse</g>"
Save-Svg 'preview-dark.svg' (New-SvgDocument '1400' '680' '0 0 1400 680' $reversePreview 'Reverse original identity displayed on deep graphite.')
Write-Output "Original SVG identity written to $OutputDirectory"
