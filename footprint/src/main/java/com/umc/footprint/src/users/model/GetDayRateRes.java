package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetDayRateRes { //일별 달성률(월별 달성률 보낼 때 리스트로 사용)
    private int day; //날짜
    private float rate; //달성률
}
