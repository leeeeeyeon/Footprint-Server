package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PatchUserGoalReq {
    private int walkGoalTime;
    private int walkTimeSlot;
    private List<Integer> dayIdx;

}
