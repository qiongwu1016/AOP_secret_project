package edu.sjsu.cmpe275.aop.aspect;
 
import edu.sjsu.cmpe275.aop.NotAuthorizedException;
import edu.sjsu.cmpe275.aop.SecretServiceImpl;
import edu.sjsu.cmpe275.aop.SecretStatsImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Aspect

public class AccessControlAspect {
	/***
	 * Following is a dummy implementation of this aspect.
	 * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
	 */
	@Autowired
	SecretStatsImpl stats;

	@Before(value = "execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..))")
	public void readSecretCheck(JoinPoint joinPoint) throws NotAuthorizedException {
		System.out.printf("Access control prior to the execution of the method %s\n", joinPoint.getSignature().getName());
		String userId = (String)joinPoint.getArgs()[0];
		UUID secretId = (UUID)joinPoint.getArgs()[1];
		if (!stats.checkSecretIdExist(secretId)) {
			throw new NotAuthorizedException("The secretId does not exist.");
		}
		if  (!stats.checkOwnSecret(userId, secretId) && !stats.checkBeShared(userId, secretId)) {
			throw new NotAuthorizedException("Read failed because user has no access. " + userId);
		}
	}

	@Before(value = "execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..))")
	public void shareSecretCheck(JoinPoint joinPoint) throws NotAuthorizedException {
		System.out.printf("Access control prior to the execution of the method %s\n", joinPoint.getSignature().getName());
		String fromUser = (String)joinPoint.getArgs()[0];
		UUID secretId = (UUID)joinPoint.getArgs()[1];
		String toUser = (String)joinPoint.getArgs()[2];
		if (!stats.checkOwnSecret(fromUser, secretId) && !stats.checkBeShared(fromUser, secretId)){
			throw new NotAuthorizedException("Share failed because user has no access. " + fromUser );
		}

	}

	@Before(value = "execution(public * edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
	public void unshareSecretCheck(JoinPoint joinPoint) throws NotAuthorizedException {
		System.out.printf("Access control prior to the execution of the method %s\n", joinPoint.getSignature().getName());
		String fromUser = (String)joinPoint.getArgs()[0];
		UUID secretId = (UUID)joinPoint.getArgs()[1];
		String toUser = (String)joinPoint.getArgs()[2];
		if (!stats.checkOwnSecret(fromUser, secretId) && !stats.checkSharedBefore(toUser, secretId,fromUser)){
			throw new NotAuthorizedException("Unshare failed because user has no access. " + fromUser);
		}
	}
}