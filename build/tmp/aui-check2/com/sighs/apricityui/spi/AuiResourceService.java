/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.spi;

import com.sighs.apricityui.spi.RenderHandle;
import com.sighs.apricityui.spi.TextureKey;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface AuiResourceService {
    public Optional<InputStream> openResource(String var1);

    public Map<String, String> listResourcePaths(String var1, String var2);

    public TextureKey locationOf(String var1);

    public TextureKey tryParseTextureKey(String var1);

    public Object textureLocation(TextureKey var1);

    public RenderHandle smoothRenderType(TextureKey var1, boolean var2, boolean var3);
}

