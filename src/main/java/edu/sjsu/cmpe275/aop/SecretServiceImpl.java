package edu.sjsu.cmpe275.aop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecretServiceImpl implements SecretService {

	/***
	 * Important: While you can tweak this implementation to suit your need for
	 * debugging purpose, this file is NOT part of your submission, and the
	 * correctness of your submission CANNOT depend on your implementation of this
	 * class. 
	 */

	Map<UUID, String> secrets = new HashMap<UUID, String>();

	@Override
	public UUID createSecret(String userId, String secretContent) throws IOException, IllegalArgumentException {
		System.out.printf("User %s creates secret: %s\n", userId, secretContent);
		UUID id = UUID.randomUUID();
		secrets.put(id, secretContent);
		return id;
	}

	@Override
	public String readSecret(String userId, UUID secretId)
			throws IOException, IllegalArgumentException, NotAuthorizedException {
		String secret = secrets.get(secretId);
		System.out.printf("User %s reads secret: %s\n", userId, secret);
		return secret;
	}

	@Override
	public void shareSecret(String userId, UUID secretId, String targetUserId)
			throws IOException, IllegalArgumentException, NotAuthorizedException {
		System.out.printf("User %s shares secret: %s\n", userId, secretId);
//		throw new IOException("Fake IOException.");
	}

	@Override
	public void unshareSecret(String userId, UUID secretId, String targetUserId)
			throws IOException, IllegalArgumentException, NotAuthorizedException {
		System.out.printf("User %s unshares secret: %s\n", userId, secretId);
	}

}
