/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.devtop.encryption;


import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author raji
 */
public class AES256 {

    private static final String IMPLEMENTATION = "AES/GCM/NoPadding";
    
    
    private static byte[] generateIv() {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static String encrypt(String input, String secretKey) {

        try {
            byte[] iv                   = generateIv();
            GCMParameterSpec gcmSpec    = new GCMParameterSpec(128, iv);
            byte[] decodedKey           = Base64.getDecoder().decode(secretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
            Cipher cipher               = Cipher.getInstance(IMPLEMENTATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
            byte[] cipherText           = cipher.doFinal(input.getBytes());
            String base64IV             = Base64.getEncoder().encodeToString(iv);
            String prefixedIv           =  base64IV + ":" + Base64.getEncoder().encodeToString(cipherText);
            return prefixedIv;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    
    public static String decrypt(String input, String secretKey){
        try {
            String[] parts              = input.split(":");
            byte[] iv                   = Base64.getDecoder().decode(parts[0]);
            byte[] val                  = Base64.getDecoder().decode(parts[1]);
            GCMParameterSpec gcmSpec    = new GCMParameterSpec(128, iv);
            byte[] decodedKey           = Base64.getDecoder().decode(secretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
            Cipher cipher               = Cipher.getInstance(IMPLEMENTATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);
            byte[] decryptedBytes       = cipher.doFinal(val);
             return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }




}
