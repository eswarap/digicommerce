package org.woven.digicommerce.tokensvc.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class AuthRequest {
    private String username;
    private String secret;

    public AuthRequest(String username, String secret) {
        this.username = username;
        this.secret = secret;
    }
}