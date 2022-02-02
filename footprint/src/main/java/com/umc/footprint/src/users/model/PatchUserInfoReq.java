package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PatchUserInfoReq {
    private String nickname;
    private String sex;
    private String birth;
    private int height;
    private int weight;
    private List<Integer> dayIdx;
    private int walkGoalTime;
    private int walkTimeSlot;
}
