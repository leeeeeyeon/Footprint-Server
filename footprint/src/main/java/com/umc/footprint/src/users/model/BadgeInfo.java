package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BadgeInfo {
    private int badgeIdx;
    private String badgeName;
    private String badgeUrl;
    private String badgeDate;
}
