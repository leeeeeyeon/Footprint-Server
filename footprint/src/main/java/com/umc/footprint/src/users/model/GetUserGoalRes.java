package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserGoalRes {
    private List<Integer> dayIdx;
    private UserGoalTime userGoalTime;
}
