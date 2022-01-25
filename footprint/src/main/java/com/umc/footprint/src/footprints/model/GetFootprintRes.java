package com.umc.footprint.src.footprints.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class GetFootprintRes {
    private int footprintIdx;
    @JsonFormat(pattern = "hh:mm", timezone = "Asia/Seoul")
    private Timestamp recordAt;
    private String write;
    private List<String> photoList;
    private List<String> tagList;
}
