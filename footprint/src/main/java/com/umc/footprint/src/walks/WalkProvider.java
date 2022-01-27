package com.umc.footprint.src.walks;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.walks.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class WalkProvider {
    private final WalkDao walkDao;

    @Autowired
    public WalkProvider(WalkDao walkDao) {
        this.walkDao = walkDao;
    }


    public GetWalkInfo getWalkInfo(int walkIdx) throws BaseException {
        GetWalkInfo getWalkInfo = walkDao.getWalkInfo(walkIdx);
        return getWalkInfo;
        /*try {
            GetWalkInfo getWalkInfo = walkDao.getWalkInfo(walkIdx);
            return getWalkInfo;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }*/
  }

    //
    public Walk getGoalRate(Walk walk) throws BaseException {
        try {
            // 산책 시간
            Integer walkTime = Math.toIntExact(Duration.between(walk.getStartAt(), walk.getEndAt()).toMinutes());
            // 산책 목표 시간
            Integer walkGoalTime = walkDao.getWalkGoalTime(walk.getUserIdx());
            // (산책 끝 시간 - 산책 시작 시간) / 산책 목표 시간
            float goalRate =walkTime.floatValue() / walkGoalTime.floatValue();

            // 100퍼 넘을 시 100으로 고정
            if (goalRate >= 1.0) {
                goalRate = 1.0f;
            }

            Walk newWalk = new Walk(walk.getStartAt(),
                    walk.getEndAt(),
                    walk.getCoordinate(),
                    walk.getDistance(),
                    walk.getUserIdx(),
                    goalRate,
                    walk.getCalorie());

            return newWalk;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public List<Integer> getAcquiredBadgeIdxList(int userIdx) throws BaseException {
        try {
            // 조건에 합하는 뱃지 조회
            GetBadgeIdx getBadgeIdx = walkDao.getAcquiredBadgeIdxList(userIdx);
            System.out.println("getBadgeIdx.getDistanceBadgeIdx() = " + getBadgeIdx.getDistanceBadgeIdx());
            System.out.println("getBadgeIdx.getRecordBadgeIdx() = " + getBadgeIdx.getRecordBadgeIdx());
            // 원래 가지고 있던 뱃지 조회
            List<Integer> getOriginBadgeIdx = walkDao.getOriginBadgeIdxList(userIdx);
            for (Integer badgeIdx : getOriginBadgeIdx) {
                System.out.println("badgeIdx = " + badgeIdx);
            }

            // 얻은 뱃지
            List<Integer> acquiredBadgeIdxList = new ArrayList<>();
            // 기존에 있는 뱃지의 id랑 비교하여 새로 얻은 뱃지 판별
            for (Integer badgeIdx : getOriginBadgeIdx) {
                if (badgeIdx >= 2 && badgeIdx <= 5) {
                // 거리 관련 뱃지
                    // 새로 획득하는 뱃지 존재!
                    if (getBadgeIdx.getDistanceBadgeIdx() > badgeIdx) {
                        // 누적 거리 뱃지를 여러 개 달성할 경우
                        for (int i = badgeIdx + 1; i <= getBadgeIdx.getDistanceBadgeIdx(); i++) {
                            acquiredBadgeIdxList.add(i);
                        }
                    }
                } else if (badgeIdx >= 6 && badgeIdx <= 10) {
                // 기록 관련 뱃지
                    // 새로 획득하는 뱃지 존재!
                    if (getBadgeIdx.getRecordBadgeIdx() > badgeIdx) {
                        acquiredBadgeIdxList.get(getBadgeIdx.getRecordBadgeIdx());
                    }
                    // 처음 뱃지 달성 했을 때, 발자국 스타터 뱃지 밖에 없을 때
                } else if (badgeIdx == 1 && getOriginBadgeIdx.size() == 1) {
                    // 거리 관련 뱃지를 달성했을 때
                    if (getBadgeIdx.getDistanceBadgeIdx() != null && getBadgeIdx.getDistanceBadgeIdx() != 0) {
                        for (int i = 2; i <= getBadgeIdx.getDistanceBadgeIdx(); i++) {
                            acquiredBadgeIdxList.add(i);
                        }
                    }
                    // 기록 관련 뱃지를 달성했을 때
                    if (getBadgeIdx.getRecordBadgeIdx() != null && getBadgeIdx.getRecordBadgeIdx() != 0) {
                        acquiredBadgeIdxList.add(getBadgeIdx.getRecordBadgeIdx());
                    }
                }
            }
            return acquiredBadgeIdxList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
