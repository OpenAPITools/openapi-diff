package org.openapitools.openapidiff.core.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class I18n {

  private static final String BUNDLE_NAME = "i18n.messages";
  private static volatile Locale currentLocale = Locale.ENGLISH;
  private static volatile ResourceBundle bundle = loadBundle(currentLocale);

  private I18n() {}

  public static synchronized void setLocale(Locale locale) {
    bundle = loadBundle(locale);
    currentLocale = locale;
  }

  public static Locale getLocale() {
    return currentLocale;
  }

  public static String getMessage(String key) {
    return bundle.getString(key);
  }

  public static String getMessage(String key, Object... args) {
    return String.format(bundle.getString(key), args);
  }

  public static Locale parseLocale(String lang) {
    switch (lang.toLowerCase().replace("_", "-")) {
      case "zh-hant":
      case "zh-tw":
        return Locale.TRADITIONAL_CHINESE;
      case "zh-hans":
      case "zh-cn":
        return Locale.SIMPLIFIED_CHINESE;
      case "en":
      default:
        return Locale.ENGLISH;
    }
  }

  private static ResourceBundle loadBundle(Locale locale) {
    return ResourceBundle.getBundle(BUNDLE_NAME, locale, new UTF8Control());
  }

  private static class UTF8Control extends ResourceBundle.Control {
    @Override
    public ResourceBundle newBundle(
        String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
        throws IllegalAccessException, InstantiationException, IOException {
      String bundleName = toBundleName(baseName, locale);
      String resourceName = toResourceName(bundleName, "properties");
      InputStream stream = loader.getResourceAsStream(resourceName);
      if (stream != null) {
        try {
          return new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } finally {
          stream.close();
        }
      }
      return super.newBundle(baseName, locale, format, loader, reload);
    }
  }
}
