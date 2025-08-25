package com.zmt.aspect;

import cn.hutool.core.date.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class TimeAspect {
    @Pointcut("@annotation(com.zmt.anno.Log)")
    public void pointcut() {
    }

    // 使用切面的方式实现方法耗时记录
//    @Around("execution(* com.zmt.service.impl.*.*(..))")
//    public Object recordTimeLogByCron(ProceedingJoinPoint joinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//        Object result = joinPoint.proceed();
//        String point = joinPoint.getClass().getName() + "." + joinPoint.getSignature().getName();
//        long endTime = System.currentTimeMillis();
//        log.info("表达式耗时：" + (endTime - startTime) + " ms, 方法：" + point);
//        return result;
//    }

    // 使用注解的方式记录方法执行时间
    @Around("pointcut()")
    public Object recordTimeLogByAnno(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(joinPoint.getClass().getName() + "." + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        stopWatch.stop();
        log.info("注解耗时:" + stopWatch.shortSummary());
        return result;
    }
}
