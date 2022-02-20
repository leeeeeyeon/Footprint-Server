package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostLoginReq {
    private String userId;
    private String username;
    private String email;
    private String providerType;

    public void setEncryptedInfos(String encryptedUsername, String encryptedEmail) {
        this.username = encryptedUsername;
        this.email = encryptedEmail;
    }
}
