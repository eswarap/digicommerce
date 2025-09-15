package org.woven.digicommerce.tokensvc.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginEntryExitAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginEntryExitAspect.class);
    
    @Before("@annotation(org.woven.digicommerce.tokensvc.annotation.LoginEntryExit)")
    public void logEntry(JoinPoint joinPoint) {
        logger.info("Entering method: {}", joinPoint.getSignature().getName());
    }
    
    @After("@annotation(org.woven.digicommerce.tokensvc.annotation.LoginEntryExit)")
    public void logExit(JoinPoint joinPoint) {
        logger.info("Exiting method: {}", joinPoint.getSignature().getName());
    }
}