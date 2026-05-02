package com.danish.commitgenerator;

import com.intellij.openapi.diagnostic.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiApiService {

    private static final Logger LOG = Logger.getInstance(GeminiApiService.class);
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";
    private static final Pattern TEXT_PATTERN = Pattern.compile("\"text\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    public String generateCommitMessage(String diff) throws Exception {
        AppSettingsState settings = AppSettingsState.getInstance();
        String apiKey = AppSettingsState.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Gemini API key is not set. Configure it in Settings > Tools > Commit Generator.");
        }

        String url = String.format(API_URL, settings.model, apiKey);
        LOG.info("POST Gemini model=" + settings.model);

        String requestBody = buildRequestBody(diff);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        LOG.info("Sending request to Gemini…");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        LOG.info("Gemini responded with HTTP " + response.statusCode());

        if (response.statusCode() != 200) {
            LOG.error("Gemini error response: " + response.body());
            throw new RuntimeException("Gemini API error " + response.statusCode() + ": " + response.body());
        }

        String result = extractText(response.body());
        LOG.info("Extracted response: " + result);
        return result;
    }

    private String buildRequestBody(String diff) {
        String prompt = "Output a single-line git commit message for the diff below.\n"
                + "Format: <type>(<scope>): <description>\n"
                + "Types: feat, fix, docs, style, refactor, test, chore, perf\n"
                + "STRICT RULES — violations are not allowed:\n"
                + "- ONE LINE ONLY. No newlines, no body, no bullet points.\n"
                + "- Under 72 characters total.\n"
                + "- Imperative mood: \"add\" not \"added\", \"fix\" not \"fixed\".\n"
                + "- Output ONLY the commit message. Nothing else.\n"
                + "Example: feat(auth): add JWT refresh token support\n\n"
                + "Git diff:\n" + diff;

        return "{\"contents\":[{\"parts\":[{\"text\":\"" + escapeJsonString(prompt) + "\"}]}],"
                + "\"generationConfig\":{\"maxOutputTokens\":256}}";
    }

    private String extractText(String json) {
        Matcher matcher = TEXT_PATTERN.matcher(json);
        if (!matcher.find()) {
            LOG.error("Unexpected Gemini response format: " + json);
            throw new RuntimeException("Unexpected response format from Gemini API.");
        }
        return firstLine(unescapeJsonString(matcher.group(1)));
    }

    private static String firstLine(String s) {
        return s.trim().lines().map(String::trim).filter(l -> !l.isBlank()).findFirst().orElse(s.trim());
    }

    private static String escapeJsonString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJsonString(String s) {
        return s.replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"");
    }
}
