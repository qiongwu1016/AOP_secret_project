package edu.sjsu.cmpe275.aop;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {
        /***
         * Following is a dummy implementation of App to demonstrate bean creation with Application context.
         * You may make changes to suit your need, but this file is NOT part of your submission.
         */

    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
        SecretService secretService = (SecretService) ctx.getBean("secretService");
        SecretStats stats = (SecretStats) ctx.getBean("secretStats");
        UUID fakeSecretId = UUID.randomUUID();
        try {
        	UUID secret = secretService.createSecret("Alice", "My little secret");
            UUID secret1 = secretService.createSecret("Qiong", "Qiong hahahah");
            secretService.shareSecret("Alice", secret, "Bob");
            secretService.readSecret("Bob", secret);
            secretService.shareSecret("Bob", secret, "Freya");
            secretService.shareSecret("Bob", secret, "Tom");
            secretService.shareSecret("Alice", secret, "Freya");
            secretService.shareSecret("Qiong", secret1, "Jasper");
            secretService.shareSecret("Bob", secret, "Jasper");
            secretService.unshareSecret("Alice", secret, "Jasper");
            UUID secret2 = secretService.createSecret("Freya", "I'm a little girl");
            secretService.shareSecret("Freya", secret, "Sea");
            secretService.shareSecret("Bob", secret, "Sea");
//            secretService.unshareSecret("Bob", secret, "Sea");
            secretService.unshareSecret("Freya", secret, "Sea");
            secretService.shareSecret("Freya", secret, "Lucy");
            secretService.shareSecret("Freya", secret, "Bob");
            secretService.shareSecret("Freya", secret, "Qiong");
            secretService.shareSecret("Freya", secret2, "Jasper");

//            secretService.createSecret("Jasper", ";akjd;alkjjhglkjh;khhlkjhljhlhhlhhlhlhklh;khk;jhljhklglkjghkljhjlkhlhhk;h;khhj;h;dfkjadaj;dkfjadfkjadkfja;dkfjadkjfa;dkjakfja;ldkjfa;");
//            secretService.unshareSecret("Alice", secret, "Qiong");
//            secretService.shareSecret("Qiong", secret1, "Bob");
//        	secretService.shareSecret("Qiong", secret, "Bob");
//            secretService.unshareSecret("Alice", secret, "Qiong");
//        	secretService.shareSecret("Alice", secret, "Bob");
//        	secretService.readSecret("Bob", secret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("The best known secret: " + stats.geMostAccessibleSecret());
        System.out.println("The worst secret keeper: " + stats.getWorstSecretKeeper());
        System.out.println("The most trusted user: " + stats.getMostTrustedUser());
        System.out.println("The length of the longest secret: " + stats.getLengthOfLongestSecret());
        ctx.close();
    }
}
