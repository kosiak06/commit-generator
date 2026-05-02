package com.danish.commitgenerator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class CommitGeneratorPanel {

    private static final Logger LOG = Logger.getInstance(CommitGeneratorPanel.class);

    private final Project project;
    private final JPanel root;
    private final JBTextArea messageArea;
    private final JButton generateButton;
    private final JLabel statusLabel;

    public CommitGeneratorPanel(Project project) {
        this.project = project;

        root = new JPanel(new BorderLayout(4, 4));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        generateButton = new JButton("Generate Commit Message");
        generateButton.addActionListener(e -> generate());

        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e ->
                ShowSettingsUtil.getInstance().showSettingsDialog(project, AppSettingsConfigurable.class));

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.GRAY);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        toolbar.add(generateButton);
        toolbar.add(settingsButton);
        toolbar.add(statusLabel);

        messageArea = new JBTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        messageArea.setRows(6);

        JBScrollPane scrollPane = new JBScrollPane(messageArea);

        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(e -> copyToClipboard());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        south.add(copyButton);

        root.add(toolbar, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);
    }

    public JComponent getContent() {
        return root;
    }

    private void generate() {
        generateButton.setEnabled(false);
        statusLabel.setText("Getting staged diff…");
        statusLabel.setForeground(Color.GRAY);
        messageArea.setText("");

        LOG.info("Generate triggered for project: " + project.getName());

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                LOG.info("Fetching staged diff…");
                String diff = GitDiffService.getStagedDiff(project);

                if (diff.isBlank()) {
                    LOG.warn("No staged changes found");
                    setStatus("No staged changes found. Stage some files first.", true);
                    return;
                }
                LOG.info("Diff fetched, length=" + diff.length() + " chars");

                AppSettingsState settings = AppSettingsState.getInstance();
                boolean useOllama = "ollama".equals(settings.provider);

                if (useOllama) {
                    LOG.info("Using Ollama: url=" + settings.ollamaUrl + ", model=" + settings.ollamaModel);
                    setStatus("Calling Ollama (" + settings.ollamaModel + ")…", false);
                } else {
                    LOG.info("Using Gemini: model=" + settings.model);
                    setStatus("Calling Gemini (" + settings.model + ")…", false);
                }

                String message = useOllama
                        ? new OllamaApiService().generateCommitMessage(diff)
                        : new GeminiApiService().generateCommitMessage(diff);

                LOG.info("Response received, message length=" + message.length() + " chars");

                ApplicationManager.getApplication().invokeLater(() -> {
                    messageArea.setText(message);
                    statusLabel.setText("Done");
                    statusLabel.setForeground(new Color(0, 128, 0));
                    generateButton.setEnabled(true);
                });
            } catch (Exception ex) {
                LOG.error("Failed to generate commit message", ex);
                setStatus("Error: " + ex.getMessage(), true);
            }
        });
    }

    private void setStatus(String text, boolean error) {
        ApplicationManager.getApplication().invokeLater(() -> {
            statusLabel.setText(text);
            statusLabel.setForeground(error ? Color.RED : Color.GRAY);
            if (error) generateButton.setEnabled(true);
        });
    }

    private void copyToClipboard() {
        String text = messageArea.getText().trim();
        if (text.isEmpty()) return;
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(text), null);
        statusLabel.setText("Copied!");
        statusLabel.setForeground(new Color(0, 128, 0));
    }
}
