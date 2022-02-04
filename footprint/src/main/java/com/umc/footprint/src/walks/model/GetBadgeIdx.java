package com.umc.footprint.src.walks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetBadgeIdx {
    // 거리 관련 뱃지
    private Integer distanceBadgeIdx;
    // 기록 관련 뱃지
    private Integer recordBadgeIdx;
    // 달성률 관련 뱃지 (PRO, LOVER)
    private List<Integer> goalRateBadgeIdxList;

    public GetBadgeIdx(int distanceBadgeIdx, int recordBadgeIdx) {
        this.distanceBadgeIdx = distanceBadgeIdx;
        this.recordBadgeIdx = recordBadgeIdx;
    }
}
