package com.umc.footprint.src.users.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostUserGoalRes {
    private int userIdx;
    private int walkGoalTime;
    private String walkTimeSlot;
    private List<String> goalDay;
}
