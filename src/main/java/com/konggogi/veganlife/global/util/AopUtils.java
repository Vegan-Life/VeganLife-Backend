package com.konggogi.veganlife.global.util;


import com.konggogi.veganlife.global.aop.domain.MethodSignature;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.method.HandlerMethod;

public class AopUtils {
    public static MethodSignature extractMethodSignature(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return new MethodSignature(className, methodName);
    }

    public static MethodSignature extractMethodSignature(HandlerMethod handlerMethod) {
        String className = handlerMethod.getBeanType().getSimpleName();
        String methodName = handlerMethod.getMethod().getName();
        return new MethodSignature(className, methodName);
    }
}
