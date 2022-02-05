package com.umc.footprint.src.footprints.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class GetFootprintRes {
    private int footprintIdx;
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime recordAt;
    private String write;
    private List<String> photoList;
    private List<String> tagList;
}
