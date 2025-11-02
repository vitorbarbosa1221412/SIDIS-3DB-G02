package com.example.psoft25_1221392_1211686_1220806_1211104.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Propriedades de configuração do JWT (RSA Keys).
 *
 * CORREÇÃO: Usamos @Primary para resolver o conflito de Bean com o RsaKeyConfig.
 */

@ConfigurationProperties(prefix = "jwt.key")
@Getter
@Setter
public class RsaKeyProperties {
    private String keyStore;
    private String keyAlias;

    // Nota: keyPassword é na verdade a senha da chave privada (privateKeyPassword)
    // Usamos este nome para evitar confusão com a senha do Keystore.
    private String keyPassword;
}