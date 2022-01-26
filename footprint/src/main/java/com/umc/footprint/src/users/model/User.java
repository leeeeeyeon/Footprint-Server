package com.umc.footprint.src.users.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class User {
    private int userIdx;
    private String nickname;
    private String password;
    private int badgeIdx;
    private String name;
    private int age;
    private int sex;
    private int height;
    private int weight;
    private String status;
}