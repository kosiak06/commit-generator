package com.danish.commitgenerator;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;

public class AppSettingsComponent {

    private final JPanel panel;

    private final ComboBox<String> providerComboBox = new ComboBox<>(new String[]{"gemini", "ollama"});

    // Gemini
    private final JBPasswordField apiKeyField = new JBPasswordField();
    private final ComboBox<String> modelComboBox = new ComboBox<>(new String[]{
            "gemini-2.0-flash",
            "gemini-1.5-flash",
            "gemini-1.5-pro"
    });
    private final JPanel geminiPanel;

    // Ollama
    private final JBTextField ollamaUrlField = new JBTextField();
    private final JBTextField ollamaModelField = new JBTextField();
    private final JPanel ollamaPanel;

    public AppSettingsComponent() {
        apiKeyField.setColumns(40);
        ollamaUrlField.setColumns(40);
        ollamaModelField.setColumns(20);

        geminiPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Gemini API key:"), apiKeyField, 1, false)
                .addLabeledComponent(new JBLabel("Model:"), modelComboBox, 1, false)
                .getPanel();

        ollamaPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Ollama URL:"), ollamaUrlField, 1, false)
                .addLabeledComponent(new JBLabel("Model:"), ollamaModelField, 1, false)
                .getPanel();

        providerComboBox.addActionListener(e -> updateVisibility());

        panel = new JPanel(new BorderLayout());
        JPanel top = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Provider:"), providerComboBox, 1, false)
                .addComponent(geminiPanel)
                .addComponent(ollamaPanel)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        panel.add(top, BorderLayout.NORTH);

        updateVisibility();
    }

    private void updateVisibility() {
        boolean isOllama = "ollama".equals(providerComboBox.getSelectedItem());
        geminiPanel.setVisible(!isOllama);
        ollamaPanel.setVisible(isOllama);
    }

    public JPanel getPanel() {
        return panel;
    }

    public String getProvider() {
        return (String) providerComboBox.getSelectedItem();
    }

    public void setProvider(String provider) {
        providerComboBox.setSelectedItem(provider);
        updateVisibility();
    }

    public String getApiKey() {
        return new String(apiKeyField.getPassword());
    }

    public void setApiKey(String key) {
        apiKeyField.setText(key);
    }

    public String getModel() {
        return (String) modelComboBox.getSelectedItem();
    }

    public void setModel(String model) {
        modelComboBox.setSelectedItem(model);
    }

    public String getOllamaUrl() {
        return ollamaUrlField.getText();
    }

    public void setOllamaUrl(String url) {
        ollamaUrlField.setText(url);
    }

    public String getOllamaModel() {
        return ollamaModelField.getText();
    }

    public void setOllamaModel(String model) {
        ollamaModelField.setText(model);
    }
}