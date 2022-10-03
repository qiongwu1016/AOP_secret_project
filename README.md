# AOP_secret_project

Service Interface

The SecretService interface is defined here:

* public interface SecretService {
    
     * Creates a secret in the service. A new Secret object is created, identified
     * by randomly generated UUID, with the current user as the owner of the secret.
     *
     * @param userId   	the ID of the current user
     * @param secretConent the content of the secret to be created. No duplication
     * check is performed; i.e., one can create different secret
     * objects with the same content.
     * @throws IOException          	if there is a network failure
     * @throws IllegalArgumentException if the userId is null, or the secretContent is null or
     * more than 128 characters
     * @return returns the ID for the newly created secret object
     */
    UUID createSecret(String userId, String secretContent) throws IOException, IllegalArgumentException;

    
     * Reads a secret by its ID. A user can read a secret if he has created it or
     * it has been shared with him and not been unshared.
     *
     * @param userId   the ID of the current user
     * @param secretId the ID of the secret being requested
     * @throws IOException          	if there is a network failure
     * @throws IllegalArgumentException if any argument is null
     * @throws NotAuthorizedException   if the user cannot read a secret. If
     * there does not exist a secret with the given
     * UUID, this exception is thrown too.
     * @return the requested secret content
     */
    String readSecret(String userId, UUID secretId)
   		 throws IOException, IllegalArgumentException, NotAuthorizedException;

    
     * Shares a secret with another user. A user can share a secret that he has
     * created or currently has access to. In the case it is a secret he has been shared
     * with but is not the creator of. A user sharing the same secret ID with the same another user
     * multiple times is allowed. 
     * @param userId   	the ID of the current user
     * @param secretId 	the ID of the secret being shared
     * @param targetUserId the ID of the user to share the secret with
     * @throws IOException          	if there is a network failure
     * @throws IllegalArgumentException if any argument is null, or userId and targetUserId 
     * are equal.
     * @throws NotAuthorizedException   if the user with userId attempts to share a secret that he
     * cannot read. Throws this exception too if the given secret does not exist.  
     */
    void shareSecret(String userId, UUID secretId, String targetUserId)
   		 throws IOException, IllegalArgumentException, NotAuthorizedException;

    
     * Unshares a secret that was previously shared with another user. A user X can ONLY
     * unshare secret s from user Y if X has created s or X has shared s with Y before. 
     * As the creator of a secret, unsharing this secret  from himself is not allowed and triggers an
     * IllegalArgumentException. X unsharing secret s from Y will trigger NotAuthorizedException
     * if X has not created s and X has not shared s with Y. If X shares s with Y multiple times in
     * a row, and then unshares s from Y in twice in a row, the second unsharing will fail with
     * NotAuthorizedException as unsharing is not meant to be idempotent; regardless, if   
     * somebody else has shared s with Y before X’s first unsharing and X is not the creator of
     * s, Y can still read s. If, however,  X is the creator of s and X unshares s from Y, Y cannot
     * Y cannot read s until somebody shares s with Y again.

     * @param userId   	the ID of the current user
     * @param secretId 	the ID of the secret being unshared
     * @param targetUserId the ID of the user to unshare the secret with
     * @throws IOException          	if there is a network failure
     * @throws IllegalArgumentException if any argument is null, or userId and targetUserId 
     * are equal.
     * @throws NotAuthorizedException   See the description of the method.
     */
    void unshareSecret(String userId, UUID secretId, String targetUserId)
   		 throws IOException, IllegalArgumentException, NotAuthorizedException;
}

* Network Failure Retry

Since network failure happens relatively frequently, you are asked to add the feature to automatically retry for up to three times (not counting the initial invocation) for a network failure (indicated by an IOException) through RetryAspect.java. Please note the three retries are in addition to the original failed invocation. If on the last retry, we still get an IOException, the call on SecretService fails with IOException thrown.  

Parameter Validation
The existing implementation of SecretService does not do proper validation of the arguments. Please provide proper implementation for ValidationAspect.java such that IllegalArgumentException is thrown based on what’s described in the interface documentation.

Access Control
The existing implementation of SecretService does not enforce access control and you are required to implement it in AccessControllAspect.java, such that NotAuthorizedException is thrown as documented in the interface.  

Please note that our access control assumes that authentication is already taken care of elsewhere, i.e., it’s outside the scope of the project to make sure only Alice can call readSecret with userId as “Alice”.

Secret Stats
You need to provide the stats as defined in SecretStats.java. Your implementation of this interface resides in SecretStatsImpl.java.

* public interface SecretStats {
    
    /**
     * Reset all the four measurements. For purpose of this lab, it also clears up
     * all secret objects ever created and their sharing/unsharing as if the system
     * is starting fresh for any purpose related to the metrics below.
     */
    void resetStatsAndSystem();

    /**
     * @return the length of the longest secret by content a user has successfully
     * created since the beginning or last reset. If no secrets are created,
     * return 0.
     */
    int getLengthOfLongestSecret();

    /**
     * If Alice shares a secret with ID foo with Bob, the triple (Alice, foo, Bob) is considered
     * a sharing occurrence with Bob. The total number of unique occurrences (X, Y, c) for user c
     * is called the inbound sharing count for user c. The most trusted user is determined by the
     * user that has the biggest success inbound sharing count. The uniqueness of secrets
     * are defined their UUIDs; i.e., two secrets with the same content but
     * different UUIDs are considered different secrets. Unsharing does NOT affect
     * this stat. If Alice and Bob share the same secret with Carl once each, it's
     * considered as two total sharing occurrences with Carl. If Alice shares the
     * same secret he created with Carl five times and later unshares it, it is
     * still considered one sharing occurrence. Sharing a secret with a user
     * himself does NOT count for the purpose of this stat. If there is a tie,
     * return the 1st of such users based on the alphabetical order of the user ID. Only
     * successful sharing matters here; if no secrets have been successfully shared
     * with anyone, return null.
     *
     * @return the ID of the most trusted user.
     */
    String getMostTrustedUser();

    /**
     * The total number of successful unique sharing occurrences (a, Y, Z), where a is not 
     * the creator of Y, is called the outbound resharing count for the user. Suppose Alice 
     * has been successfully shared with N unique secrets (regardless of unsharing), and has
     * an outbound resharing count of M, M/N is called Alice’s leaking score. The leaking score
     * is 0 if N is zero.
     *
     * @return the ID of the person with the highest leaking score. If there
     * is a tie, return the 1st of such users based on alphabetical order of
     * the user ID. If no users have a leaking score, return null.
     */
    String getWorstSecretKeeper();

    /**
     * Returns the secret that can be successfully read by the biggest number of
     * different users. 
     *
     * @return the ID of the secret that can be read by the biggest
     * number of different users. If no secrets can ever be read by users,  return null. If there is a tie, return the one with the smallest UUID. 
     */
    UUID geMostAccessibleSecret();
}
