package com.lv2dev.cloudguard.dto.auth;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
public class LoginDTO {

    @Parameter(description = "이메일")
    private String email;
    @Parameter(description = "이메일")
    private String password;
}
