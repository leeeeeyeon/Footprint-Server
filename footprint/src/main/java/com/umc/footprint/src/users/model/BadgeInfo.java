package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BadgeInfo {
    @NonNull
    private int badgeIdx;

    @NonNull
    private String badgeName;

    @NonNull
    private String badgeImageUrl;
}
