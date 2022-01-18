package com.umc.footprint.src.users.model;

import lombok.*;

import java.util.ArrayList;

/*
 * userIdx, date에 해당하는 산책 정보 DTO
 * */

@Getter
@Setter
@AllArgsConstructor
public class GetUserDateRes {

    public GetUserDateRes(){}

    private UserDateWalk userDateWalk;
    private ArrayList<String> hashtag;

}
