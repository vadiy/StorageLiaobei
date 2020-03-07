package vip.vadiy.storageliaobei.Utils;

public class MStringUtils {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();


    /**
     * hex转byte数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexToBytes(String hex) {
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            int intVal = Integer.decode("0x" + hex.substring(i * 2, i * 2 + 2));
            ret[i] = Byte.valueOf((byte) intVal);
        }
        return ret;
    }

    public static String bytesToHex(byte[] data, boolean separated) {
        if (data != null) {
            int len = data.length * (separated == true ? 3 : 2);
            StringBuilder r = new StringBuilder(len);
            for (byte b : data) {
                r.append(hexCode[(b >> 4) & 0xf]);
                r.append(hexCode[(b) & 0xf]);
                r.append((separated == true) ? "," : "");
            }
            return r.toString();
        } else {
            return "";
        }

    }

    public static String bytesTobytes(byte[] data) {
        if (data != null) {
            int iMax = data.length - 1;
            if (iMax == -1) {
                return "{}";
            }
            StringBuilder r = new StringBuilder();
            r.append("{");
            for (int i = 0; ; i++) {
                int h = data[i] & 0xff;
                r.append(h);
                if (i == iMax) {
                    return r.append("}").toString();
                }
                r.append(", ");
            }
        }
        return "{}";
    }

}
