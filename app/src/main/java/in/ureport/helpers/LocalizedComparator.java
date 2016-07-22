package in.ureport.helpers;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by john-mac on 7/21/16.
 */
public class LocalizedComparator implements Comparator<Object> {

    private static final String THAILAND_COUNTRY = "th";

    private Collator collator;
    private Locale locale;

    public LocalizedComparator(Locale locale) {
        this.locale = locale;
        this.collator = Collator.getInstance(locale);
    }

    @Override
    public int compare(Object lhs, Object rhs) {
//        if (locale.getLanguage().equals(THAILAND_COUNTRY)) {
//            return ThailandComparator.compare(lhs.toString(), rhs.toString());
//        }
//        return collator.compare(lhs.toString(), rhs.toString());
        return ThailandComparator.compare(lhs.toString().toLowerCase(locale), rhs.toString().toLowerCase(locale));
    }
}
