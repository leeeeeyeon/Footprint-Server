package com.umc.footprint.src.walks;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.walks.model.DeleteWalkRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;


@Service
public class WalkService {
    private final WalkDao walkDao;
    private final WalkProvider walkProvider;

    @Autowired
    public WalkService(WalkDao walkDao, WalkProvider walkProvider) {
        this.walkDao = walkDao;
        this.walkProvider = walkProvider;
    }

    public String deleteWalk(int walkIdx) throws BaseException {
        try {
            String result = walkDao.deleteWalk(walkIdx);
            return result;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
