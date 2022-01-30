package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class Walk {
    int walkIdx;
    String walkTime;
    String pathImageUrl;
    List<String> tagList;
}
