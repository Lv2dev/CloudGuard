package com.lv2dev.cloudguard.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;

@Service
public class EncryptionService {

    // 암호화 알고리즘을 나타내는 열거형
    public enum EncryptionAlgorithm {
        AES, SEED, ARIA
    }

    static {
        // Bouncy Castle provider 등록
        Security.addProvider(new BouncyCastleProvider());
    }

    public byte[] encryptData(byte[] data, String key, EncryptionAlgorithm algorithm) throws Exception {
        Key secretKey = generateKey(key, algorithm);
        Cipher cipher = Cipher.getInstance(getCipherAlgorithm(algorithm), "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(data);
    }

    private Key generateKey(String keyString, EncryptionAlgorithm algorithm) throws Exception {
        // 키 생성 로직, 알고리즘에 따라 변화
        byte[] keyBytes = keyString.getBytes();
        return new SecretKeySpec(keyBytes, algorithm.name());
    }

    private String getCipherAlgorithm(EncryptionAlgorithm algorithm) {
        switch (algorithm) {
            case AES:
                return "AES/CBC/PKCS5Padding";
            case SEED:
                return "SEED/CBC/PKCS5Padding";
            case ARIA:
                return "ARIA/CBC/PKCS5Padding";
            default:
                throw new IllegalArgumentException("Unsupported encryption algorithm");
        }
    }
}
