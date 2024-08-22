/**
 * Copyright (C) 2020 GZoltar contributors.
 *
 * This file is part of GZoltar.
 *
 * GZoltar is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * GZoltar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with GZoltar. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.gzoltar.irfl.topicModeling.nlp;

import com.gzoltar.core.util.FileUtils;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Predicate;

public class NLPParser {
    Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    private final static String SEPARATORS_PATH = String.valueOf(NLPParser.class.getResource("nlp/separators.txt"));
    private final static String SINGLE_CHAR_OPERATORS_PATH = String.valueOf(NLPParser.class.getResource("nlp/singleCharOperators.txt"));
    private final static String MULTI_CHAR_OPERATORS_PATH = String.valueOf(NLPParser.class.getResource("nlp/multiCharOperators.txt"));
    private final static String JAVA_KEYWORDS_PATH = String.valueOf(NLPParser.class.getResource("nlp/keywords.txt"));
    private final static String ENGLISH_KEYWORDS_PATH = String.valueOf(NLPParser.class.getResource("nlp/englishWords.txt"));
    private final static Set<String> SEPARATORS = new HashSet<>();
    private final static Set<String> SINGLE_CHAR_OPERATORS = new HashSet<>();
    private final static Comparator<String> byLengthDescendingThenAlphabetical = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            int lengthComparison = Integer.compare(s2.length(), s1.length());
            if (lengthComparison != 0) {
                return lengthComparison;
            } else {
                return s1.compareTo(s2);
            }
        }
    };

    private final static Set<String> MULTI_CHAR_OPERATORS =
            new TreeSet<>(byLengthDescendingThenAlphabetical);

    private final static Set<String> JAVA_KEYWORDS = new HashSet<>();
    private final static Set<String> ENGLISH_KEYWORDS = new HashSet<>();

    public NLPParser() {
        try {
            SEPARATORS.addAll(FileUtils.loadFileByLine(SEPARATORS_PATH));
            SINGLE_CHAR_OPERATORS.addAll(FileUtils.loadFileByLine(SINGLE_CHAR_OPERATORS_PATH));
            MULTI_CHAR_OPERATORS.addAll(FileUtils.loadFileByLine(MULTI_CHAR_OPERATORS_PATH));
            JAVA_KEYWORDS.addAll(FileUtils.loadFileByLine(JAVA_KEYWORDS_PATH));
            ENGLISH_KEYWORDS.addAll(FileUtils.loadFileByLine(ENGLISH_KEYWORDS_PATH));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }

    private static final Predicate<String> isKeyword = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return JAVA_KEYWORDS.contains(s);
        }
    };

    private static final Predicate<String> isBlank = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return s.trim().isEmpty();
        }
    };

    private static final Predicate<String> isImportStatement = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return s.startsWith("import ");
        }
    };

    private static final Predicate<String> isPackageStatement = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return s.startsWith("package ");
        }
    };

    private static final Predicate<String> isCommentLine = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return s.startsWith("//") || s.startsWith("/*") || s.startsWith("*");
        }
    };

    private static final Predicate<String> isSeparator = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return SEPARATORS.contains(s) || s.equals(".");
        }
    };

    private static final Predicate<String> isEnglishWord = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return ENGLISH_KEYWORDS.contains(s);
        }
    };

    private static final Predicate<String> hasOnlySeparators = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            for (char c : s.toCharArray()) {
                if (!isSeparator.test(String.valueOf(c))) return false;
            }
            return true;
        }
    };

    private static final Predicate<String> isRelevantStatement = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return !isBlank.test(s) &&
                    !isImportStatement.test(s) &&
                    !isPackageStatement.test(s) &&
                    !isCommentLine.test(s) &&
                    !hasOnlySeparators.test(s);
        }
    };

    private List<String> filterRelevantLines(List<String> tokens) {
        List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            if (isRelevantStatement.test(token)) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }

    private List<String> filterTokens(List<String> tokens) {
        List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            token = removeLeadingTrailingPunctuation(token);
            if (!isKeyword.test(token) &&
                    !isEnglishWord.test(token) &&
                    !isSeparator.test(token) &&
                    !isBlank.test(token)) {
                token = (String) stemmer.stem(token);
                if (!isKeyword.test(token) &&
                        !isEnglishWord.test(token)) {
                    filteredTokens.add(token.toLowerCase());
                }
            }
        }
        return filteredTokens;
    }

    private String removeLeadingTrailingPunctuation(String token) {
        return token.replaceAll("^\\p{Punct}+|\\p{Punct}+$", "");
    }

    public List<String> processTokens(List<String> tokens) {
        List<String> processedTokens = new ArrayList<>();
        List<String> tokensToProcess = new ArrayList<>();

        tokens = filterRelevantLines(tokens);

        for (String token : tokens) {
            tokensToProcess.addAll(Arrays.asList(token.split(" ")));
        }

        for (String token : tokensToProcess) {
            processedTokens.addAll(removeOperatorsAndSeparators(token));
        }

        return filterTokens(processedTokens);
    }

    private List<String> removeOperatorsAndSeparators(String token) {
        List<String> processedTokens = new ArrayList<>();
        int start = 0;

        while (start < token.length()) {
            int matchLength = processOperatorOrSeparatorAndGetLength(token, processedTokens, MULTI_CHAR_OPERATORS, start);

            if (matchLength == 0) {
                matchLength = processOperatorOrSeparatorAndGetLength(token, processedTokens, SINGLE_CHAR_OPERATORS, start);
            }

            if (matchLength == 0) {
                matchLength = processOperatorOrSeparatorAndGetLength(token, processedTokens, SEPARATORS, start);
            }

            if (matchLength > 0) {
                start += matchLength;
                token = token.substring(start);
                start = 0;
            } else {
                start++;
            }
        }
        if (!token.isEmpty()) {
            processedTokens.addAll(Arrays.asList(token.split("\\s+")));
        }

        return processedTokens;
    }

    private int processOperatorOrSeparatorAndGetLength(String token, List<String> processedTokens, Set<String> toRemove, int start) {
        for (String operatorOrSeparator : toRemove) {
            if (token.startsWith(operatorOrSeparator, start)) {
                if (start > 0) {
                    processedTokens.add(token.substring(0, start));
                }
                processedTokens.add(operatorOrSeparator);
                return operatorOrSeparator.length();
            }
        }
        return 0;
    }
}
