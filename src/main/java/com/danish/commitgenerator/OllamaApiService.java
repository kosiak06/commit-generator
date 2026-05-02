package com.danish.commitgenerator;

import com.intellij.openapi.diagnostic.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OllamaApiService {

    private static final Logger LOG = Logger.getInstance(OllamaApiService.class);
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("\"response\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    public String generateCommitMessage(String diff) throws Exception {
        AppSettingsState settings = AppSettingsState.getInstance();
        String baseUrl = settings.ollamaUrl == null || settings.ollamaUrl.isBlank()
                ? "http://localhost:11434"
                : settings.ollamaUrl.strip().replaceAll("/+$", "");
        String model = (settings.ollamaModel == null || settings.ollamaModel.isBlank())
                ? "llama3.2"
                : settings.ollamaModel.strip();

        String url = baseUrl + "/api/generate";
        LOG.info("POST " + url + " model=" + model);

        String requestBody = buildRequestBody(diff, model);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(120))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        LOG.info("Sending request to Ollama…");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        LOG.info("Ollama responded with HTTP " + response.statusCode());

        if (response.statusCode() != 200) {
            LOG.error("Ollama error response: " + response.body());
            throw new RuntimeException("Ollama error " + response.statusCode() + ": " + response.body());
        }

        String result = extractResponse(response.body());
        LOG.info("Extracted response: " + result);
        return result;
    }

    private String buildRequestBody(String diff, String model) {
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

        return "{\"model\":\"" + escapeJsonString(model) + "\","
                + "\"prompt\":\"" + escapeJsonString(prompt) + "\","
                + "\"stream\":false}";
    }

    private String extractResponse(String json) {
        Matcher matcher = RESPONSE_PATTERN.matcher(json);
        if (!matcher.find()) {
            LOG.error("Unexpected Ollama response format: " + json);
            throw new RuntimeException("Unexpected response format from Ollama.");
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
