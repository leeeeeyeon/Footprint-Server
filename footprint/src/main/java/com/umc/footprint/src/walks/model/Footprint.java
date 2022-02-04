package com.umc.footprint.src.walks.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Footprint {
    private int footprintIdx;
    // coordinate 형식에 따라 string으로 받을 지 point로 받을 지 정함
    private List<Double> coordinates;
    private String str_coordinate;
    private String write;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordAt;
    private int walkIdx;

    private List<String> hashtagList;

    // file 저장
    private List<MultipartFile> photos;

    // Url 저장
    private List<String> imgUrlList;

    public Footprint(String coordinate, String write, LocalDateTime recordAt, List<String> hashtagList, List<MultipartFile> photos) {
        this.str_coordinate = coordinate;
        this.write = write;
        this.recordAt = recordAt;
        this.hashtagList = hashtagList;
        this.photos = photos;
    }
}
