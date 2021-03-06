package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for parsing.
 *
 * Copyright (c) 2014 University of Oxford
 */
public final class ParseUtils {
    private ParseUtils() {
    }

    /**
     * Parses a string into an integer. Trims whitespace and does not throw a NumberFormatException.
     * @param toParse The string to parse.
     * @return The parsed integer, or null if it could not be parsed.
     */
    public static Integer parseInteger(String toParse) {
        try {
            return Integer.parseInt(convertString(toParse));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a string into a long. Trims whitespace and does not throw a NumberFormatException.
     * @param toParse The string to parse.
     * @return The parsed long, or null if it could not be parsed.
     */
    public static Long parseLong(String toParse) {
        try {
            return Long.parseLong(convertString(toParse));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a string into a double. Trims whitespace and does not throw a NumberFormatException.
     * @param toParse The string to parse.
     * @return The parsed double, or null if it could not be parsed.
     */
    public static Double parseDouble(String toParse) {
        String convertedString = convertString(toParse);

        // Null input causes Double.parseDouble to throw a NullPointerException, so handle this separately
        if (convertedString == null) {
            return null;
        }

        try {
            return Double.parseDouble(convertedString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Converts a string to a canonical form, by stripping whitespace and converting to a null if an empty string.
     * @param toConvert The string to convert.
     * @return The converted string.
     */
    public static String convertString(String toConvert) {
        String convertedString = StringUtils.trimWhitespace(toConvert);
        if (convertedString != null && convertedString.equals("")) {
            convertedString = null;
        }
        return convertedString;
    }

    /**
     * Parses a list of strings into a list of integers using parseInteger().
     * @param strings A list of strings to parse.
     * @return The parsed integers.
     */
    public static List<Integer> parseIntegers(List<String> strings) {
        List<Integer> parsedIntegers = null;

        if (strings != null) {
            parsedIntegers = new ArrayList<>();
            for (String string : strings) {
                parsedIntegers.add(ParseUtils.parseInteger(string));
            }
        }

        return parsedIntegers;
    }

    /**
     * Converts a list of strings using convertString().
     * @param strings A list of strings to convert.
     * @return The converted strings.
     */
    public static List<String> convertStrings(List<String> strings) {
        List<String> convertedStrings = null;

        if (strings != null) {
            convertedStrings = new ArrayList<>();
            for (String string : strings) {
                convertedStrings.add(ParseUtils.convertString(string));
            }
        }

        return convertedStrings;
    }

    /**
     * Splits the specified comma-delimited text. All tokens are trimmed and empty tokens are ignored.
     * @param text The text to split.
     * @return The split text.
     */
    public static List<String> splitCommaDelimitedString(String text) {
        List<String> splitList = new ArrayList<>();

        if (StringUtils.hasText(text)) {
            // Note: all tokens are trimmed and empty tokens are ignored
            String[] splitArray = StringUtils.tokenizeToStringArray(text, ",", true, true);
            Collections.addAll(splitList, splitArray);
        }

        return splitList;
    }

    /**
     * Splits the specified space-delimited text. All tokens are trimmed and empty tokens are ignored.
     * @param text The text to split.
     * @return The split text.
     */
    public static List<String> splitSpaceDelimitedString(String text) {
        return Arrays.asList(StringUtils.tokenizeToStringArray(text, " ", true, true));
    }

    /**
     * Reads a CSV file into a list of the specified type.
     * @param csv The CSV file.
     * @param responseClass The type of the returned list's elements.
     * @param schema A schema representing the CSV file's structure.
     * @param <T> The type of the returned list's elements.
     * @return A list of parsed elements.
     * @throws IOException if parsing failed.
     */
    public static <T> List<T> readFromCsv(String csv, Class<T> responseClass, CsvSchema schema) throws IOException {
        if (csv == null) {
            // Protect against NullPointerException
            csv = "";
        }

        ObjectReader reader = new CsvMapper().reader(responseClass).with(schema);
        MappingIterator<T> iterator = reader.readValues(csv);
        ArrayList<T> results = new ArrayList<>();
        try {
            while (iterator.hasNext()) {
                results.add(iterator.next());
            }
        } catch (RuntimeException e) {
            // ObjectReader throws (subclasses of) IOException, but MappingIterator wraps them in a RuntimeException.
            // We unwrap them for consistency.
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw e;
        }
        return results;
    }
}
