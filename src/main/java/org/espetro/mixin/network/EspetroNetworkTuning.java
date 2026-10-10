package org.espetro.mixin.network;

/**
 * 弱网容忍参数（Espetro 网络调优）。
 *
 * <p>原版两处硬阈值在差网络下会直接掐断连接，与流量大小无关：</p>
 * <ul>
 *   <li>netty {@code ReadTimeoutHandler(30)}：客户端与服务端双向都装（30 秒收不到任何字节就断开）；
 *       {@code Connection$1}（客户端出站连接初始化）用字面量 30，
 *       {@code ServerConnectionListener$1}（服务端入站连接初始化）用字段
 *       {@code ServerConnectionListener.READ_TIMEOUT}。</li>
 *   <li>{@code ServerGamePacketListenerImpl.tick()} 的 KeepAlive 判定：每 15 秒发一次，
 *       下次检查仍未回应就 {@code disconnect("Timed out")}。</li>
 * </ul>
 *
 * <p>这里统一放宽：读超时 30 → 120 秒，KeepAlive 窗口 15 → 60 秒（心跳间隔随之变长，
 * 交互流量更小；容忍窗口约 120 秒）。若需恢复原版数值，把下面两个常量改回 30 / 15000L 重新构建即可。</p>
 */
public final class EspetroNetworkTuning {

    /** netty 读超时（秒）：原版 30。 */
    public static final int READ_TIMEOUT_SECONDS = 120;

    /** KeepAlive 判定窗口（毫秒）：原版 15000（15 秒）。 */
    public static final long KEEP_ALIVE_WINDOW_MILLIS = 60_000L;

    private EspetroNetworkTuning() {
    }
}
