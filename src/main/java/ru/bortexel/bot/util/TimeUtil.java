package ru.bortexel.bot.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimeUtil {
    /**
     * Converts a time interval to easy-to-understand text
     * @param delay Time interval to convert
     * @return Convertation result (String)
     */
    public static String formatDelay(long delay) {
        String result = "";

        int seconds = (int) Math.ceil(((double) delay) / 1000);
        int minutes = (int) Math.ceil(((double) delay) / (60 * 1000));
        int hours = (int) Math.ceil(((double) delay) / (60 * 60 * 1000));
        int days = (int) Math.ceil(((double) delay) / (24 * 60 * 60 * 1000));

        if (seconds != 0) result = "" + seconds + " сек.";
        if (minutes != 0) result = "" + minutes + " мин.";
        if (hours != 0) result = "" + hours + " ч.";
        if (days != 0) result = "" + days + " дн.";

        return result;
    }

    /**
     * Converts a time interval to easy-to-understand text with right spelling
     * @param time Time interval to convert (unixtime)
     * @return Convertation result (String)
     */
    public static String formatLength(long time) {
        int days = (int) Math.floor((double) time / 86400);
        time = time - days * 86400L;
        int hours = (int) Math.floor((double) time / 3600);
        time = (int) time - hours * 3600L;
        int minutes = (int) Math.floor((double) time / 60);
        time = time - minutes * 60L;

        StringBuilder output = new StringBuilder();
        int count = 0;
        if (days > 0) {
            output.append(days);
            String s = "" + days;
            count++;
            if (s.length() >= 2 && s.charAt(s.length() - 2) == '1') {
                output.append(" дней ");
            } else {
                switch (s.charAt(s.length() - 1)) {
                    case '0':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        output.append(" дней ");
                        break;
                    case '1':
                        output.append(" день ");
                        break;
                    case '2':
                    case '3':
                    case '4':
                        output.append(" дня ");
                        break;
                }
            }
        }
        if (hours > 0) {
            output.append(hours);
            String s = "" + hours;
            count++;
            if (s.length() >= 2 && s.charAt(s.length() - 2) == '1') {
                output.append(" часов ");
            } else {
                switch (s.charAt(s.length() - 1)) {
                    case '0':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        output.append(" часов ");
                        break;
                    case '1':
                        output.append(" час ");
                        break;
                    case '2':
                    case '3':
                    case '4':
                        output.append(" часа ");
                        break;
                }
            }
        }
        if (minutes > 0 && count < 2) {
            output.append(minutes);
            String s = "" + minutes;
            count++;
            if (s.length() >= 2 && s.charAt(s.length() - 2) == '1') {
                output.append(" минут ");
            } else {
                switch (s.charAt(s.length() - 1)) {
                    case '0':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        output.append(" минут ");
                        break;
                    case '1':
                        output.append(" минуту ");
                        break;
                    case '2':
                    case '3':
                    case '4':
                        output.append(" минуты ");
                        break;
                }
            }
        }
        if ((time > 0 && count < 2) && !(time == 1 && count == 1)) {
            output.append(time);
            String s = "" + time;
            if (s.length() >= 2 && s.charAt(s.length() - 1) == '1') {
                output.append(" секунд ");
            } else {
                switch (s.charAt(s.length() - 1)) {
                    case '0':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        output.append(" секунд ");
                        break;
                    case '1':
                        output.append(" секунду ");
                        break;
                    case '2':
                    case '3':
                    case '4':
                        output.append(" секунды ");
                        break;
                }
            }
        }

        return output.toString();
    }

    /**
     * Converts AdBhCmDs to time interval
     * @param s Input string to convert
     * @return Time interval (long)
     */
    public static long convertString(String s) {
        long time = 0;
        char[] input = s.toCharArray();

        for (int i = 0; i < s.length(); i++) {
            switch (input[i]) {
                case 's':
                    time = time + getIntFromTo(input, i);
                    break;
                case 'm':
                    time = time + (long) getIntFromTo(input, i) * 60;
                    break;
                case 'h':
                    time = time + (long) getIntFromTo(input, i) * 3600;
                    break;
                case 'd':
                    time = time + (long) getIntFromTo(input, i) * 86400;
                    break;
            }
        }

        return time;
    }

    /**
     * Gets integer from given string
     * @param string Input string
     * @param startPos Parsing start position
     * @return Output integer
     */
    private static int getIntFromTo(char[] string, int startPos) {
        final List<Character> numbers = new ArrayList<>();
        numbers.add('0');
        numbers.add('1');
        numbers.add('2');
        numbers.add('3');
        numbers.add('4');
        numbers.add('5');
        numbers.add('6');
        numbers.add('7');
        numbers.add('8');
        numbers.add('9');

        int pos = 1, res = 0;

        for (int i = startPos - 1; i >= 0; i--) {
            if (numbers.contains(string[i])) {
                res = res + Integer.parseInt("" + string[i]) * pos;
                pos = pos * 10;
            } else break;
        }

        return res;
    }

    public static SimpleDateFormat getDefaultDateFormat() {
        return new SimpleDateFormat("E, dd MMMM yyyy HH:mm:ss", new Locale("ru", "RU"));
    }

    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
}
