package edu.sjsu.cmpe275.aop.aspect;

import edu.sjsu.cmpe275.aop.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.SecretStatsImpl;

import java.util.Map;
import java.util.UUID;

@Aspect
@Order(0)
public class StatsAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */ 

	@Autowired SecretStatsImpl stats;


	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.SecretService.createSecret(..))", returning = "val")
	public void afterCreatAdvice(JoinPoint joinPoint, UUID val) {
		System.out.printf("Doing stats after the executuion of the metohd %s\n", joinPoint.getSignature().getName());
		String userId = (String)joinPoint.getArgs()[0];
		String secret = (String)joinPoint.getArgs()[1];
		UUID secretId = val;
		stats.addCreateByRecord(secretId, userId);
		stats.addSecretAccessRecord(secretId, userId);
//		stats.printAllCreateBy();
		if (secret.length() > stats.getLengthOfLongestSecret()) {
			stats.setLengthOfLongestSecret(secret.length());
		}
	}

	@After("execution(public void edu.sjsu.cmpe275.aop.SecretService.shareSecret(..))")
	public void afterShareAdvice(JoinPoint joinPoint){
		System.out.printf("Doing stats After the executuion of the metohd %s\n", joinPoint.getSignature().getName());
		String fromUser = (String)joinPoint.getArgs()[0];
		UUID secretId = (UUID)joinPoint.getArgs()[1];
		String toUser = (String)joinPoint.getArgs()[2];
		stats.addShareRecord(toUser, secretId, fromUser);
		stats.addSecretAccessRecord(secretId, toUser);
		stats.printAllShare();
		stats.addInboundSharingCount(toUser, secretId);
		if (!stats.checkOwnSecret(fromUser, secretId)) stats.addOutboundResharingCount(fromUser, secretId);
	}

	@After ("execution(public void edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
	public void afterUnshareAdvice(JoinPoint joinPoint){
		System.out.printf("Doing stats After the executuion of the metohd %s\n", joinPoint.getSignature().getName());
		String fromUser = (String)joinPoint.getArgs()[0];
		UUID secretId = (UUID)joinPoint.getArgs()[1];
		String toUser = (String)joinPoint.getArgs()[2];
		if (stats.checkOwnSecret(fromUser, secretId)) {
			stats.deleteAllShareRecords(toUser, secretId);
			stats.removeSecretAccessRecord(secretId, toUser);
		} else {
			stats.deleteAShareRecord(toUser, secretId, fromUser);
			if (!stats.checkBeShared(toUser, secretId)) stats.removeSecretAccessRecord(secretId, toUser);
		}
		stats.printAllShare();
	}


}
