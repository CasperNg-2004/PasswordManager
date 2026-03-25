package my.edu.utar.passwordmanager;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

public class CryptoUtil {

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "pm_aes_key";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private static SecretKey getOrCreateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        if (keyStore.containsAlias(KEY_ALIAS)) {
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            return entry.getSecretKey();
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build();
        keyGenerator.init(spec);
        return keyGenerator.generateKey();
    }

    // Returns: base64(iv) + ":" + base64(ciphertext)
    public static String encrypt(String plaintext) {
        if (plaintext == null) return null;
        try {
            SecretKey key = getOrCreateKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] iv = cipher.getIV();
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            String ivB64 = Base64.encodeToString(iv, Base64.NO_WRAP);
            String ctB64 = Base64.encodeToString(ciphertext, Base64.NO_WRAP);
            return ivB64 + ":" + ctB64;
        } catch (Exception e) {
            // Fallback: store as-is if encryption fails
            return plaintext;
        }
    }

    public static String decrypt(String stored) {
        if (stored == null) return null;
        try {
            int idx = stored.indexOf(':');
            if (idx <= 0) return stored; // not encrypted (fallback / old data)

            String ivB64 = stored.substring(0, idx);
            String ctB64 = stored.substring(idx + 1);
            byte[] iv = Base64.decode(ivB64, Base64.NO_WRAP);
            byte[] ciphertext = Base64.decode(ctB64, Base64.NO_WRAP);

            SecretKey key = getOrCreateKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // If decrypt fails, return raw
            return stored;
        }
    }
}

