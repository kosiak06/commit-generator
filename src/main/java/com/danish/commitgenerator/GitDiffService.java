package com.danish.commitgenerator;

import com.intellij.openapi.project.Project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class GitDiffService {

    public static String getStagedDiff(Project project) throws Exception {
        String basePath = project.getBasePath();
        if (basePath == null) {
            throw new IllegalStateException("Cannot determine project path.");
        }

        ProcessBuilder pb = new ProcessBuilder("git", "diff", "--staged")
                .directory(Paths.get(basePath).toFile())
                .redirectErrorStream(true);

        Process process = pb.start();
        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("git diff failed (exit " + exitCode + "): " + output);
        }

        return output;
    }
}
