package com.konggogi.veganlife.global.aop;

import static com.konggogi.veganlife.global.util.AopUtils.extractMethodSignature;

import com.konggogi.veganlife.global.aop.domain.MethodSignature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class TimeTraceAop {

    @Around("execution(* com.konggogi.veganlife..*repository..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        MethodSignature signature = extractMethodSignature(joinPoint);
        try {
            stopWatch.start();
            return joinPoint.proceed();

        } finally {
            stopWatch.stop();
            log.info(
                    "[TIME] {}.{} : {}Ms",
                    signature.className(),
                    signature.methodName(),
                    stopWatch.getTotalTimeMillis());
        }
    }
}
