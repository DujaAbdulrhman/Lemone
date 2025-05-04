package com.example.demo.Config;

import com.example.demo.Service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;


@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(myUserDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeHttpRequests()

                // 👤 User endpoints
                .requestMatchers("/api/v1/user/add").permitAll()
                .requestMatchers("/api/v1/user/get/**").permitAll()

                // 📦 Product endpoints
                .requestMatchers("/api/v1/product/add").permitAll()
                .requestMatchers("/api/v1/product/get").permitAll()
                .requestMatchers("/api/v1/product/checkout/**").permitAll()
                .requestMatchers("/api/v1/product/delete/**").permitAll()

                // 💳 Payment endpoints
                .requestMatchers("/api/v1/payments/create").permitAll()
                .requestMatchers("/api/v1/payments/get-status/**").permitAll()
                .requestMatchers("/api/v1/payments/callback").permitAll()

                // 🪝 Webhook endpoint
                .requestMatchers("/api/v1/payment/webhook").permitAll()

                // ⛔ أي شيء غير ذلك يتطلب توثيق
                .anyRequest().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }

    @Bean
    @Lazy
    public WebClient webClient(@Value("${lemon.squeezy.api.key}") String apiKey) {
        return WebClient.builder()
                .baseUrl("https://api.lemonsqueezy.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)  // إضافة الـ API Key
                .build();
    }


    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeHttpRequests()

                .requestMatchers("/api/v1/user/add").permitAll()
                .requestMatchers("/api/v1/user/get/**").authenticated()

                // 📦 Product Endpoints
                .requestMatchers("/api/v1/product/get").permitAll()
                .requestMatchers("/api/v1/product/checkout/**").permitAll()
                .requestMatchers("/api/v1/product/add").authenticated()
                .requestMatchers("/api/v1/product/delete/**").authenticated()

                // 💳 Webhook Endpoint
                .requestMatchers("/api/v1/payment/webhook").permitAll()

                // أي شيء ثاني يتطلب تسجيل دخول
                .anyRequest().authenticated()
                .and()

                .httpBasic();
        return http.build();*/
   // }



    //annotation not lombok
   /* @Value("${lemon.api.base.url}")
    private String baseUrl;

    @Value("${lemon.api.key}")
    private String apiKey;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " +apiKey)
                .defaultHeader("Accept", "application/vnd.api+json")
                .defaultHeader("Content-Type", "application/vnd.api+json")
                .build();
    }*/
}
