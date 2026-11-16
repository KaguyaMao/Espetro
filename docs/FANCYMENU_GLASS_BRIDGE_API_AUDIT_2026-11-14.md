# FancyMenu glass bridge API audit

Audited local dependency: `fancymenu_forge_3.9.9_MC_1.20.1.jar`, Forge 1.20.1 / Java 17.

## Verified API and rendering behavior

The installed JAR exposes these public static methods, verified with `javap -p -c`:

```java
GuiBlurRenderer.renderBlurArea(GuiGraphics graphics,
    float x, float y, float width, float height,
    float blurRadius, float cornerRadius, DrawableColor tint, float partialTick);
DrawableColor.of(int red, int green, int blue, int alpha);
RenderingUtils.captureRenderState(); // returns public nested RenderStateSnapshot
RenderingUtils.RenderStateSnapshot.restore();
```

The `renderBlurAreaScaled` overload does **not** transform coordinates through
`GuiGraphics.pose()`. It reads FancyMenu's `RenderScaleUtil` / `RenderTranslationUtil`
trackers. The unscaled overload expects coordinates in screen GUI units and then
applies Minecraft's window GUI scale internally. The bridge therefore transforms the
rectangle through the caller's pose once, calls the unscaled overload, and scales the
blur radius with the smaller absolute X/Y scale. AuiScreen slide translations and HUD
scales remain aligned. Rotated, sheared, perspective, degenerate, and nonfinite poses
return false so the normal painter can handle them.

The native renderer loads `minecraft:shaders/post/fancymenu_gui_blur.json`, flushes
pending GUI primitives, processes the current framebuffer through a local blur mask,
then composites the result with the supplied RGBA tint. The bridge supplies a zero
corner radius for the requested simple, square-cornered glass style. No theme colors
or blur-strength policy are defined in the bridge.

The renderer already pushes/pops its own GuiGraphics scissor and restores its render
state in `finally`. Its snapshot includes framebuffer, viewport, scissor, texture
bindings, blend settings, depth test/mask/function, culling, shader, color and fog.
However, that snapshot is captured **after** post-chain initialization/resize and
`GuiGraphics.flush()`, and its final cleanup resets shader color to white. The bridge
takes and restores an additional outer native snapshot so the caller's preexisting
depth state, opacity and initial framebuffer binding survive those steps as well.
The bridge never mutates the pose stack or the user's FancyMenu configuration.

## Failure and client/server boundaries

Reflection is resolved and cached once on demand. No FancyMenu class is a Java import,
field type, method descriptor or superclass; common/server registration must never
invoke this client-only class. `draw` also rejects calls off the render thread. A
client-only Forge dependency on FancyMenu 3.9.9 remains the integration responsibility
of `mods.toml`.

The native renderer has a void return and silently returns after shader initialization
fails. The bridge reads cached private `blurPostChainFailed` and `blurPostChain` fields
after the call to distinguish this from a successful invocation. Access/API/render
failure disables the bridge for the process, logs once, and returns false for normal
translucent painting. These private field names are verified for 3.9.9; an incompatible
future release falls back safely. No retry or modification of another mod is attempted.

Reflection-wrapped `VirtualMachineError`, `ThreadDeath`, and other non-linkage errors
are rethrown rather than converted to an ordinary visual fallback.

## Verification and limitations

`FancyMenuGlassGeometryTest` covers a combined slide/scale pose, a reflected pose,
unsupported rotation, collapsed scale, empty bounds and nonfinite inputs. Run it in
the project test task alongside the integration build; no concurrent Gradle build was
started by this subtask. Bytecode inspection confirms the APIs and state behavior;
an actual OpenGL/game session is still required to verify shader-pack compatibility,
render timing and visual appearance. Multiple adjacent areas each invoke the native
blur renderer, so callers should prefer one background panel per content region and
avoid redundantly blurring every decorative stroke or row.
