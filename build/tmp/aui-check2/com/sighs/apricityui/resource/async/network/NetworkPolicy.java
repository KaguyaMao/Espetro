/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.resource.async.network;

public final class NetworkPolicy {
    public static final int CONNECT_TIMEOUT_MS = 3000;
    public static final int READ_TIMEOUT_MS = 3000;
    public static final int MAX_RETRY_COUNT = 1;
    public static final int MAX_REDIRECTS = 3;
    public static final int MAX_IN_FLIGHT_REQUESTS = 4;
    public static final int MAX_CONTENT_LENGTH_BYTES = 0x800000;
    public static final long SUCCESS_CACHE_TTL_MS = 60000L;
    public static final int SUCCESS_CACHE_MAX_ENTRIES = 256;
    public static final long SUCCESS_CACHE_SWEEP_INTERVAL_MS = 30000L;
    public static final long DISK_CACHE_TTL_MS = 604800000L;
    public static final long FAILURE_RETRY_DELAY_MS = 5000L;
    public static final long RETRY_DELAY_429_MS = 20000L;
    public static final long RETRY_DELAY_5XX_OR_TIMEOUT_MS = 2000L;

    private NetworkPolicy() {
    }
}

