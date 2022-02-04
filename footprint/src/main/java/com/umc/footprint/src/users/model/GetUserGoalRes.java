package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserGoalRes {
    private String month;
    private List<Integer> dayIdx;
    private UserGoalTime userGoalTime;
    private boolean goalNextModified;
}
