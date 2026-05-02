# Commit Generator — IntelliJ Plugin

An IntelliJ IDEA plugin that generates conventional git commit messages from your staged diff using a local **Ollama** model — no API key, no cloud, fully private.

## Features

- Reads `git diff --staged` and produces a single-line commit message in `type(scope): description` format
- Supports Conventional Commits types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `perf`
- Works with any locally running Ollama model (llama3, mistral, codellama, etc.)
- One-click copy to clipboard

## Requirements

- IntelliJ IDEA 2025.2+
- Java 21
- A running [Ollama](https://ollama.com) instance

## Setup

1. Install and start Ollama, then pull a model:
   ```bash
   ollama pull llama3.2
   ```
2. Open **Settings → Tools → Commit Generator**
3. Set the Ollama URL (default: `http://localhost:11434`) and model name
4. Click **OK**

## Usage

1. Stage the files you want to commit (`git add ...` or via the IDE)
2. Open the **Commit Generator** tool window (right sidebar)
3. Click **Generate Commit Message**
4. Review the result, then click **Copy to Clipboard** and paste it into the commit dialog

## Project Structure

```
src/main/java/com/danish/commitgenerator/
├── AppSettingsComponent.java           Settings UI form
├── AppSettingsConfigurable.java        Settings page registration
├── AppSettingsState.java               Persistent settings (URL, model)
├── CommitGeneratorPanel.java           Tool window UI
├── CommitGeneratorToolWindowFactory.java
├── GitDiffService.java                 Runs git diff --staged
└── OllamaApiService.java               Ollama REST client
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
