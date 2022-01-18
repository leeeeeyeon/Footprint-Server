package com.umc.footprint.src.users.model;

import lombok.*;

/*
 * userIdx, date에 해당하는 산책 정보
 * */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDateWalk {
    private int walkIdx;
    private String startTime;
    private String endTime;
    private String pathImageUrl;
}
