package com.umc.footprint.src.users.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PatchUserGoalReq {

    private int walkGoalTime;
    private int walkTimeSlot;
    private List<Integer> dayIdx;

}
