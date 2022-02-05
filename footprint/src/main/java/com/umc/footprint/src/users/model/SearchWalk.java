package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class SearchWalk {
    private UserDateWalk userDateWalk;
    private List<String> hashtag;
}
