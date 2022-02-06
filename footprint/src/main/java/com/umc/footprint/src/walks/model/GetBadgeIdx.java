package com.umc.footprint.src.walks.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetBadgeIdx {
    // 거리 관련 뱃지
    private Integer distanceBadgeIdx;
    // 기록 관련 뱃지
    private Integer recordBadgeIdx;

    @Builder
    public GetBadgeIdx(Integer distanceBadgeIdx, Integer recordBadgeIdx) {
        this.distanceBadgeIdx = distanceBadgeIdx;
        this.recordBadgeIdx = recordBadgeIdx;
    }

}
