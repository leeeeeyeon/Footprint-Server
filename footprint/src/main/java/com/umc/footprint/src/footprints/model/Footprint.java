package com.umc.footprint.src.footprints.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class Footprint {
    private int footprintIdx;
    private String write;
    private List<MultipartFile> photos; // 사진 저장
    private List<String> photoList; // Url 저장
    private List<String> tagList;
}
