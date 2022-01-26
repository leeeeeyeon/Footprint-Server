package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class GetUserRes {
    private int userIdx;
    private String nickname;
    private int badgeIdx;
    private String name;
    private int age;
    private int sex;
    private int height;
    private int weight;
}

