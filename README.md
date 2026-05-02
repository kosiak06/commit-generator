# Commit Generator — IntelliJ Plugin

An IntelliJ IDEA plugin that generates conventional git commit messages from your staged diff using AI.
Supports **Gemini** (cloud) and **Ollama** (local/private).

## Features

- Reads `git diff --staged` and produces a single-line commit message in `type(scope): description` format
- Supports Conventional Commits types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `perf`
- Two provider options: Gemini API or any local Ollama model
- API key stored securely in the OS credential store (Keychain / libsecret / Windows Credential Manager) — never written to disk in plain text
- One-click copy to clipboard

## Requirements

- IntelliJ IDEA 2025.2+
- Java 21
- For Gemini: a [Google AI Studio](https://aistudio.google.com) API key
- For Ollama: a running [Ollama](https://ollama.com) instance

## Setup

1. Open **Settings → Tools → Commit Generator**
2. Choose a provider:
   - **Gemini** — paste your API key, select a model (`gemini-2.0-flash` recommended)
   - **Ollama** — set the base URL (default: `http://localhost:11434`) and model name
3. Click **OK**

## Usage

1. Stage the files you want to commit (`git add ...` or via the IDE)
2. Open the **Commit Generator** tool window (right sidebar)
3. Click **Generate Commit Message**
4. Review the result, then click **Copy to Clipboard** and paste it into the commit dialog

## Project Structure

```
src/main/java/com/danish/commitgenerator/
├── AppSettingsComponent.java       Settings UI form
├── AppSettingsConfigurable.java    Settings page registration
├── AppSettingsState.java           Persistent settings + PasswordSafe integration
├── CommitGeneratorPanel.java       Tool window UI
├── CommitGeneratorToolWindowFactory.java
├── GeminiApiService.java           Gemini REST client
├── GitDiffService.java             Runs git diff --staged
└── OllamaApiService.java           Ollama REST client
```

## Building

```bash
./gradlew buildPlugin
```

The plugin ZIP will be in `build/distributions/`. Install it via **Settings → Plugins → Install from disk**.

## Running in a sandbox IDE

```bash
./gradlew runIde
```
