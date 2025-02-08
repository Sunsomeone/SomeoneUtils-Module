package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/23 19:57
 */

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {

    @NonNull
    public static Class<?> getCls(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    public void modifyField(@NonNull Class<?> clazz, Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public Object newInstance(String className, Class<?>[] paramsType, Object[] params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class<?> cls = getCls(className);
        Constructor<?> cons = cls.getDeclaredConstructor(paramsType);
        return cons.newInstance(params);
    }

    public Class<?>[] getParamsType(@NonNull Object[] params) {
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


    public Object invokeMethod(@NonNull Class<?> clazz, Object instance, String methodName, Object... params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] parameterTypes = getParamsType(params);
        Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
        return method.invoke(instance, params);
    }
}
