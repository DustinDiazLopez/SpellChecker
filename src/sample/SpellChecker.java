package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpellChecker {
    public static HashSet<Word> incorrectWords;

    public static void main(String[] args) {
        File file = new File("src/dictionary/spanish.txt");
        final List<String> dictionary = lda(file);

        if (dictionary == null) {
            System.err.println("Dictionary is NULL.");
            System.exit(1);
        } else {
            System.out.println("Dictionary has been initialized.");
        }

        String input = "Saldos mi nobre es Dustin Díaz soi una perzona, 787 787-234-1234";
        HashSet<Word> ey = spellCheck(input, dictionary);
        if (ey.isEmpty()) {
            System.out.println("No errors.");
        } else {
            System.out.println("Errors were found. " + ey);
        }
    }

    static HashSet<Word> spellCheck(String input, List<String> dictionary) {
        incorrectWords = new HashSet<>();
        String currentCheck;
        Scanner spellChecker = new Scanner(input);
        spellChecker.useDelimiter("\\s+");
        Word range;
        while (spellChecker.hasNext()) {
            currentCheck = spellChecker.next();
            String temp = currentCheck;
            if (isSpecial(currentCheck)) temp = temp.replaceAll("([.,()])", "");
            if (!validWord(temp, dictionary) && !otherChecks(currentCheck)) {
                range = new Word(currentCheck, spellChecker.match().start(), spellChecker.match().end());
                range.isError = true;
                incorrectWords.add(range);
                System.out.println(currentCheck + " is spelt incorrectly at index " + spellChecker.match().start());
            } else if (isEmail(currentCheck)) {
                range = new Word(currentCheck, spellChecker.match().start(), spellChecker.match().end());
                range.isEmail = true;
                incorrectWords.add(range);
                System.out.println(currentCheck + " is an email at index " + spellChecker.match().start());
            } else if (isURL(currentCheck)) {
                range = new Word(currentCheck, spellChecker.match().start(), spellChecker.match().end());
                range.isWebsite = true;
                incorrectWords.add(range);
                System.out.println(currentCheck + " is a website at index " + spellChecker.match().start());
            }
        }

        return incorrectWords;
    }

    private static boolean otherChecks(String text) {
        String temp = text.trim();
        return isEmail(temp) || isPhone(temp) || isNumber(temp) || isURL(text);
    }

    private static boolean isURL(String text) {
        return text.matches("^((https?|ftp|smtp)://)?(www.)?[a-z0-9]+\\.[a-z]+(/[a-zA-Z0-9#]+/?)*$");
    }

    private static boolean isNumber(String text) {
        return text.matches("[0-9]+");
    }

    private static boolean isPhone(String text) {
        return text.matches("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$");
    }

    private static boolean isEmail(String text) {
        return text.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");
    }

    private static boolean validWord(String currentCheck, List<String> dictionary) {
        boolean valid = false;
        int i = 0;
        while (!valid && i < dictionary.size()) {
            if (currentCheck.trim().equalsIgnoreCase(dictionary.get(i))) valid = true;
            i++;
        }
        return valid;
    }

    private static boolean isSpecial(String currentCheck) {
        Pattern pattern = Pattern.compile("([.,():])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(currentCheck);
        return matcher.find();
    }

    static List<String> lda(File file) {
        List<String> dictionary = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) dictionary.add(line.trim());
            return dictionary;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static class Word {
        public String word;
        public boolean isEmail = false;
        public boolean isWebsite = false;
        public boolean isError = false;
        public boolean isWarning = false;
        public boolean hasMultipleVersions = false;
        public int start;
        public int end;

        public Word(String word, int start, int end) {
            this.word = word;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return word + "{" +
                    "start=" + start +
                    ",end=" + end +
                    '}';
        }
    }
}
