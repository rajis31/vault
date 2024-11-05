/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.devtop.encryption;


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
            byte[] iv = generateIv();
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            byte[] decodedKey = Base64.getDecoder().decode(secretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
            Cipher cipher = Cipher.getInstance(IMPLEMENTATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
            byte[] cipherText = cipher.doFinal(input.getBytes());
            String base64IV = Base64.getEncoder().encodeToString(iv);
            String prefixedIv =  base64IV + ":" + Base64.getEncoder().encodeToString(cipherText);
            return prefixedIv;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }




}
