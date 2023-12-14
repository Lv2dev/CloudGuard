
package com.lv2dev.cloudguard.config;


import com.lv2dev.cloudguard.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] SWAGGER_URI = {
            "/swagger-ui.html", "/v2/api-docs", "/swagger-resources/**", "/webjars/**", "/swagger/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http 시큐리티 빌더
        http
                .csrf((csrf) -> csrf
                    .ignoringRequestMatchers("/api/*")
                ) // csrf 사용하지 않음
                .httpBasic(withDefaults()) // basic 사용하지 않음
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authorize) -> authorize // 인증 안해도 되는 경로 설정
                        .requestMatchers(SWAGGER_URI).permitAll()
                        .requestMatchers("/",
                                "/api/unauth/**",
                                "/error",
                                "/google/**","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .cors(withDefaults()); // cors 사용
        //filter 등록
        //매 요청마다
        //CorsFilter 실행한 후에
        //jwtAuthenticationFilter 실행
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}


