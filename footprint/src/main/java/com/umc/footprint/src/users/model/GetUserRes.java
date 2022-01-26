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
    private String name;
    private String email;
    private String status;
    private int badgeIdx;
    private String badgeUrl;
    private int age;
    private String sex;
    private int height;
    private int weight;
}

