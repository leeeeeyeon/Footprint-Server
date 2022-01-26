package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserBadges {
    private BadgeInfo repBadgeInfo;
    private List<BadgeInfo> badgeInfoList;
}
