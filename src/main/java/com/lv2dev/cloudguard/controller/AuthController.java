package com.lv2dev.cloudguard.controller;

import com.lv2dev.cloudguard.dto.auth.LoginDTO;
import com.lv2dev.cloudguard.model.Member;
import com.lv2dev.cloudguard.persistence.MemberRepository;
import com.lv2dev.cloudguard.response.auth.TokenResponse;
import com.lv2dev.cloudguard.service.TokenService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Api(tags = "토큰 발급과 재발급 관련 API를 담당하는 컨트롤러")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class AuthController {

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String refreshToken) {
        try {
            String email = tokenService.getEmailFromToken(refreshToken);
            Member member = memberRepository.findByEmail(email);
            // db에 저장된 refresh token과 요청으로 온 refresh token이 일치하는지 확인
            if (member != null && refreshToken.equals(member.getRefreshToken())) {
                String newAccessToken = tokenService.createAccessToken(member);
                return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken));
            }
            if (member != null) {
                String newAccessToken = tokenService.createAccessToken(member);
                return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validity")
    public ResponseEntity<?> isTokenValid(@RequestParam String token) {
        boolean isValid = tokenService.isTokenValid(token);
        return ResponseEntity.ok(Collections.singletonMap("isValid", isValid));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Member member = memberRepository.findByEmail(loginDTO.getEmail());
        if (member != null && passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
            String accessToken = tokenService.createAccessToken(member);
            String refreshToken = tokenService.createRefreshToken(member);
            // db에 refresh token 저장
            member.setRefreshToken(refreshToken);
            memberRepository.save(member);
            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 로그아웃 컨트롤러 ... db에 저장된 refresh token 삭제
     * */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String refreshToken) {
        try {
            String email = tokenService.getEmailFromToken(refreshToken);
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                member.setRefreshToken(null);
                memberRepository.save(member);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
