package com.umc.footprint.src.users.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDateWalk {
    private int walkIdx;
    private String startTime;
    private String endTime;
}
