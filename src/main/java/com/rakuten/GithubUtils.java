package com.rakuten;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class GithubUtils {

    private static final String GET_REPO_LIST_URL_TEMPLATE = "https://api.github.com/users/%s/repos?per_page=%d&page=%d";
    private static final String GET_README_FILE_URL_TEMPLATE = "https://raw.githubusercontent.com/%s/master/README.md";
    private static final int REPOSITORIES_PER_PAGE = 100;
    private static final String REPOSITORY_NAME_FIELD = "full_name";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static List<String> getRepositories(String username) {
        int page = 1;
        List<String> repositories = new LinkedList<>();
        while (true) {
            List<String> repositoriesByPage = getRepositories(username, REPOSITORIES_PER_PAGE, page++);
            if (repositoriesByPage.isEmpty()) break;
            repositories.addAll(repositoriesByPage);
        }
        return repositories;
    }

    public static List<String> getRepositories(String username, int perPage, int page) {
        String getRepoListUrl = String.format(GET_REPO_LIST_URL_TEMPLATE, username, perPage, page);
        try {
            ArrayNode repoListResponse = OBJECT_MAPPER.readValue(new URL(getRepoListUrl), ArrayNode.class);
            return repoListResponse.findValuesAsText(REPOSITORY_NAME_FIELD);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Optional<String> getReadmeFile(String repository) {
        String getReadmeFileUrl = String.format(GET_README_FILE_URL_TEMPLATE, repository);
        try (InputStream inputStream = new URL(getReadmeFileUrl).openStream()) {
            return Optional.of(new String(inputStream.readAllBytes()));
        } catch (IOException e) {
            System.out.println("can not get readme from " + repository);
            return Optional.empty();
        }
    }

}