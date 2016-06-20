package in.ureport.helpers;

/**
 * Created by john-mac on 2/20/16.
 */
public class TimeFormatter {

    public static String getDurationStringFromMillis(long milliseconds) {
        return getDurationString((int)milliseconds/1000);
    }

    public static String getDurationString(int seconds) {
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(minutes) + ":" + twoDigitString(seconds);
    }

    private static String twoDigitString(int number) {
        if (number == 0)
            return "00";

        if (number / 10 == 0)
            return "0" + number;

        return String.valueOf(number);
    }

}
