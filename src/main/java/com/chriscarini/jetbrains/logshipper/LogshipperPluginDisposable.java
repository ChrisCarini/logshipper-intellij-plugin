package com.chriscarini.jetbrains.logshipper;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


/**
 * The service is intended to be used instead of a project/application as a parent disposable.
 * <p>
 * See: ["Choosing a disposable parent"](https://plugins.jetbrains.com/docs/intellij/disposers.html?from=IncorrectParentDisposable#choosing-a-disposable-parent)
 */
@Service({Service.Level.APP, Service.Level.PROJECT})
public final class LogshipperPluginDisposable implements Disposable {
    public static @NotNull Disposable getInstance() {
        return ApplicationManager.getApplication().getService(LogshipperPluginDisposable.class);
    }

    public static @NotNull Disposable getInstance(@NotNull Project project) {
        return project.getService(LogshipperPluginDisposable.class);
    }

    @Override
    public void dispose() {
    }
}