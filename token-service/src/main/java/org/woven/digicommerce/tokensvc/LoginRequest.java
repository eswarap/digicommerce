package org.woven.digicommerce.tokensvc;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    private String username;
    private String secret;
}
