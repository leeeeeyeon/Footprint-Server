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
            int result = footprintDao.deleteFootprint(footprintIdx); // 발자국 삭제 성공 - 1, 실패 - 0

            if (result == 0) {
                throw new BaseException(DELETE_FOOTPRINT_FAIL); // 발자국 삭제 실패
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
