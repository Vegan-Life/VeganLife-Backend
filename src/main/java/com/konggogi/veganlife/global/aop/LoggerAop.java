package com.konggogi.veganlife.global.aop;

import static com.konggogi.veganlife.global.util.AopUtils.extractMethodSignature;

import com.konggogi.veganlife.global.aop.domain.MethodSignature;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggerAop {
    @Pointcut(
            "execution(* com.konggogi.veganlife..*repository..*(..)) || "
                    + "execution(* com.konggogi.veganlife..*controller..*(..)) || "
                    + "execution(* com.konggogi.veganlife..*service..*(..))")
    private void cuts() {}

    @Before("cuts()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = extractMethodSignature(joinPoint);
        log.debug("[START] - {}.{}", signature.className(), signature.methodName());

        Object[] args = joinPoint.getArgs();
        Stream.of(args)
                .filter(Objects::nonNull)
                .forEach(
                        arg ->
                                log.debug(
                                        "type: {} | value: {}",
                                        arg.getClass().getSimpleName(),
                                        arg));
    }

    @AfterReturning(value = "cuts()")
    public void afterReturn(JoinPoint joinPoint) {
        MethodSignature signature = extractMethodSignature(joinPoint);
        log.debug("[END] - {}.{}", signature.className(), signature.methodName());
    }

    @AfterThrowing(pointcut = "cuts()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        MethodSignature signature = extractMethodSignature(joinPoint);
        log.error(
                "[Exception] - {}.{} - message: {}",
                signature.className(),
                signature.methodName(),
                e.getMessage());
    }
}
