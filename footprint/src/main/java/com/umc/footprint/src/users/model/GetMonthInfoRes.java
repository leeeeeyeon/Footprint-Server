package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMonthInfoRes {
    private GetGoalDays getGoalDays;
    private List<GetDayRateRes> getDayRatesRes; //일별 달성률?
    private GetMonthTotal getMonthTotal; //누적 3종세트

}
