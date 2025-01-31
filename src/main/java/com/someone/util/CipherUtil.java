package com.someone.util;

/*
  @Author Someone
 * @Date 2024/09/25 19:44
 */
import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.someone.debug.LogReceiver;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.zip.CRC32;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {

    public static String getDigest(byte[] data, String algorithm) {
        try {
            MessageDigest instance = MessageDigest.getInstance(algorithm);
            return toHex(instance.digest(data));
        } catch (NoSuchAlgorithmException e) {
            LogReceiver.e(e);
            return null;
        }
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

    public static String toSHA1(String data) {
        return toSHA1(data.getBytes());
    }

    public static String toSHA1(byte[] data) {
        return getDigest(data, "SHA-1");
    }

    public static String toSHA256(String data) {
        return toSHA256(data.getBytes());
    }

    public static String toSHA256(byte[] data) {
        return getDigest(data, "SHA-256");
    }

    public static String toSHA512(String data) {
        return toSHA512(data.getBytes());
    }

    public static String toSHA512(byte[] data) {
        return getDigest(data, "SHA-512");
    }

    public static String toMD5(String data) {
        return toMD5(data.getBytes());
    }

    public static String toMD5(byte[] data) {
        return getDigest(data, "MD5");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String toBase64(String data) {
        Base64.Encoder encoder = Base64.getMimeEncoder();
        return new String(encoder.encode(data.getBytes()), StandardCharsets.UTF_8);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String toBase64(byte[] data) {
        Base64.Encoder encoder = Base64.getMimeEncoder();
        return new String(encoder.encode(data), StandardCharsets.UTF_8);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String toBase64Decode(String data) {
        Base64.Decoder decoder = Base64.getMimeDecoder();
        return new String(decoder.decode(data.getBytes()), StandardCharsets.UTF_8);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String toBase64Decode(byte[] data) {
        Base64.Decoder decoder = Base64.getMimeDecoder();
        return new String(decoder.decode(data), StandardCharsets.UTF_8);
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

    public static class RSAEncryption {
        private static final String RSA = "RSA";
        private static final int KEY_SIZE = 1024;
        private PrivateKey privateKey;
        private PublicKey publicKey;

        public RSAEncryption() {
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
                keyPairGenerator.initialize(KEY_SIZE);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                this.privateKey = keyPair.getPrivate();
                this.publicKey = keyPair.getPublic();
            } catch (NoSuchAlgorithmException e) {
                LogReceiver.e(e);
            }
        }

        public String encrypt(String plainText) throws Exception {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return toHex(encryptedBytes);
        }

        public String decrypt(String encryptedText) throws Exception {
            byte[]encryptedBytes = hexToByteArray(encryptedText);
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        }
    }

    public static String addSalt(String data) {
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

    public static String removeSalt(String data) {
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String AESEncrypt(String data, SecretKey secretKey) throws Exception {
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES"); // 创建Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, secretKey); // 初始化Cipher对象为加密模式
        byte[] encryptedData = cipher.doFinal(data.getBytes()); // 进行加密
        return Base64.getEncoder().encodeToString(encryptedData); // 返回Base64编码的加密字符串
    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
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

        public byte[] encrypt(String value) throws Exception {
            return encrypt(value, SECRET_KEY, INIT_VECTOR);
        }

        public byte[] encrypt(String value, String secretKey) throws Exception {
            return encrypt(value, secretKey, INIT_VECTOR);
        }

        public byte[] encrypt(String value, String secretKey, String vector) throws Exception {
            if ((secretKey.length() == 16 || secretKey.length() == 24 || secretKey.length() == 32) && vector.length() == 16) {
                IvParameterSpec iv = new IvParameterSpec(vector.getBytes(StandardCharsets.UTF_8));
                SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                return cipher.doFinal(value.getBytes());
            } 
            throw new Exception("密钥和矢量长度必须为16,24或32 密钥长度：" + secretKey.length() + " 向量长度：" + vector.length());
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public byte[] decrypt(String value) throws Exception {
            return decrypt(value, SECRET_KEY, INIT_VECTOR);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public byte[] decrypt(String value, String secretKey) throws Exception {
            return decrypt(value, secretKey, INIT_VECTOR);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public byte[] decrypt(String value, String secretKey, String vector) throws Exception {
            if ((secretKey.length() == 16 || secretKey.length() == 24 || secretKey.length() == 32) && vector.length() == 16) {
                IvParameterSpec iv = new IvParameterSpec(vector.getBytes(StandardCharsets.UTF_8));
                SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
                return cipher.doFinal(Base64.getDecoder().decode(value));
            }
            throw new Exception("密钥和矢量长度必须为16,24或32 密钥长度：" + secretKey.length() + " 向量长度：" + vector.length());
        }

    }
}
