package org.camunda.bpm.scenario.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Feature {

  static Logger log = Logger.getLogger(Feature.class.getName());

  public static void failIfNotSupported(String className) {
    failIfNotSupported(className, null);
  }

  public static void failIfNotSupported(String className, String methodName, Class<?>... parameterTypes) {
    if (!isSupported(className, methodName, parameterTypes)) {
      throw new UnsupportedOperationException(message(className, methodName, parameterTypes));
    };
  }

  public static boolean isSupportedWithWarn(String className) {
    return isSupported(className, null, true);
  }

  public static boolean isSupportedWithWarn(String className, String methodName, Class<?>... parameterTypes) {
    return isSupported(className, methodName, true, parameterTypes);
  }

  public static boolean isSupported(String className) {
    return isSupported(className, null);
  }

  public static boolean isSupported(String className, String methodName, Class<?>... parameterTypes) {
    return isSupported(className, methodName, false);
  }

  private static boolean isSupported(String className, boolean logWarning) {
    return isSupported(className, null, logWarning);
  }

  private static boolean isSupported(String className, String methodName, boolean logWarning, Class<?>... parameterTypes) {
    try {
      Class.forName(className).getMethod(methodName, parameterTypes);
    } catch (ClassNotFoundException e) {
      if (logWarning)
        log.warning(message(className, methodName, parameterTypes));
      return false;
    } catch (NoSuchMethodException e) {
      if (logWarning)
        log.warning(message(className, methodName, parameterTypes));
      return false;
    }
    return true;
  }

  private static String message(String className, String methodName, Class<?>... parameterTypes) {
    StringBuffer buffer = new StringBuffer("Camunda BPM API Feature");
    buffer.append(" '").append(className.substring(className.lastIndexOf('.') + 1));
    if (methodName != null) {
      buffer.append(".").append(methodName);
      buffer.append("(");
      if (parameterTypes != null && parameterTypes.length > 0) {
        Iterator<Class<?>> it = Arrays.asList(parameterTypes).iterator();
        while (it.hasNext()) {
          buffer.append(it.next().getSimpleName());
          if (it.hasNext())
            buffer.append(", ");
        }
      }
      buffer.append(")");
    }
    buffer.append("' requested, but not supported by the used version.");
    return buffer.toString();
  }

}
