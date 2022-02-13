package com.umc.footprint.src.footprints;

import com.umc.footprint.config.BaseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umc.footprint.src.footprints.model.*;
import static com.umc.footprint.config.BaseResponseStatus.*;

import java.util.List;

@Service
public class FootprintProvider {
    private final FootprintDao footprintDao;

    @Autowired
    public FootprintProvider(FootprintDao footprintDao) {
        this.footprintDao = footprintDao;
    }

    // 발자국 조회
    public List<GetFootprintRes> getFootprints(int walkIdx) throws BaseException {
        try {
            List<GetFootprintRes> getFootprintRes = footprintDao.getFootprints(walkIdx);

            int walkExist = footprintDao.walkExist(walkIdx);
            if (walkExist == 0) {
                throw new BaseException(INVALID_WALKIDX); // 잘못된 산책 인덱스에 접근 (11111)
            }
            else if (getFootprintRes.isEmpty()){
                throw new BaseException(NO_FOOTPRINT_IN_WALK); // 산책 기록에 발자국이 없을 때
           }
            return getFootprintRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getFootprintWholeIdx(int walkIdx, int footprintIdx) throws BaseException {
        try {
            System.out.println("FootprintProvider.getFootprintWholeIdx");
            System.out.println("walkIdx = " + walkIdx);
            System.out.println("footprintIdx = " + footprintIdx);
            int wholeFootprintIdx = footprintDao.getFootprintWholeIdx(walkIdx, footprintIdx);
            System.out.println(wholeFootprintIdx);
            return wholeFootprintIdx;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
