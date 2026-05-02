package com.danish.commitgenerator;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class AppSettingsComponent {

    private final JPanel panel;
    private final JBTextField ollamaUrlField = new JBTextField();
    private final JBTextField ollamaModelField = new JBTextField();

    public AppSettingsComponent() {
        ollamaUrlField.setColumns(40);
        ollamaModelField.setColumns(20);

        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Ollama URL:"), ollamaUrlField, 1, false)
                .addLabeledComponent(new JBLabel("Model:"), ollamaModelField, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return panel;
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
