package com.danish.commitgenerator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class CommitGeneratorToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CommitGeneratorPanel panel = new CommitGeneratorPanel(project);
        Content content = ContentFactory.getInstance().createContent(panel.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
