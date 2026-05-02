package com.danish.commitgenerator;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
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

    // "gemini" or "ollama"
    public String provider = "gemini";

    // Gemini fields (API key is stored securely via PasswordSafe, not here)
    public String model = "gemini-2.0-flash";

    // Ollama fields
    public String ollamaUrl = "http://localhost:11434";
    public String ollamaModel = "llama3.2";

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    public static @Nullable String getApiKey() {
        return PasswordSafe.getInstance().getPassword(credentialAttributes());
    }

    public static void setApiKey(@Nullable String key) {
        PasswordSafe.getInstance().setPassword(credentialAttributes(), key);
    }

    private static CredentialAttributes credentialAttributes() {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("CommitGenerator", "GeminiApiKey")
        );
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