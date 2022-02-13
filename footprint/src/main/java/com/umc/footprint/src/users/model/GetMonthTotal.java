package com.umc.footprint.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMonthTotal {
    private int monthTotalMin; //이번달 총 산책 시간(누적)
    private double monthTotalDistance; //이번달 총 산책 거리(누적)
    private int monthPerCal; //평균 칼로리

    public void avgCal(int dayCount){
        setMonthPerCal(monthPerCal/dayCount);
    }

    public void convertSecToMin() {
        setMonthTotalMin(monthTotalMin/60);
    }
}
