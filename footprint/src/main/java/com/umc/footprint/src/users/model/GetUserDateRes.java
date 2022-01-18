package com.umc.footprint.src.users.model;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class GetUserDateRes {

    public GetUserDateRes(){}

    private UserDateWalk userDateWalk;
    private ArrayList<String> hashtag;

}
