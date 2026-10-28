package org.espetro.client;

/**
 * 客户端"我是不是医疗兵"状态（由 {@link org.espetro.network.MedicFlagPacket} 写入）。
 *
 * <p>只用于渲染：医疗兵可以看到倒地队友的头顶/地图标识。默认 false，
 * 服务端未下发（例如未配置 med 职业）时保持普通视野。</p>
 */
public final class ClientMedicState {

    private static boolean medic;

    private ClientMedicState() {
    }

    public static void setMedic(boolean value) {
        medic = value;
    }

    public static boolean isMedic() {
        return medic;
    }

    /** 退出服务器时清理，避免把上一个服务器/存档的医疗兵身份带到下一个。 */
    public static void clear() {
        medic = false;
    }
}
