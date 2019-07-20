package com.test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class PasswordHelper
{

    public String decryptPassword(){
        return "";
    }

    public String encryptPassword(){
        return "";
    }


    
    /*
        1. Generate a random 32 bit salt: 908D C60A
        2. Concatenate that with the UTF-8 representation of the password (in this case test12): 908D C60A 7465 7374 3132
        3. Take the SHA-256 hash (assuming the hashing function wasn't modified): A5B9 24B3 096B 8897 D65A 3B5F 80FA 5DB62 A94 B831 22CD F4F8 FEAD 10D5 15D8 F391
        4. Concatenate the salt again: 908D C60A A5B9 24B3 096B 8897 D65A 3B5F 80FA 5DB62 A94 B831 22CD F4F8 FEAD 10D5 15D8 F391
        5. Convert to base64 encoding: kI3GCqW5JLMJa4iX1lo7X4D6XbYqlLgxIs30+P6tENUV2POR
        6. Use the base64-encoded value as the password_hash value in the request JSON.

    */
    private static  String getPasswordHash(String password) {
        byte[] salt = getSalt();
        printBytes("Salt",salt);

        try {
            byte[] saltedPassword = concatenateByteArray(salt, password.getBytes(StandardCharsets.UTF_8));

            printBytes("Password ",password.getBytes(StandardCharsets.UTF_8));

            printBytes("Salted Password",saltedPassword);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedPassword);

            printBytes("Salted Password Digest",hash);

            printBytes("Salted Password",concatenateByteArray(salt,hash));

            return Base64.getEncoder().encodeToString(concatenateByteArray(salt,hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a 32 bit random salt.
     */
    private static byte[] getSalt() {
        byte[] ba = new byte[4];
        new SecureRandom().nextBytes(ba);
        return ba;
    }

    /**
     * Concatenates two byte arrays.
     */
    private static byte[] concatenateByteArray(byte[] a, byte[] b) {
        
        int lenA = a.length;
        int lenB = b.length;
        byte[] c = Arrays.copyOf(a, lenA + lenB);
        System.arraycopy(b, 0, c, lenA, lenB);
        return c;
    }

    private static void printBytes(String message, byte[] bytes){
        try{
            System.out.println(message + "" + new String(bytes,"UTF-8"));
            char[] chars = new String(bytes,"UTF-8").toCharArray();

            for(char by: chars)
            {
                System.out.print(by+" "); 
            }
        }catch(Exception e){

        }
        System.out.println();
    }
    private static String separateByteArray(){
        return "";
    }

    public String decode(String encodedString){
        byte[] encodedData = Base64.getDecoder().decode(encodedString);

        return "";
    }

    public static void main(String[] args) {
        System.out.println(getPasswordHash("user"));
    }
}