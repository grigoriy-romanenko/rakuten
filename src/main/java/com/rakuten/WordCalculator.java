package com.rakuten;

import java.util.*;
import java.util.stream.Collectors;

public class WordCalculator {

    private static final String GITHUB_USERNAME = "spotify";
    private static final int RESULTS_LIMIT = 3;
    private static final String WORD_DELIMITER = "(\\s|\\n)+";
    private static final int WORD_MIN_LENGTH = 5;

    public static void main(String[] args) {
        GithubUtils.getRepositories(GITHUB_USERNAME).stream()
                .parallel()
                .map(GithubUtils::getReadmeFile)
                .filter(Optional::isPresent).map(Optional::get)
                .map(WordCalculator::getWordCount)
                .flatMap(Collection::stream)
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum))
                .entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(RESULTS_LIMIT)
                .forEach(System.out::println);
    }

    public static Set<Map.Entry<String, Integer>> getWordCount(String readmeText) {
        String[] words = readmeText.split(WORD_DELIMITER);
        Map<String, Integer> wordCountMap = new HashMap<>();
        for (String word : words) {
            if (word.length() < WORD_MIN_LENGTH) continue;
            Integer count = wordCountMap.get(word);
            if (count == null) count = 0;
            wordCountMap.put(word, ++count);
        }
        return wordCountMap.entrySet();
    }

}