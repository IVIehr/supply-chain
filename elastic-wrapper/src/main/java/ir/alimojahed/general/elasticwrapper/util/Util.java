package ir.alimojahed.general.elasticwrapper.util;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import ir.alimojahed.general.elasticwrapper.exception.ExceptionStatus;
import ir.alimojahed.general.elasticwrapper.exception.ProjectException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Ali Mojahed on 10/7/2022
 * @project iso3
 **/
public class Util {
    public static boolean isNullOrEmpty(String param) {
        return param == null || param.isEmpty() || param.trim().isEmpty();
    }

    public static <k, T> boolean isNullOrEmpty(Map<k, T> param) {
        return param == null || param.isEmpty();
    }

    public static <T> boolean isNullOrEmpty(Set<T> param) {
        return param == null || param.isEmpty();
    }


    public static <T> boolean isNotInitializedOrNullOrEmpty(Set<T> param) {
        return !Hibernate.isInitialized(param) || param == null || param.isEmpty();
    }


    public static <T> boolean isNullOrEmpty(List<T> param) {
        return param == null || param.isEmpty();
    }

    public static <T> boolean isNullOrEmpty(T[] param) {
        return param == null || param.length <= 0;
    }

    public static <T> boolean isNullOrEmpty(Collection<T> param) {
        return param == null || param.isEmpty();
    }

    public static <T> boolean isNullOrZero(Long number) {
        return number == null || number == 0L;
    }

    public static List<String> sentenceToWords(String filterValue) {
        String[] words = filterValue.split("\\s+");
        return Arrays.asList(words);
    }

    public static boolean isPositive(int number) {
        return number >= 0;
    }

    public static void checkPagination(int size, int offset) throws ProjectException {
        if (!Util.isPositive(offset) || !Util.isPositive(size)) {
            throw new ProjectException(ExceptionStatus.INVALID_INPUT, "pagination is negative");
        }
    }

    public static List<String> splitWords(String value) {
        List<String> words = sentenceToWords(value);
        List<String> searchedWords = new ArrayList<>();

        words.forEach(w -> {
            w = w.replace("_", "\\_");
            w = w.replace("%", "\\%");
            String word = w.replace(w, "'%" + w + "%'");
            if (!Util.isNullOrEmpty(w)) {
                searchedWords.add(word);
            }
        });
        return searchedWords;
    }


    public static String getSplitSearchQuery(String field, String search) {
        if (StringUtils.isBlank(search)) {
            return "";
        }

        List<String> splittedWords = Util.splitWords(search);

        StringBuilder filterStatement = new StringBuilder(" and (");
        splittedWords.forEach(word -> filterStatement
                .append(field)
                .append(" like ")
                .append(" lower( ")
                .append(word)
                .append(" )")
                .append(" escape \'\\\' ")
                .append(" or "));

        return filterStatement
                .substring(0, filterStatement.lastIndexOf("or")) + ") ";
    }

    public static long generateMajorCode(long code, long facultyCode, long gradeCode) {
        return Long.parseLong(String.valueOf(gradeCode) + String.valueOf(facultyCode) + String.valueOf(code));
    }

    public static String getMajor(String majorAndField) {
        return majorAndField.split("-")[0];
    }

    public static String getField(String majorAndField) {
        String[] split = majorAndField.split("-");
        return split.length == 2 ? split[1] : "";
    }

    public static long getTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime).getTime();
    }

    public static String getJalaliDate(long timestamp) {
        LocalDateTime localDateTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
                        TimeZone.getDefault().toZoneId());

        DateConverter dateConverter = new DateConverter();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(localDateTime.getYear(),
                localDateTime.getMonth(),
                localDateTime.getDayOfMonth());

        String year = String.valueOf(jalaliDate.getYear());

        String month = jalaliDate.getMonthPersian().getValue() < 10 ?
                "0" + jalaliDate.getMonthPersian().getValue() : String.valueOf(jalaliDate.getMonthPersian().getValue());

        String day = jalaliDate.getDay() < 10 ?
                "0" + jalaliDate.getDay() : String.valueOf(jalaliDate.getDay());

        return year + "/" + month + "/" + day;

    }

}
