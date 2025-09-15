package org.woven.digicommerce.tokensvc.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodProcessingTimeAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(MethodProcessingTimeAspect.class);
    
    @Around("@annotation(org.woven.digicommerce.tokensvc.annotation.MethodProcessingTime)")
    public Object logProcessingTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        logger.info("Method {} took {} ms", joinPoint.getSignature().getName(), endTime - startTime);
        return result;
    }
}