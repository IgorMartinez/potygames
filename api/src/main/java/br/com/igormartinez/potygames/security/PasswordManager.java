package br.com.igormartinez.potygames.security;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;
import org.springframework.stereotype.Service;

@Service
public class PasswordManager {

    public PasswordManager() {}

    public PasswordEncoder getDefaultPasswordEncoder(){
        Map<String, PasswordEncoder> encoders = new HashMap<>();
		Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder("", 8, 185000, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
		encoders.put("pbkdf2", pbkdf2PasswordEncoder);
		DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
		passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2PasswordEncoder);

        return passwordEncoder;
    }

    public String encodePassword(String rawPassword) {
        PasswordEncoder passwordEncoder = getDefaultPasswordEncoder();
        String encodedString = passwordEncoder.encode(rawPassword);
        return StringUtils.substringAfter(encodedString, "}"); // {pbkdf2}f88...
    }
}
