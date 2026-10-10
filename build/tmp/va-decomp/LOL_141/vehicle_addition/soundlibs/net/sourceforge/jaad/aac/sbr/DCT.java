/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

class DCT {
    private static final int n = 32;
    private static final float[] w_array_real = new float[]{1.0f, 0.98078525f, 0.9238795f, 0.8314696f, 0.70710677f, 0.5555702f, 0.3826834f, 0.19509028f, 0.0f, -0.19509037f, -0.3826835f, -0.5555703f, -0.7071068f, -0.83146966f, -0.92387956f, -0.9807853f};
    private static final float[] w_array_imag = new float[]{0.0f, -0.19509032f, -0.38268346f, -0.55557024f, -0.70710677f, -0.83146966f, -0.92387956f, -0.9807853f, -1.0f, -0.98078525f, -0.9238795f, -0.8314696f, -0.7071067f, -0.5555702f, -0.38268337f, -0.19509023f};
    private static final float[] dct4_64_tab = new float[]{0.9999247f, 0.9981181f, 0.993907f, 0.9873014f, 0.9783174f, 0.96697646f, 0.953306f, 0.937339f, 0.9191139f, 0.8986745f, 0.8760701f, 0.8513552f, 0.82458925f, 0.7958369f, 0.76516724f, 0.7326543f, 0.69837624f, 0.66241574f, 0.62485945f, 0.58579785f, 0.545325f, 0.5035384f, 0.46053872f, 0.41642955f, 0.37131715f, 0.32531023f, 0.2785196f, 0.23105814f, 0.18303989f, 0.13458069f, 0.08579727f, 0.036807165f, -1.0121963f, -1.0594388f, -1.1041292f, -1.1461595f, -1.1854287f, -1.2218422f, -1.255312f, -1.2857577f, -1.313106f, -1.3372908f, -1.3582538f, -1.3759449f, -1.390321f, -1.4013479f, -1.4089987f, -1.4132552f, -1.4141071f, -1.4115522f, -1.4055967f, -1.396255f, -1.3835497f, -1.3675113f, -1.3481784f, -1.3255975f, -1.2998233f, -1.2709177f, -1.2389501f, -1.2039981f, -1.1661453f, -1.1254834f, -1.0821099f, -1.0361296f, -0.9876532f, -0.9367974f, -0.88368475f, -0.8284433f, -0.771206f, -0.71211076f, -0.6513001f, -0.58892035f, -0.5251218f, -0.46005824f, -0.39388633f, -0.32676548f, -0.25885743f, -0.19032592f, -0.121335685f, -0.052053273f, 0.017354608f, 0.086720645f, 0.15587783f, 0.22465932f, 0.29289973f, 0.3604344f, 0.42710093f, 0.49273846f, 0.5571889f, 0.62029713f, 0.681911f, 0.74188185f, 0.8000656f, 0.856322f, 0.91051537f, 0.96251523f, 1.0f, 0.99879545f, 0.9951847f, 0.9891765f, 0.98078525f, 0.97003126f, 0.95694035f, 0.94154406f, 0.9238795f, 0.9039893f, 0.88192123f, 0.8577286f, 0.8314696f, 0.8032075f, 0.77301043f, 0.7409511f, 0.70710677f, 0.6715589f, 0.6343933f, 0.5956993f, 0.5555702f, 0.5141027f, 0.47139665f, 0.4275551f, 0.38268343f, 0.33688983f, 0.29028463f, 0.24298012f, 0.19509023f, 0.1467305f, 0.098017134f, 0.04906765f, -1.0f, -1.0478631f, -1.0932019f, -1.1359069f, -1.1758755f, -1.2130115f, -1.247225f, -1.2784339f, -1.3065629f, -1.3315444f, -1.353318f, -1.3718314f, -1.3870399f, -1.3989068f, -1.4074037f, -1.4125102f, 0.0f, -1.4125102f, -1.4074037f, -1.3989068f, -1.3870399f, -1.3718314f, -1.353318f, -1.3315444f, -1.3065629f, -1.2784339f, -1.247225f, -1.2130114f, -1.1758755f, -1.135907f, -1.0932019f, -1.0478631f, -1.0f, -0.9497278f, -0.89716756f, -0.842446f, -0.78569496f, -0.7270511f, -0.66665566f, -0.6046542f, -0.54119605f, -0.47643423f, -0.4105245f, -0.34362584f, -0.27589935f, -0.2075082f, -0.1386171f, -0.069392145f, 0.0f, 0.069392264f, 0.13861716f, 0.2075082f, 0.27589947f, 0.34362596f, 0.41052464f, 0.4764342f, 0.5411961f, 0.6046542f, 0.6666557f, 0.72705114f, 0.7856951f, 0.842446f, 0.89716756f, 0.9497278f};
    private static final int[] bit_rev_tab = new int[]{0, 16, 8, 24, 4, 20, 12, 28, 2, 18, 10, 26, 6, 22, 14, 30, 1, 17, 9, 25, 5, 21, 13, 29, 3, 19, 11, 27, 7, 23, 15, 31};

    DCT() {
    }

    private static void fft_dif(float[] Real, float[] Imag) {
        float w_imag;
        float w_real;
        float point2_imag;
        float point2_real;
        int i2;
        float point1_imag;
        float point1_real;
        int i = 0;
        while (i < 16) {
            point1_real = Real[i];
            point1_imag = Imag[i];
            i2 = i + 16;
            point2_real = Real[i2];
            point2_imag = Imag[i2];
            w_real = w_array_real[i];
            w_imag = w_array_imag[i];
            int n = i;
            Real[n] = Real[n] + point2_real;
            int n2 = i++;
            Imag[n2] = Imag[n2] + point2_imag;
            Real[i2] = (point1_real -= point2_real) * w_real - (point1_imag -= point2_imag) * w_imag;
            Imag[i2] = point1_real * w_imag + point1_imag * w_real;
        }
        int j = 0;
        int w_index = 0;
        while (j < 8) {
            w_real = w_array_real[w_index];
            w_imag = w_array_imag[w_index];
            int i3 = j;
            point1_real = Real[i3];
            point1_imag = Imag[i3];
            int i22 = i3 + 8;
            point2_real = Real[i22];
            point2_imag = Imag[i22];
            int n = i3;
            Real[n] = Real[n] + point2_real;
            int n3 = i3;
            Imag[n3] = Imag[n3] + point2_imag;
            Real[i22] = (point1_real -= point2_real) * w_real - (point1_imag -= point2_imag) * w_imag;
            Imag[i22] = point1_real * w_imag + point1_imag * w_real;
            i3 = j + 16;
            point1_real = Real[i3];
            point1_imag = Imag[i3];
            i22 = i3 + 8;
            point2_real = Real[i22];
            point2_imag = Imag[i22];
            int n4 = i3;
            Real[n4] = Real[n4] + point2_real;
            int n5 = i3;
            Imag[n5] = Imag[n5] + point2_imag;
            Real[i22] = (point1_real -= point2_real) * w_real - (point1_imag -= point2_imag) * w_imag;
            Imag[i22] = point1_real * w_imag + point1_imag * w_real;
            ++j;
            w_index += 2;
        }
        for (i = 0; i < 32; i += 8) {
            i2 = i + 4;
            point1_real = Real[i];
            point1_imag = Imag[i];
            point2_real = Real[i2];
            point2_imag = Imag[i2];
            int n = i;
            Real[n] = Real[n] + point2_real;
            int n6 = i;
            Imag[n6] = Imag[n6] + point2_imag;
            Real[i2] = point1_real - point2_real;
            Imag[i2] = point1_imag - point2_imag;
        }
        w_real = w_array_real[4];
        for (i = 1; i < 32; i += 8) {
            i2 = i + 4;
            point1_real = Real[i];
            point1_imag = Imag[i];
            point2_real = Real[i2];
            point2_imag = Imag[i2];
            int n = i;
            Real[n] = Real[n] + point2_real;
            int n7 = i;
            Imag[n7] = Imag[n7] + point2_imag;
            Real[i2] = ((point1_real -= point2_real) + (point1_imag -= point2_imag)) * w_real;
            Imag[i2] = (point1_imag - point1_real) * w_real;
        }
        for (i = 2; i < 32; i += 8) {
            i2 = i + 4;
            point1_real = Real[i];
            point1_imag = Imag[i];
            point2_real = Real[i2];
            point2_imag = Imag[i2];
            int n = i;
            Real[n] = Real[n] + point2_real;
            int n8 = i;
            Imag[n8] = Imag[n8] + point2_imag;
            Real[i2] = point1_imag - point2_imag;
            Imag[i2] = point2_real - point1_real;
        }
        w_real = w_array_real[12];
        for (i = 3; i < 32; i += 8) {
            i2 = i + 4;
            point1_real = Real[i];
            point1_imag = Imag[i];
            point2_real = Real[i2];
            point2_imag = Imag[i2];
            int n = i;
            Real[n] = Real[n] + point2_real;
            int n9 = i;
            Imag[n9] = Imag[n9] + point2_imag;
            Real[i2] = ((point1_real -= point2_real) - (point1_imag -= point2_imag)) * w_real;
            Imag[i2] = (point1_real + point1_imag) * w_real;
        }
        for (i = 0; i < 32; i += 4) {
            i2 = i + 2;
            point1_real = Real[i];
            point1_imag = Imag[i];
            point2_real = Real[i2];
            point2_imag = Imag[i2];
            int n = i;
            Real[n] = Real[n] + point2_real;
            int n10 = i;
            Imag[n10] = Imag[n10] + point2_imag;
            Real[i2] = point1_real - point2_real;
            Imag[i2] = point1_imag - point2_imag;
        }
        for (i = 1; i < 32; i += 4) {
            i2 = i + 2;
            point1_real = Real[i];
            point1_imag = Imag[i];
            point2_real = Real[i2];
            point2_imag = Imag[i2];
            int n = i;
            Real[n] = Real[n] + point2_real;
            int n11 = i;
            Imag[n11] = Imag[n11] + point2_imag;
            Real[i2] = point1_imag - point2_imag;
            Imag[i2] = point2_real - point1_real;
        }
        for (i = 0; i < 32; i += 2) {
            i2 = i + 1;
            point1_real = Real[i];
            point1_imag = Imag[i];
            point2_real = Real[i2];
            point2_imag = Imag[i2];
            int n = i;
            Real[n] = Real[n] + point2_real;
            int n12 = i;
            Imag[n12] = Imag[n12] + point2_imag;
            Real[i2] = point1_real - point2_real;
            Imag[i2] = point1_imag - point2_imag;
        }
    }

    public static void dct4_kernel(float[] in_real, float[] in_imag, float[] out_real, float[] out_imag) {
        int i_rev;
        float tmp;
        float x_im;
        float x_re;
        int i;
        for (i = 0; i < 32; ++i) {
            x_re = in_real[i];
            x_im = in_imag[i];
            tmp = (x_re + x_im) * dct4_64_tab[i];
            in_real[i] = x_im * dct4_64_tab[i + 64] + tmp;
            in_imag[i] = x_re * dct4_64_tab[i + 32] + tmp;
        }
        DCT.fft_dif(in_real, in_imag);
        for (i = 0; i < 16; ++i) {
            i_rev = bit_rev_tab[i];
            x_re = in_real[i_rev];
            x_im = in_imag[i_rev];
            tmp = (x_re + x_im) * dct4_64_tab[i + 96];
            out_real[i] = x_im * dct4_64_tab[i + 160] + tmp;
            out_imag[i] = x_re * dct4_64_tab[i + 128] + tmp;
        }
        out_imag[16] = (in_imag[1] - in_real[1]) * dct4_64_tab[112];
        out_real[16] = (in_real[1] + in_imag[1]) * dct4_64_tab[112];
        for (i = 17; i < 32; ++i) {
            i_rev = bit_rev_tab[i];
            x_re = in_real[i_rev];
            x_im = in_imag[i_rev];
            tmp = (x_re + x_im) * dct4_64_tab[i + 96];
            out_real[i] = x_im * dct4_64_tab[i + 160] + tmp;
            out_imag[i] = x_re * dct4_64_tab[i + 128] + tmp;
        }
    }
}

