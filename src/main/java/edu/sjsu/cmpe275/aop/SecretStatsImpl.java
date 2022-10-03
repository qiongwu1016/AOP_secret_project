package edu.sjsu.cmpe275.aop;

import java.util.*;

public class SecretStatsImpl implements SecretStats {
    /***
     * This is a dummy implementation only. 
     * You are expected to provide an actual implementation based on the requirements.
     */
	static int lengthOfLongestSecret;
	static Map<String, Integer> inboundCountingMap = new TreeMap<String, Integer>();
	static Map<String, Integer> outboundCountingMap = new TreeMap<String, Integer>();
	static Map<UUID, String> secretCreateByMap = new HashMap<UUID, String>();
	static Map<String, Map<UUID, Set<String>>> secretShareMap = new HashMap<String, Map<UUID, Set<String>>>();
	static Map<UUID, Set<String>> secretAccessMap = new TreeMap<UUID, Set<String>>();

	public void addSecretAccessRecord(UUID secretId, String userId) {
		if (secretAccessMap.containsKey(secretId)) {
			secretAccessMap.get(secretId).add(userId);
		} else {
			Set<String> set = new HashSet<String>();
			set.add(userId);
			secretAccessMap.put(secretId, set);
		}
	}

	public void removeSecretAccessRecord(UUID secretId, String userId) {
		if (secretAccessMap.containsKey(secretId)) {
			if (secretAccessMap.get(secretId).contains(userId)) {
				secretAccessMap.get(secretId).remove(userId);
				if (secretAccessMap.get(secretId).isEmpty()) secretAccessMap.remove(secretId);
			}
		}
	}
	public void addShareRecord(String toUser, UUID secretId, String fromUser){
		if (secretShareMap.containsKey(toUser)){
			if (secretShareMap.get(toUser).containsKey(secretId)){
				secretShareMap.get(toUser).get(secretId).add(fromUser);
			} else {
				Set<String> tempSet = new HashSet<String>();
				tempSet.add(fromUser);
				secretShareMap.get(toUser).put(secretId,tempSet);
			}
		} else{
			Set<String> tempSet = new HashSet<String>();
			tempSet.add(fromUser);
			Map<UUID, Set<String>> tempMap = new HashMap<UUID, Set<String>>();
			tempMap.put(secretId, tempSet);
			for (UUID id: tempMap.keySet()) {
			}
			secretShareMap.put(toUser, tempMap);
		}
	}

	public void deleteAShareRecord(String toUser, UUID secretId, String fromUser){
		if (secretShareMap.containsKey(toUser)){
			if (secretShareMap.get(toUser).containsKey((secretId))) {
				secretShareMap.get(toUser).get(secretId).remove(fromUser);
				if (secretShareMap.get(toUser).get(secretId).size() == 0) secretShareMap.get(toUser).remove(secretId);
				if (secretShareMap.get(toUser).isEmpty()) secretShareMap.remove(toUser);
			}
		}
	}

	public void deleteAllShareRecords(String toUser, UUID secretId) {
		if (secretShareMap.containsKey(toUser)) {
			secretShareMap.get(toUser).remove(secretId);
			if (secretShareMap.get(toUser).isEmpty()) secretShareMap.remove(toUser);
		}
	}

	public Set<String> getShareFrom(String toUser, UUID secretId) {
		if (secretShareMap.containsKey(toUser) && secretShareMap.get(toUser).containsKey(secretId)) {
			return secretShareMap.get(toUser).get(secretId);
		} else return null;
	}

	public void printAllShare(){
		System.out.println("***********");
		for (String toUser: secretShareMap.keySet()) {
			System.out.printf("ToUser:%s \n", toUser);
			if (secretShareMap.get(toUser).size() > 0) {
				for (UUID secretId: secretShareMap.get(toUser).keySet()) {
					System.out.printf("SecretId: %s FromUserSet:%s\n", secretId, secretShareMap.get(toUser).get(secretId).toString());
					System.out.println("--------");
				}
			}
		}
		System.out.println("***********");
	}

	public void addCreateByRecord(UUID secretId, String userId) {
		secretCreateByMap.put(secretId, userId);
	}

	public Boolean checkSecretIdExist(UUID secretId) {
		if (secretCreateByMap.containsKey(secretId)) {
			return true;
		} else return false;
	}

//	public void printAllCreateBy(){
//		System.out.println("*****");
//		for (UUID key: secretCreateByMap.keySet()) {
//			System.out.println(key);
//			System.out.println(secretCreateByMap.get(key));
//			System.out.println("-------------");
//		}
//		System.out.println("*****");
//	}

	public Boolean checkOwnSecret(String user, UUID secretId){
		if (user == secretCreateByMap.get(secretId)) {
			return true;
		} else return false;
	}

	public Boolean checkBeShared(String user, UUID secretId) {
		if (getShareFrom(user, secretId) != null) {
			return true;
		} else return false;
	}

	public Boolean checkSharedBefore(String toUser, UUID secretId, String fromUser) {
		if (getShareFrom(toUser, secretId) == null) return false;
		if (getShareFrom(toUser, secretId).contains(fromUser)){
			return true;
		} else return false;
	}

	public void addInboundSharingCount(String toUser, UUID secretId) {
		if (inboundCountingMap.containsKey(toUser)) {
			inboundCountingMap.put(toUser, inboundCountingMap.get(toUser) + 1);
		} else inboundCountingMap.put(toUser, 1);
	}

	public void addOutboundResharingCount(String fromUser, UUID secretId) {
		if (outboundCountingMap.containsKey(fromUser)) {
			outboundCountingMap.put(fromUser,outboundCountingMap.get(fromUser) + 1);
		} else outboundCountingMap.put(fromUser, 1);
//		for (String key:outboundCountingMap.keySet()) {
//			System.out.printf("fromUser:%s OutboundResharingCount:%s\n", key, outboundCountingMap.get(key));
//		}
	}

	@Override
	public void resetStatsAndSystem() {
		// TODO Auto-generated method stub
		lengthOfLongestSecret = 0;
		secretCreateByMap.clear();
		secretShareMap.clear();
		inboundCountingMap.clear();
		outboundCountingMap.clear();
	}

	@Override
	public int getLengthOfLongestSecret() {
		// TODO Auto-generated method stub
		return lengthOfLongestSecret;
	}

	public void setLengthOfLongestSecret(Integer len) {
		lengthOfLongestSecret = len;
	}

	@Override
	public String getMostTrustedUser() {
		// TODO Auto-generated method stub
//		for (String key:inboundCountingMap.keySet()) {
//			System.out.printf("ToUser:%s InboundSharingCount:%s\n", key, inboundCountingMap.get(key));
//		}

		if (inboundCountingMap.isEmpty()) return null;
		Integer maxValueInMap = (Collections.max(inboundCountingMap.values()));
		for (String key: inboundCountingMap.keySet()) {
			if (inboundCountingMap.get(key) == maxValueInMap) return key;
		}
		return  null;
	}

	@Override
	public String getWorstSecretKeeper() {
		// TODO Auto-generated method stub

//		for (String key:outboundCountingMap.keySet()) {
//			System.out.printf("ToUser:%s outboundSharingCount:%s\n", key, outboundCountingMap.get(key));
//		}

		if(outboundCountingMap.isEmpty()) return null;
		Integer maxValueInMap = (Collections.max(outboundCountingMap.values()));
		for (String key: outboundCountingMap.keySet()) {
			if (outboundCountingMap.get(key) == maxValueInMap) return key;
		}
		return null;
	}

	@Override
	public UUID geMostAccessibleSecret() {
		// TODO Auto-generated method stub
//		for (UUID key:secretAccessMap.keySet()) {
//			System.out.printf("SecretId:%s Share to Users:%s\n", key, secretAccessMap.get(key).toString());
//		}

		if (secretAccessMap.isEmpty()) return null;

		Integer maxAccessByNumber = 0;
		for (UUID key: secretAccessMap.keySet()) {
			Integer tempNumber = secretAccessMap.get(key).size();
			if (tempNumber > maxAccessByNumber) maxAccessByNumber = tempNumber;
		}

		for (UUID key: secretAccessMap.keySet()) {
			if (secretAccessMap.get(key).size() == maxAccessByNumber) return key;
		}
		return null;
	}
    
}



