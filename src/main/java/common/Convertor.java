package common;

public class Convertor {

    /**
     * スネークケース→キャメルケース変換
     *
     * @param str 対象文字列
     * @return 変換後文字列
     */
    public static String snakeCase2CamelCase(String str) {
        boolean conv = false;
        char[] charStr = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < charStr.length; i++) {
            if(charStr[i] == '_') {
                conv = true;
                continue;
            }
            if(conv) {
                String upper = String.valueOf(charStr[i]).toUpperCase();
                sb.append(upper);
            } else {
                sb.append(charStr[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 最初一文字を大文字変換
     *
     * @param str 対象文字列
     * @return 変換後文字列
     */
    public static String firstCharUpperConvert(String str) {
        String firstChar = str.substring(0, 1);
        String remainStr = str.substring(1);
        return firstChar.toUpperCase() + remainStr;
    }

}
