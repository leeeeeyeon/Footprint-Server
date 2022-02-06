package com.umc.footprint.src.users.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.jfr.Timespan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor

public class GetUserRes {
    private int userIdx;
    private String nickname;
    private String username;
    private String email;
    private String status;
    private int badgeIdx;
    private String badgeUrl;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp birth;
    private String sex;
    private int height;
    private int weight;
    private int walkNumber;
}

