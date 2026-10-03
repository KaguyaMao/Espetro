/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.network.codec;

import com.sighs.apricityui.network.codec.StreamDecoder;
import com.sighs.apricityui.network.codec.StreamEncoder;

public interface StreamCodec<B, V>
extends StreamDecoder<B, V>,
StreamEncoder<B, V> {
}

