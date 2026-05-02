package com.danish.commitgenerator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.danish.commitgenerator.AppSettingsState",
        storages = @Storage("CommitGeneratorSettings.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public String ollamaUrl = "http://localhost:11434";
    public String ollamaModel = "llama3.2";

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Override
    public @Nullable AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
