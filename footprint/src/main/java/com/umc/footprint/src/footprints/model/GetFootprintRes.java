package com.umc.footprint.src.footprints.model;

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
    private Timestamp recordAt;
    private String write;
    private List<String> photoList;
}
