package com.konggogi.veganlife.global.util;


import com.konggogi.veganlife.global.aop.domain.MethodSignature;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

@Component
public class AopUtils {
    public static MethodSignature extractMethodSignature(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return new MethodSignature(className, methodName);
    }
}
