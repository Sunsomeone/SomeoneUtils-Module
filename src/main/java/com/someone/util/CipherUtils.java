package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/25 19:44
 */

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.zip.CRC32;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtils {

    public static String getDigest(byte[] data, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance(algorithm);
        return toHex(instance.digest(data));
    }

    public static int toHashCode(String data) {
        return data.hashCode();
    }

    public static String toHex(String data) {
        return toHex(data.getBytes());
    }

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    public static String toSHA1(String data) throws NoSuchAlgorithmException {
        return toSHA1(data.getBytes());
    }

    public static String toSHA1(byte[] data) throws NoSuchAlgorithmException {
        return getDigest(data, "SHA-1");
    }

    public static String toSHA256(String data) throws NoSuchAlgorithmException {
        return toSHA256(data.getBytes());
    }

    public static String toSHA256(byte[] data) throws NoSuchAlgorithmException {
        return getDigest(data, "SHA-256");
    }

    public static String toSHA512(String data) throws NoSuchAlgorithmException {
        return toSHA512(data.getBytes());
    }

    public static String toSHA512(byte[] data) throws NoSuchAlgorithmException {
        return getDigest(data, "SHA-512");
    }

    public static String toMD5(String data) throws NoSuchAlgorithmException {
        return toMD5(data.getBytes());
    }

    public static String toMD5(byte[] data) throws NoSuchAlgorithmException {
        return getDigest(data, "MD5");
    }

    public static String toBase64(String data) {
        return Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
    }

    public static String toBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static String toBase64Decode(String data) throws UnsupportedEncodingException {
        byte[] decodedBytes = android.util.Base64.decode(data, Base64.NO_WRAP);
        return new String(decodedBytes, "UTF-8");
    }

    public static String toBase64Decode(byte[] data) throws UnsupportedEncodingException {
        byte[] decodedBytes = android.util.Base64.decode(data, Base64.NO_WRAP);
        return new String(decodedBytes, "UTF-8");
    }

    public static long toCRC32(String data) {
        CRC32 crc = new CRC32();
        crc.update(data.getBytes());
        return crc.getValue();
    }

    public static long toCRC32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }

    public static String addSalt(String data) throws NoSuchAlgorithmException {
        /*int saltLocation = toHashCode(data);
         while (Math.abs(saltLocation) > data.length()) {
         saltLocation = Math.abs(saltLocation / 2);
         }*/
        int saltLocation = data.length();
        String salt = toMD5(data);
        StringBuilder builder = new StringBuilder(data);
        builder.insert(saltLocation, salt);
        return builder.toString();
    }

    public static String removeSalt(String data) throws NoSuchAlgorithmException {
        /*int saltLocation = toHashCode(data);
         while (Math.abs(saltLocation) > data.length()) {
         saltLocation = Math.abs(saltLocation / 2);
         }*/
        int saltLocation = data.length() - 32;
        String salt = toMD5(data);
        StringBuilder builder = new StringBuilder(data);
        builder.delete(saltLocation, data.length());
        return builder.toString();
    }

    public static String AESEncrypt(String data, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // 明确指定加密模式、填充方式
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 生成随机 IV（初始化向量）
        SecureRandom secureRandom = new SecureRandom();
        byte[] ivBytes = new byte[16]; // IV 长度需与块大小一致（AES 块大小为 16 字节）
        secureRandom.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        // 初始化 Cipher 时传入 IV
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        // 加密数据
        byte[] encryptedData = cipher.doFinal(data.getBytes("UTF-8"));
        // 将 IV 和密文合并（IV 需要传输给解密方）
        byte[] combined = new byte[ivBytes.length + encryptedData.length];
        System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
        System.arraycopy(encryptedData, 0, combined, ivBytes.length, encryptedData.length);
        // 返回 Base64 编码的完整数据
        return android.util.Base64.encodeToString(combined, android.util.Base64.NO_WRAP);
    }

    public static String AESDecrypt(String encryptedData, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        // 解码 Base64 数据
        byte[] combined = android.util.Base64.decode(encryptedData, android.util.Base64.NO_WRAP);
        // 提取 IV（前 16 字节）
        byte[] ivBytes = new byte[16];
        System.arraycopy(combined, 0, ivBytes, 0, ivBytes.length);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        // 提取实际密文（剩余字节）
        byte[] ciphertext = new byte[combined.length - ivBytes.length];
        System.arraycopy(combined, ivBytes.length, ciphertext, 0, ciphertext.length);
        // 初始化 Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        // 解密数据
        byte[] decryptedData = cipher.doFinal(ciphertext);
        return new String(decryptedData, "UTF-8");
    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public static class RSAEncryption {
        private static final String RSA = "RSA";
        private static final int KEY_SIZE = 1024;
        private PrivateKey privateKey;
        private PublicKey publicKey;

        public RSAEncryption() throws NoSuchAlgorithmException {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        }

        public String encrypt(String plainText) throws Exception {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return toHex(encryptedBytes);
        }

        public String decrypt(String encryptedText) throws Exception {
            byte[] encryptedBytes = hexToByteArray(encryptedText);
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        }
    }

    public static class SymmetricEncryption {
        private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
        private static String SECRET_KEY = "Unset66BySomeone";
        private static String INIT_VECTOR = "Someone66Someone";

        public void setCustomKey(String secretKey) throws Exception {
            if (secretKey.length() == 16 | secretKey.length() == 24 | secretKey.length() == 32) {
                SECRET_KEY = secretKey;
            } else {
                throw new Exception("密钥的长度必须为16,24或32 密钥长度：" + secretKey.length());
            }
        }

        public void setCustomVector(String vector) throws Exception {
            if (vector.length() == 16) {
                INIT_VECTOR = vector;
            } else {
                throw new Exception("向量的长度必须为16 向量长度：" + vector.length());
            }
        }

        public byte[] encrypt(String value) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
            return encrypt(value, SECRET_KEY, INIT_VECTOR);
        }

        public byte[] encrypt(String value, String secretKey) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
            return encrypt(value, secretKey, INIT_VECTOR);
        }

        public byte[] encrypt(String value, String secretKey, String vector) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            if ((secretKey.length() == 16 || secretKey.length() == 24 || secretKey.length() == 32) && vector.length() == 16) {
                // 替换字符集和 Base64
                IvParameterSpec iv = new IvParameterSpec(vector.getBytes("UTF-8"));
                SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                return cipher.doFinal(value.getBytes("UTF-8")); // 显式指定字符集
            }
            throw new IllegalArgumentException("密钥和矢量长度必须为16,24或32 密钥长度：" + secretKey.length() + " 向量长度：" + vector.length());
        }

        public byte[] decrypt(String value) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
            return decrypt(value, SECRET_KEY, INIT_VECTOR);
        }

        public byte[] decrypt(String value, String secretKey) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
            return decrypt(value, secretKey, INIT_VECTOR);
        }

        public byte[] decrypt(String value, String secretKey, String vector) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
            if ((secretKey.length() == 16 || secretKey.length() == 24 || secretKey.length() == 32) && vector.length() == 16) {
                // 替换字符集和 Base64
                IvParameterSpec iv = new IvParameterSpec(vector.getBytes("UTF-8"));
                SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
                byte[] decodedData = android.util.Base64.decode(value, android.util.Base64.NO_WRAP); // 使用 Android Base64
                return cipher.doFinal(decodedData);
            }
            throw new IllegalArgumentException("密钥和矢量长度必须为16,24或32 密钥长度：" + secretKey.length() + " 向量长度：" + vector.length());
        }
    }
}
