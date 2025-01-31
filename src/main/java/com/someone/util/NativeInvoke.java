package com.someone.util;
import java.lang.reflect.Method;

/**
 * @Author Someone
 * @Date 2024/11/28 17:51
 */
public class NativeInvoke {

    public static native Object invoke(Object classInstance, Method methodName , Object... params);

}


