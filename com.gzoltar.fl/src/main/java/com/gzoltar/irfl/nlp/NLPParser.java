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
package com.gzoltar.irfl.nlp;

import com.gzoltar.core.util.FileUtils;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;


import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NLPParser {
    Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    private final static String SEPARATORS_PATH = String.valueOf(NLPParser.class.getResource("nlp/separators.txt"));
    private final static String SINGLE_CHAR_OPERATORS_PATH = String.valueOf(NLPParser.class.getResource("nlp/singleCharOperators.txt"));
    private final static String MULTI_CHAR_OPERATORS_PATH = String.valueOf(NLPParser.class.getResource("nlp/multiCharOperators.txt"));
    private final static String JAVA_KEYWORDS_PATH = String.valueOf(NLPParser.class.getResource("nlp/keywords.txt"));
    private final static String ENGLISH_KEYWORDS_PATH = String.valueOf(NLPParser.class.getResource("nlp/englishWords.txt"));
    private final static Set<String> SEPARATORS = new HashSet<>();
    private final static Set<String> SINGLE_CHAR_OPERATORS = new HashSet<>();
    private final static Set<String> MULTI_CHAR_OPERATORS = new TreeSet<>(Comparator.comparingInt(String::length).reversed().thenComparing(Comparator.naturalOrder()));
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

    private static final Predicate<String> isKeyword = JAVA_KEYWORDS::contains;
    private static final Predicate<String> isBlank = String::isBlank;
    private static final Predicate<String> isImportStatement = s -> s.startsWith("import ");
    private static final Predicate<String> isPackageStatement = s -> s.startsWith("package ");
    private static final Predicate<String> isCommentLine = s -> s.startsWith("//") || s.startsWith("/*") || s.startsWith("*");
    private static final Predicate<String> isSeparator = s -> SEPARATORS.contains(s) || s.equals(".");
    private static final Predicate<String> isEnglishWord = ENGLISH_KEYWORDS::contains;

    private static final Predicate<String> hasOnlySeparators = s -> {
        for (char c : s.toCharArray()) {
            if (!isSeparator.test(String.valueOf(c))) return false;
        }
        return true;
    };

    private static final Predicate<String> isRelevantStatement = isBlank
            .or(isImportStatement)
            .or(isPackageStatement)
            .or(isCommentLine)
            .or(hasOnlySeparators)
            .negate();

    private List<String> filterRelevantLines(List<String> tokens) {
        return tokens.stream().filter(isRelevantStatement).collect(Collectors.toList());
    }

    private List<String> filterTokens(List<String> tokens) {
        return tokens.stream()
                .map(this::removeLeadingTrailingPunctuation)
                .filter(isKeyword.negate())
                .filter(isEnglishWord.negate())
                .filter(isSeparator.negate())
                .filter(isBlank.negate())
                .map(token -> (String) stemmer.stem(token))
                .filter(isKeyword.negate())
                .filter(isEnglishWord.negate())
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private String removeLeadingTrailingPunctuation(String token) {
        return token.replaceAll("^\\p{Punct}+|\\p{Punct}+$", "");
    }

    public List<String> processTokens(List<String> tokens) {
        List<String> processedTokens = new ArrayList<>();
        List<String> tokensToProcess = new ArrayList<>();

        tokens = filterRelevantLines(tokens);

        for(String token : tokens) {
            tokensToProcess.addAll(List.of(token.split(" ")));
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
            processedTokens.addAll(List.of(token.split("\\s+")));
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