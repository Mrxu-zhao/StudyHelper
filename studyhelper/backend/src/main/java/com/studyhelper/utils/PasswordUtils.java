package com.studyhelper.utils;

import org.springframework.util.DigestUtils;

import java.util.UUID;

public class PasswordUtils {
    
    private static final String SALT_PREFIX = "StudyHelper";
    
    /**
     * 密码加密（使用BCrypt，但这里用简单方式演示）
     */
    public static String encode(String rawPassword) {
        String salt = SALT_PREFIX + UUID.randomUUID().toString().substring(0, 8);
        String encoded = DigestUtils.md5DigestAsHex((salt + rawPassword).getBytes());
        return salt + ":" + encoded;
    }
    
    /**
     * 密码验证
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null || !encodedPassword.contains(":")) {
            return false;
        }
        String[] parts = encodedPassword.split(":");
        String salt = parts[0];
        String encoded = parts[1];
        String newEncoded = DigestUtils.md5DigestAsHex((salt + rawPassword).getBytes());
        return newEncoded.equals(encoded);
    }
    
    /**
     * 生成6位随机绑定码
     */
    public static String generateBindCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
}
