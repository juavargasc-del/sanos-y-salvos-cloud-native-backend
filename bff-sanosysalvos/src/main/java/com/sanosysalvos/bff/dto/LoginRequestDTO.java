package com.sanosysalvos.bff.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    private String email;
    private String password;
}