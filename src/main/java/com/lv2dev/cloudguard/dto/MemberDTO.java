package com.lv2dev.cloudguard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String token;

    private Long id; // 사용자에게 고유하게 부여되는 값

    private String refreshToken;

    private String password;

    private String nickname;

    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime joinDay;

    private String profile; // 프로필 이미지가 들어있는 경로

    private int role; // 0:학생, 1:선생, 2:관리자

    private int state;
}
