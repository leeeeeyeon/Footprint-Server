package com.umc.footprint.src.users.model;

import lombok.*;

@Getter
@NoArgsConstructor
@ToString
public class PostLoginReq {
    private String userId;
    private String username;
    private String email;
    private String providerType;

    @Builder
    public PostLoginReq(String userId, String username, String email, String providerType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.providerType = providerType;
    }

    public void setEncryptedInfos(String encryptedUsername, String encryptedEmail) {
        this.username = encryptedUsername;
        this.email = encryptedEmail;
    }
}
