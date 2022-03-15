package com.umc.footprint.src.walks.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class SaveWalk {

    @ApiModelProperty(example = "산책 인덱스", hidden = true)
    private int walkIdx;

    @ApiModelProperty(example = "산책 시작 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startAt;

    @ApiModelProperty(example = "산책 종료 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endAt;

    @ApiModelProperty(example = "산책 거리")
    private double distance;

    @ApiModelProperty(example = "산책 좌표")
    private List<List<Double>> coordinates;

    @ApiModelProperty(example = "산책한 유저 인덱스")
    private int userIdx;

    @ApiModelProperty(example = "산책 string 좌표", hidden = true)
    private String strCoordinates;

    @ApiModelProperty(example = "달성률", hidden = true)
    private Double goalRate;

    @ApiModelProperty(example = "소비한 칼로리")
    private int calorie;

    @ApiModelProperty(example = "각 발자국 기록의 사진 개수")
    private List<Integer> photoMatchNumList;

    @Builder
    public SaveWalk(int walkIdx, LocalDateTime startAt, LocalDateTime endAt, double distance, List<List<Double>> coordinates, int userIdx, String strCoordinates, Double goalRate, int calorie, List<Integer> photoMatchNumList) {
        this.walkIdx = walkIdx;
        this.startAt = startAt;
        this.endAt = endAt;
        this.distance = distance;
        this.coordinates = coordinates;
        this.userIdx = userIdx;
        this.strCoordinates = strCoordinates;
        this.goalRate = goalRate;
        this.calorie = calorie;
        this.photoMatchNumList = photoMatchNumList;
    }

    public void setUserIdx(int userIdx) {
        this.userIdx = userIdx;
    }
}


