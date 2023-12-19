package com.lv2dev.cloudguard.controller;

import com.lv2dev.cloudguard.dto.MemberDTO;
import com.lv2dev.cloudguard.dto.auth.LoginDTO;
import com.lv2dev.cloudguard.model.Member;
import com.lv2dev.cloudguard.persistence.MemberRepository;
import com.lv2dev.cloudguard.response.auth.TokenResponse;
import com.lv2dev.cloudguard.service.MemberService;
import com.lv2dev.cloudguard.service.TokenService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collections;

@Api(tags = "토큰 발급과 재발급 관련 API를 담당하는 컨트롤러")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
@Controller
public class AuthController {

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    // member service
    private final MemberService memberService;

    @GetMapping("/auth/check")
    public ResponseEntity<?> checkAuthentication(HttpServletRequest request) {
        try{
            String accessToken = extractTokenFromCookies(request, "accessToken");
            if (accessToken != null && tokenService.isTokenValid(accessToken)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = extractTokenFromCookies(request, "refreshToken");
            String email = tokenService.getEmailFromToken(refreshToken);
            Member member = memberRepository.findByEmail(email);

            if (member != null && refreshToken.equals(member.getRefreshToken())) {
                String newAccessToken = tokenService.createAccessToken(member);

                // 새로운 refreshToken 생성 및 HTTP-only 쿠키로 설정 (선택적)
                String newRefreshToken = tokenService.createRefreshToken(member);
                member.setRefreshToken(newRefreshToken);
                memberRepository.save(member);

                ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                        .httpOnly(true)
                        .path("/")
                        .maxAge(7 * 24 * 60 * 60) // 예시: 1주일
                        .build();

                response.addHeader("Set-Cookie", refreshTokenCookie.toString());

                return ResponseEntity.ok(new TokenResponse(newAccessToken)); // refreshToken은 본문에 포함하지 않음
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
    public ResponseEntity<?> signUp(@ModelAttribute MemberDTO memberDTO) {
        try {
            memberService.signUp(memberDTO);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            e.printStackTrace();
            // 커스텀 예외 메시지 반환
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        try{
            Member member = memberRepository.findByEmail(loginDTO.getEmail());
            if (member != null && passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
                String accessToken = tokenService.createAccessToken(member);
                String refreshToken = tokenService.createRefreshToken(member);

                member.setRefreshToken(refreshToken);
                memberRepository.save(member);

                // refreshToken을 HTTP-only 쿠키에 저장
                ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .path("/")
                        .maxAge(7 * 24 * 60 * 60) // 예시: 1주일
                        .build();

                response.addHeader("Set-Cookie", refreshTokenCookie.toString());

                return ResponseEntity.ok(new TokenResponse(accessToken)); // accessToken만 응답에 포함
            }
        }catch(Exception e){
            e.printStackTrace();
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

    /**
     * 주어진 쿠키 이름에 해당하는 값을 HttpServletRequest의 쿠키 배열에서 찾아 반환합니다.
     * */
    private String extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null; // 쿠키를 찾지 못한 경우 null 반환
    }
}
