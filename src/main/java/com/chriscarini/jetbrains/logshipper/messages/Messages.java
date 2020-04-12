package com.chriscarini.jetbrains.logshipper.messages;

import com.intellij.AbstractBundle;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;


/**
 * The messages for the Logshipper.
 */
public final class Messages {
  @NonNls
  private static final String BUNDLE = "messages.logshipper";
  private static Reference<ResourceBundle> bundle;

  private Messages() {
  }

  public static String message(@NotNull @NonNls @PropertyKey(resourceBundle = BUNDLE) final String key,
      @NotNull final Object... params) {
    return AbstractBundle.message(getBundle(), key, params);
  }

  private static ResourceBundle getBundle() {
    ResourceBundle bundle = com.intellij.reference.SoftReference.dereference(Messages.bundle);
    if (bundle == null) {
      bundle = ResourceBundle.getBundle(BUNDLE);
      Messages.bundle = new SoftReference<>(bundle);
    }
    return bundle;
  }
}
