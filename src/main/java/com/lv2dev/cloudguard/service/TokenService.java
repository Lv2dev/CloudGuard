package com.lv2dev.cloudguard.service;

import com.lv2dev.cloudguard.persistence.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.lv2dev.cloudguard.model.Member;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final MemberRepository memberRepository;

    @Value("${secretKey}")
    private String secretKey;

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build().parseSignedClaims(token).getPayload();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String createAccessToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 시간
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String createRefreshToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 25200000)) // 일주일
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    /**
     * refresh token 으로 이메일 찾기
     * */
    public String getEmailFromToken(String token){
        // token이 안들어왔으면 exception
        if(token == null || token.isEmpty()){
            throw new IllegalArgumentException("토큰이 없습니다.");
        }
        // token이 유효하지 않으면 exception
        if(!isTokenValid(token)){
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
        // token에서 이메일 추출
        Claims claims = Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(token).getPayload();

        return claims.getSubject();
    }

}
