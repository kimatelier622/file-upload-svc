package com.example.fileupload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import java.util.List;

@Configuration
public class SecurityConfig {
  private final JsonAuthenticationEntryPoint authenticationEntryPoint;
  private final UploadMethodGuardFilter uploadMethodGuardFilter;

  public SecurityConfig(JsonAuthenticationEntryPoint authenticationEntryPoint,
      UploadMethodGuardFilter uploadMethodGuardFilter) {
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.uploadMethodGuardFilter = uploadMethodGuardFilter;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/upload", "/api/files/**").authenticated()
            .anyRequest().authenticated())
        .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(authenticationEntryPoint))
        .oauth2ResourceServer(oauth2 -> oauth2
            .authenticationEntryPoint(authenticationEntryPoint)
            .jwt(Customizer.withDefaults()))
        .addFilterAfter(uploadMethodGuardFilter, AuthorizationFilter.class)
        .build();
  }

  /**
   * Let all standard methods, including TRACE, reach MVC after authentication so the API can
   * consistently return 405 for methods absent from the OpenAPI contract. MVC still exposes only
   * the explicitly mapped POST operation.
   */
  @Bean
  HttpFirewall apiHttpFirewall() {
    StrictHttpFirewall firewall = new StrictHttpFirewall();
    firewall.setAllowedHttpMethods(List.of("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"));
    return firewall;
  }
}
