package com.umc.footprint.src.users.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostLoginRes {

    @ApiModelProperty(example = "JWT 토큰")
    private String jwtId;

    @ApiModelProperty(example = "사용자의 상태")
    private String status;

    @ApiModelProperty(example = "달이 변화되었는 지 체크하는 Flag")
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

    public void setJwtId(String jwtId) {
        this.jwtId = jwtId;
    }
}
