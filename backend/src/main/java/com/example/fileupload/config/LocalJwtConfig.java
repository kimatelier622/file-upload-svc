package com.example.fileupload.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/** Local-only JWT support. It is not loaded unless the local Spring profile is active. */
@Configuration
@Profile("local")
public class LocalJwtConfig {
  @Bean
  JwtDecoder jwtDecoder(@Value("${app.local-jwt.secret}") String secret,
      @Value("${app.local-jwt.issuer}") String issuer) {
    SecretKey key = key(secret);
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();
    decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
    return decoder;
  }

  @Bean
  CommandLineRunner localUploadToken(@Value("${app.local-jwt.secret}") String secret,
      @Value("${app.local-jwt.issuer}") String issuer,
      @Value("${app.local-jwt.subject}") String subject,
      @Value("${app.local-jwt.ttl-minutes}") long ttlMinutes) {
    return args -> {
      Instant now = Instant.now();
      JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(issuer).subject(subject)
          .issueTime(Date.from(now)).expirationTime(Date.from(now.plusSeconds(ttlMinutes * 60))).build();
      SignedJWT token = new SignedJWT(new com.nimbusds.jose.JWSHeader(JWSAlgorithm.HS256), claims);
      token.sign(new MACSigner(Base64.getDecoder().decode(secret)));
      System.out.println("LOCAL_UPLOAD_JWT=" + token.serialize());
    };
  }

  private static SecretKey key(String base64Secret) {
    return new SecretKeySpec(Base64.getDecoder().decode(base64Secret), "HmacSHA256");
  }
}
