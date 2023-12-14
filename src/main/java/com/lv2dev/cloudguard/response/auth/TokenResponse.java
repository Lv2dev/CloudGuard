package com.lv2dev.cloudguard.response.auth;

import lombok.*;

@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
}
