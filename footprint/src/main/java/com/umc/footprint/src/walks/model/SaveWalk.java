package com.umc.footprint.src.walks.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class SaveWalk {
    private int walkIdx;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endAt;
    private double distance;
    private List<List<Double>> coordinates;
    private int userIdx;
    private String str_coordinates;
    private Float goalRate;
    private int calorie;
    private List<Integer> photoMatchNumList;

    @Builder
    public SaveWalk(int walkIdx, LocalDateTime startAt, LocalDateTime endAt, double distance, List<List<Double>> coordinates, int userIdx, String str_coordinates, Float goalRate, int calorie, List<Integer> photoMatchNumList) {
        this.walkIdx = walkIdx;
        this.startAt = startAt;
        this.endAt = endAt;
        this.distance = distance;
        this.coordinates = coordinates;
        this.userIdx = userIdx;
        this.str_coordinates = str_coordinates;
        this.goalRate = goalRate;
        this.calorie = calorie;
        this.photoMatchNumList = photoMatchNumList;
    }
}


