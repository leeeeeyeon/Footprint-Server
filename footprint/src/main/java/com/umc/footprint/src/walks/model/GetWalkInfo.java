package com.umc.footprint.src.walks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;

@Getter
@Setter
@AllArgsConstructor
public class GetWalkInfo {
    private int walkIdx;
    //private String date; //날짜
    //private String startAt; //산책 시작 시간
    //private String endAt; //산책 끝 시간
    //private String timeString; //산책 시간은 string으로!
    GetWalkTime getWalkTime;
    private int calorie;
    private double distance;
    private int footCount;
    private String pathImageUrl;
}
