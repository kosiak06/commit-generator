package com.danish.commitgenerator;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent component;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Commit Generator";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new AppSettingsComponent();
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState s = AppSettingsState.getInstance();
        return !Objects.equals(component.getOllamaUrl(), s.ollamaUrl)
                || !Objects.equals(component.getOllamaModel(), s.ollamaModel);
    }

    @Override
    public void apply() {
        AppSettingsState s = AppSettingsState.getInstance();
        s.ollamaUrl = component.getOllamaUrl();
        s.ollamaModel = component.getOllamaModel();
    }

    @Override
    public void reset() {
        AppSettingsState s = AppSettingsState.getInstance();
        component.setOllamaUrl(s.ollamaUrl);
        component.setOllamaModel(s.ollamaModel);
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }
}
