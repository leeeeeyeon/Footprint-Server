package com.umc.footprint.src.walks;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.GetFootprintCount;
import com.umc.footprint.src.walks.model.GetWalkInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
