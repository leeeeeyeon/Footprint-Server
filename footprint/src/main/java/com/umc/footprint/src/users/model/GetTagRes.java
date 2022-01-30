package com.umc.footprint.src.users.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.umc.footprint.src.users.model.Walk;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class GetTagRes {
    private String walkAt;
    private List<Walk> walks;
}
