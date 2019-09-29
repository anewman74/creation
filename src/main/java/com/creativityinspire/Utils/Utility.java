package com.creativityinspire.Utils;

import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Service
public class Utility {

    public Timestamp getCurrentTimeStamp() {
        Date _today = new java.util.Date();
        return new Timestamp(_today.getTime());
    }

    public static String md5(String input) {
        String md5 = null;
        if (null == input) return null;
        try {
            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());
            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public String getHashPasword(String password) {
        String salt = "Random String with sdgfSDGF$#T@#$%@$&+(_~";
        return md5(password + salt);
    }

    public String getHashValue(String username) {
        // Set up session attribute equal to random number plus MD5 of password.  Pass this to the front end to use when editing creations.
        String salt = "$%^$%^%&#$@^RGER@#%RGA46262)^&**()_*";
        String time = String.valueOf(getCurrentTimeStamp());
        return md5(username + " " + salt + " " + time);
    }

    public String getNewRandomPassword() {
        String uuid = UUID.randomUUID().toString();
        System.out.println("Random password: uuid = " + uuid);
        return uuid;
    }
}
