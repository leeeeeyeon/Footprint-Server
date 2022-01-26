package com.umc.footprint.src.footprints;

import com.umc.footprint.config.BaseException;
import static com.umc.footprint.config.BaseResponseStatus.*;
import com.umc.footprint.src.footprints.FootprintDao;
import com.umc.footprint.src.footprints.FootprintProvider;
import com.umc.footprint.src.footprints.model.DeleteFootprintRes;
import com.umc.footprint.src.footprints.model.PatchFootprintReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FootprintService {
    private final FootprintDao footprintDao;
    private final FootprintProvider footprintProvider;

    @Autowired
    public FootprintService(FootprintDao footprintDao, FootprintProvider footprintProvider) {
        this.footprintDao = footprintDao;
        this.footprintProvider = footprintProvider;
    }

    // 발자국 수정 (Patch)
    /*
    public void modifyFootprint(PatchFootprintReq patchFootprintReq) throws BaseException {
        try {
            int result = footprintDao.modifyFootprint(patchFootprintReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result 값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FOOTPRINT_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
     */


    // 발자국 삭제 (PATCH)
    public void deleteFootprint(int footprintIdx) throws BaseException {
        try {
            int activeFootprint = footprintDao.activeFootprint(footprintIdx);
            int footprintExist = footprintDao.footprintExist(footprintIdx);

            if (footprintExist == 0) { // 발자국이 존재하지 않을 때
                throw new BaseException(NO_EXIST_FOOTPRINT);
            }
            else if (activeFootprint == 0) { // 이미 삭제된 발자국
                throw new BaseException(DELETED_FOOTPRINT);
            }
            int result = footprintDao.deleteFootprint(footprintIdx); // 발자국 삭제 성공 - 1, 실패 - 0

            // 발자국 삭제 실패 (Footprint, Photo 테이블 중 업데이트 되지 않은 테이블 존재)
            if (result == 0) {
                throw new BaseException(DELETE_FOOTPRINT_FAIL);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
