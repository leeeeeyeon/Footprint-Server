package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BadgeOrder {
    private int badgeIdx;
    private String badgeName;
    private String badgeUrl;
    private String badgeDate;
    private int badgeOrder;
}
