package edu.sjsu.cmpe275.aop.aspect;
 
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.aspectj.lang.annotation.Around;

import java.io.IOException;

@Aspect
@Order(1)
public class RetryAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */

	@Around("execution(public void edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public void dummyAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.printf("Retry aspect prior to the execution of the metohd %s\n", joinPoint.getSignature().getName());
		Object result = null;
		int numErrors = 0;
		do {
			try {
				result = joinPoint.proceed();
				System.out.printf("Finished the execution of the metohd %s with result %s\n", joinPoint.getSignature().getName(), result);
			} catch (IOException e) {
				//e.printStackTrace();
				numErrors = numErrors + 1;
				System.out.printf("IO Exception in method %s, retry #%d\n", joinPoint.getSignature().getName(), numErrors);
			}
		} while (numErrors > 0 && numErrors < 3);
	}

}
