package com.umc.footprint.src.footprints.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class PatchFootprintReq {
    private String write;
    private List<MultipartFile> photos; // 사진
    private List<String> tagList; // 태그
}
