package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserTodayRes {
    private float goalRate;
    private int walkGoalTime;
    private int walkTime;
    private double distance;
    private int calorie;

}
