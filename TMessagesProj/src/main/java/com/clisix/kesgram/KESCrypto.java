package com.clisix.kesgram;

import android.content.Context;
import android.util.Base64;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class KESCrypto {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    public static void GenerateNewKeypair(Context context) {
        try {
            // Generate the RSA key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Get the private and public keys
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Save the keys to internal storage
            saveKey(context, "private_key.pem", privateKey.getEncoded());
            saveKey(context, "public_key.pem", publicKey.getEncoded());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveKey(Context context, String fileName, byte[] key) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(Base64.encode(key, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] readKey(Context context, String keyType) {
        String fileName;

        // Determine the filename based on the key type argument
        if ("public".equalsIgnoreCase(keyType)) {
            fileName = "public_key.pem";
        } else if ("private".equalsIgnoreCase(keyType)) {
            fileName = "private_key.pem";
        } else {
            throw new IllegalArgumentException("Invalid key type. Must be 'public' or 'private'.");
        }

        try (FileInputStream fis = context.openFileInput(fileName)) {
            byte[] encodedKey = new byte[fis.available()];
            fis.read(encodedKey);

            // Decode the Base64 encoded key
            return Base64.decode(encodedKey, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Encrypt a string of data using someone's public key
    public static String encryptData(String data, PublicKey publicKey) {
        try {
            // Initialize cipher for encryption
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // Encrypt the data
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());

            // Encode the encrypted bytes to Base64 for easy storage and transfer
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptData(Context context, String encryptedData) {
        try {
            // Read the private key from internal storage
            byte[] privateKeyBytes = readKey(context, "private");
            if (privateKeyBytes == null) {
                throw new Exception("Private key not found");
            }

            // Convert bytes to PrivateKey
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // Decode the encrypted data from Base64
            byte[] encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT);

            // Decrypt the data
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Convert decrypted bytes to a string
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
