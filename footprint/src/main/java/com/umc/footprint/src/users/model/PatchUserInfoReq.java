package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    public void setEncryptedNickname(String encryptedNickname) {
        this.nickname = encryptedNickname;
    }
}
