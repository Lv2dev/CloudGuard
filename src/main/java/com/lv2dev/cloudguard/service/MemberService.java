package com.lv2dev.cloudguard.service;

import com.lv2dev.cloudguard.dto.MemberDTO;
import com.lv2dev.cloudguard.model.Member;
import com.lv2dev.cloudguard.persistence.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Service

public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private S3Service s3Service;

    public void signUp(MemberDTO memberDTO) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(memberDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        // 닉네임 중복 확인
        if (memberRepository.existsByNickname(memberDTO.getNickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nickname already in use");
        }

        // 비밀번호 규칙 검증
        if (!isValidPassword(memberDTO.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password does not meet the criteria");
        }

        Member member = Member.builder()
                .email(memberDTO.getEmail())
                .nickname(memberDTO.getNickname())
                .joinDay(LocalDateTime.now())
                .role(0)
                .state(0)
                .build();

        // 이미지가 입력되었을때만
        if (memberDTO.getProfile() != null && memberDTO.getProfile() != "") {
            // 비밀번호 암호화 및 프로필 이미지 처리
            member.setPassword(passwordEncoder.encode(memberDTO.getPassword())); // 비밀번호 암호화
            String profileUrl = s3Service.uploadFile(member.getProfile(), "/member/profile", member.getEmail());
            member.setProfile(profileUrl); // 프로필 이미지 URL 저장
        }

        // 추후 이메일 인증 기능 추가

        // 회원 정보 저장
        memberRepository.save(member);
    }

    private boolean isValidPassword(String password) {
        // 비밀번호 규칙: 특수문자 1개 이상, 대문자 1개 이상, 영문자, 소문자
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }
}
