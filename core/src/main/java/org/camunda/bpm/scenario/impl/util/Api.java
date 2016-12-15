package org.camunda.bpm.scenario.impl.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/*
 * Attention, code smells! But: it has proven to be quite useful and
 * pragmatic for small projects like Camunda BPM Community Extensions.
 *
 * Some people like really old cheese, btw. And if it turns out one
 * has to eat this more often than bearable, one can still decide
 * to go for the more correct approach, maintain Camunda BPM version
 * version specific branches, merge up and down and release Camunda
 * BPM version specific Community Extensions.
 *
 * (Maybe even with a Camunda BPM '7.5'-ish maven dependency version
 * 'classifier' element? I like. But it's actually not so often used.)
 *
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Api {

  private static Logger log = Logger.getLogger(Api.class.getName());

  private String className;
  private String methodName;
  private Class<?>[] parameterTypes;
  private String signature;

  // maps signatures to a "supported" Boolean, true means supported.
  // false and null means not supported, but null means that a warning
  // was already logged, too. We don't want to warn a thousand times.
  private static Map<String, Boolean> support = new HashMap<String, Boolean>();

  private Api(String className, String methodName, Class<?>... parameterTypes) {
    this.className = className;
    this.methodName = methodName;
    this.parameterTypes = parameterTypes;
    this.signature = signature();
    if (!support.containsKey(signature))
      support.put(signature, supported());
  }

  public static Api feature(String className) {
    return feature(className, null);
  }

  public static Api feature(String className, String methodName, Class<?>... parameterTypes) {
    return new Api(className, methodName, parameterTypes);
  }

  public void fail() {
    fail(message());
  }

  public void fail(String message) {
    if (!isSupported()) {
      throw new UnsupportedOperationException(message);
    }
  }

  public boolean warn() {
    return warn(message());
  }

  public boolean warn(String message) {
    Boolean supported = support.get(signature);
    if (supported != null && !supported) {
      support.put(signature, null);
      log.warning(message);
    }
    return isSupported();
  }

  public boolean isSupported() {
    Boolean s = support.get(signature);
    return s != null && s;
  }

  private String message() {
    StringBuffer buffer = new StringBuffer("Usage of API '").append(signature)
      .append("' requested, but not supported by the classes found in classpath.");
    return buffer.toString();
  }

  private String signature() {
    StringBuffer buffer = new StringBuffer(className);
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
    return buffer.toString();
  }

  private boolean supported() {
    try {
      Class cls = Class.forName(className);
      if (methodName != null) {
        cls.getMethod(methodName, parameterTypes);
      }
    } catch (ClassNotFoundException e) {
      return false;
    } catch (NoSuchMethodException e) {
      return false;
    }
    return true;
  }

}
