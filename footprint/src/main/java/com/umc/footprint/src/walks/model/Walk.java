package com.umc.footprint.src.walks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Walk {
    private int walkIdx;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endAt;
    // coordinate 형식에 따라 string으로 받을 지 multilinestringdm로 받을 지 정함
    private String coordinate;
    private double distance;
    private int userIdx;
    private Float goalRate;
    private int calorie;
    private MultipartFile pathImg;

    public Walk(LocalDateTime startAt, LocalDateTime endAt, String coordinate, double distance, int userIdx,  Float goalRate,int calorie) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.coordinate = coordinate;
        this.distance = distance;
        this.userIdx = userIdx;
        this.goalRate = goalRate;
        this.calorie = calorie;
    }
}


