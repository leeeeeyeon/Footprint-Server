package com.umc.footprint.src.walks.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteWalkRes {
    private int walkIdx;
    private String content;
}
