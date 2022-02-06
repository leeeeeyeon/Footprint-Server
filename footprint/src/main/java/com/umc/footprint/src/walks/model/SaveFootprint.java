package com.umc.footprint.src.walks.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class SaveFootprint {
    private int footprintIdx;
    // coordinate 형식에 따라 string으로 받을 지 point로 받을 지 정함
    private List<Double> coordinates;
    private String strCoordinate;
    private String write;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime recordAt;
    private int walkIdx;

    private List<String> hashtagList;

    // Url 저장
    private List<String> imgUrlList;
    @Builder
    public SaveFootprint(int footprintIdx, List<Double> coordinates, String strCoordinate, String write, LocalDateTime recordAt, int walkIdx, List<String> hashtagList, List<String> imgUrlList) {
        this.footprintIdx = footprintIdx;
        this.coordinates = coordinates;
        this.strCoordinate = strCoordinate;
        this.write = write;
        this.recordAt = recordAt;
        this.walkIdx = walkIdx;
        this.hashtagList = hashtagList;
        this.imgUrlList = imgUrlList;
    }



    public void setWalkIdxOfFootprint(int walkIdx) {
        this.walkIdx = walkIdx;
    }

    public void setFootprintIdx(int footprintIdx) {
        this.footprintIdx = footprintIdx;
    }
}
