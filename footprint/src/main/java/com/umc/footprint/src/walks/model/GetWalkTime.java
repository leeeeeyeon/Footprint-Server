package com.umc.footprint.src.walks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetWalkTime {
    private String date; //날짜
    private String startAt; //산책 시작 시간
    private String endAt; //산책 끝 시간
    private String timeString; //산책 시간은 string으로!

    public void convTimeString() {
        int time = Integer.parseInt(timeString);

//        int hour = time / 3600;
//        time %= 3600;
        int min = time / 60;
        int sec = time % 60;

        String str;
        str = String.format("%02d:%02d", min, sec );
//        if(hour == 0) {
//            str = String.format("%02d:%02d", min, sec );
//        }
//        else {
//            str = String.format("%02d:%02d:%02d", hour, min, sec );
//        }

        setTimeString(str);
    }
}


