package com.someone.util;

/*
  @Author Someone
 * @Date 2024/09/23 19:57
 */
import com.someone.debug.LogReceiver;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    public void modifyField(Class clazz, Object instance, String fieldName, Object value) {
        try {
            Field field = clazz.getField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception ignored) {}
    }

    public static Class<?> getCls(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public Object newInstance(String className, Class<?>[] paramsType, Object[] params) {
        try {
            Class<?> cls = getCls(className);
            if (cls != null) {
                Constructor<?> cons = cls.getDeclaredConstructor(paramsType);
                return cons.newInstance(params);
            }
        } catch (Exception e) {
            LogReceiver.i("ReflectUtils", "" + e);
        }
        return null;
    }

    public Class<?>[] getParamsType(Object[] params) {
        Class<?>[] parameterTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Integer) {
                parameterTypes[i] = Integer.TYPE;
            } else if (params[i] instanceof Double) {
                parameterTypes[i] = Double.TYPE;
            } else if (params[i] instanceof Float) {
                parameterTypes[i] = Float.TYPE;
            } else if (params[i] instanceof Long) {
                parameterTypes[i] = Long.TYPE;
            } else if (params[i] instanceof Short) {
                parameterTypes[i] = Short.TYPE;
            } else if (params[i] instanceof Byte) {
                parameterTypes[i] = Byte.TYPE;
            } else if (params[i] instanceof Boolean) {
                parameterTypes[i] = Boolean.TYPE;
            } else if (params[i] instanceof Character) {
                parameterTypes[i] = Character.TYPE;
            } else {
                parameterTypes[i] = params[i].getClass();
            }
        }
        return parameterTypes;
    }

    /*public void invokeConstructorMethod(Class clazz, Object instance, String methodName, Object... params) {
     try {
     Class<?>[] parameterTypes = new Class[params.length];
     for (int i = 0; i < params.length; i++) {
     if (params[i] instanceof Integer) {
     parameterTypes[i] = Integer.TYPE;
     } else if (params[i] instanceof Double) {
     parameterTypes[i] = Double.TYPE;
     } else if (params[i] instanceof Float) {
     parameterTypes[i] = Float.TYPE;
     } else if (params[i] instanceof Long) {
     parameterTypes[i] = Long.TYPE;
     } else if (params[i] instanceof Short) {
     parameterTypes[i] = Short.TYPE;
     } else if (params[i] instanceof Byte) {
     parameterTypes[i] = Byte.TYPE;
     } else if (params[i] instanceof Boolean) {
     parameterTypes[i] = Boolean.TYPE;
     } else if (params[i] instanceof Character) {
     parameterTypes[i] = Character.TYPE;
     } else {
     parameterTypes[i] = params[i].getClass();
     }
     }
     Constructor<?> constructor = clazz.getConstructor(parameterTypes);
     Object instance = constructor.newInstance(params);
     } catch (Exception e) {}
     }*/




    public Object invokeMethod(Class clazz, Object instance, String methodName, Object... params) {
        try {
            Class<?>[] parameterTypes = getParamsType(params);
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);

            return method.invoke(instance, params);
        } catch (Exception ignored) {}
        return null;
    }
}
