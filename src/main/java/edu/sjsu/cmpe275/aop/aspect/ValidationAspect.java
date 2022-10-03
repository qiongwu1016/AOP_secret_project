package edu.sjsu.cmpe275.aop.aspect;

import edu.sjsu.cmpe275.aop.SecretStatsImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.util.UUID;

@Aspect
@Order(1)
public class ValidationAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */
	@Autowired SecretStatsImpl stats;

	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public void validateNullAdvice(JoinPoint joinPoint) {
		System.out.printf("Doing validation prior to the execution of the method %s\n", joinPoint.getSignature().getName());
		for (Object arg: joinPoint.getArgs()) {
			if (arg == null) {
				System.out.printf("throw\n");
				throw new IllegalArgumentException("One or more argument is null");
			}
		}
	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.createSecret(..))")
	public void validateSecretLengthAdvice(JoinPoint joinPoint) {
		System.out.printf("Doing validation prior to the execution of the method %s\n", joinPoint.getSignature().getName());
		if (joinPoint.getArgs()[1] != null) {
			String secret = (String)joinPoint.getArgs()[1];
			if (secret.length() > 128) throw new IllegalArgumentException("Secret content more than 128 characters");
		}
	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.*shareSecret(..))")
	public void validateSameArgumentAdvice(JoinPoint joinPoint) {
		System.out.printf("Doing validation prior to the execution of the method %s\n", joinPoint.getSignature().getName());
		String fromUser = (String)joinPoint.getArgs()[0];
		String toUser = (String)joinPoint.getArgs()[2];
		if (fromUser.equals(toUser)) throw new IllegalArgumentException("The userId and targetUserId are equal");
	}

}
