package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfoRes {
    private UserInfoAchieve userInfoAchieve;
    private GetUserGoalRes getUserGoalRes;
    private UserInfoStat userInfoStat;
}
