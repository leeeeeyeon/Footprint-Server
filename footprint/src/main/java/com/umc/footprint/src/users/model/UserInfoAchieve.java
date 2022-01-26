package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoAchieve {
    private int todayGoalRate;
    private int monthGoalRate;
    private int userWalkCount;
}
