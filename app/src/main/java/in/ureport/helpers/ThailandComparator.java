package in.ureport.helpers;

/**
 * Created by john-mac on 7/21/16.
 */
public class ThailandComparator {

    static final char SARA_E = 0x0E40;
    static final char SARA_AI_MAIMALAI = 0x0E44;
    static final char MAITAIKHU = 0x0E47;
    static final char THANTHAKHAT = 0x0E4C;

    public static int compare(String lhs, String rhs) {
        return convertToCompare(lhs).compareTo(convertToCompare(rhs));
    }

    private static String convertToCompare(String value) {
        char[] chars = value.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (isLeadingVowel(chars[i])) {
                char c = chars[i];
                chars[i] = chars[i + 1];
                chars[i + 1] = c;
                i++;
            }
        }

        String tail = "00";
        String head = "";
        for (int i = 0; i < chars.length; i++) {
            if (isToneMark(chars[i])) {
                int pos = chars.length - i;
                if (pos >= 10)
                    tail += "" + pos;
                else
                    tail+= "0" + pos;
                tail += chars[i];
            }
            else {
                head += chars[i];
            }
        }

        return head + tail;
    }

    private static boolean isLeadingVowel(char c) {
        return (c >= SARA_E && c <= SARA_AI_MAIMALAI);
    }

    private static boolean isToneMark (char c) {
        return (c >= MAITAIKHU && c <= THANTHAKHAT);
    }
}
