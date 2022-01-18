package com.umc.footprint.src.users.model;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetUserDateRes {
    private int walkIdx;
    private String startTime;
    private String endTime;
    // private String walkImage;
    private String hashtag;
    // private List<Hashtag> hashtag;

}
