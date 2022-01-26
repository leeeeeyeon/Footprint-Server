package com.umc.footprint.src.walks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class PostWalkRes {
    // 뱃지 이름, 뱃지 url
    private List<Map<String, String>> badgeList;

}


