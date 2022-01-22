package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserGoalRes {
    List<Integer> dayIdx;
    UserGoalTime userGoalTime;
}
