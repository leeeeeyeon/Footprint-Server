package com.umc.footprint.src.users.model;

import lombok.*;

@Getter
@NoArgsConstructor
public class PostLoginRes {
    private String jwtId;
    private String status;
    private boolean checkMonthChanged;

    @Builder
    public PostLoginRes(String jwtId, String status, boolean checkMonthChanged) {
        this.jwtId = jwtId;
        this.status = status;
        this.checkMonthChanged = checkMonthChanged;
    }

    public void setCheckMonthChanged(boolean checkFlag) {
        this.checkMonthChanged = checkFlag;
    }
}
