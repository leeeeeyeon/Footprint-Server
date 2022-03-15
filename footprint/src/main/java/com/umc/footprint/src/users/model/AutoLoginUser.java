package com.umc.footprint.src.users.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class AutoLoginUser {
    private String status;
    private LocalDateTime logAt;

    @Builder
    public AutoLoginUser(String status, LocalDateTime logAt) {
        this.status = status;
        this.logAt = logAt;
    }
}
