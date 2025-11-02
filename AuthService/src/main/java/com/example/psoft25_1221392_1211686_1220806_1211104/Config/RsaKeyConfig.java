package com.example.psoft25_1221392_1211686_1220806_1211104.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.*;

@Configuration
public class RsaKeyConfig {

    @Bean
    public KeyPair keyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA key pair", e);
        }
    }

    @Bean
    public java.security.interfaces.RSAPublicKey rsaPublicKey(KeyPair keyPair) {
        return (java.security.interfaces.RSAPublicKey) keyPair.getPublic();
    }

    @Bean
    public java.security.interfaces.RSAPrivateKey rsaPrivateKey(KeyPair keyPair) {
        return (java.security.interfaces.RSAPrivateKey) keyPair.getPrivate();
    }
}




