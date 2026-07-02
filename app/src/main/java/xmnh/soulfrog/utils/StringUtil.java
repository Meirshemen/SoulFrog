package xmnh.soulfrog.utils;

public class StringUtil {

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str != null && searchStr != null) {
            int len = searchStr.length();
            int max = str.length() - len;
            for (int i = 0; i <= max; ++i) {
                if (str.regionMatches(true, i, searchStr, 0, len)) {
                    return true;
                }
            }
        }
        return false;
    }

}
