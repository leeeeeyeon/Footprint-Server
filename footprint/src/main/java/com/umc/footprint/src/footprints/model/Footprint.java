package com.umc.footprint.src.footprints.model;

import lombok.*;

import java.awt.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class Footprint {
    private int footprintIdx;
    private Point coordinate;
    private String write;
    private Timestamp recordAt;
    private int walkIdx;
    private String status;
    private List<String> photoList;
}
