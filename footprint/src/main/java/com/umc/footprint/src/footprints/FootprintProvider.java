package com.umc.footprint.src.footprints;

import com.umc.footprint.config.BaseException;

import com.umc.footprint.config.EncryptProperties;
import com.umc.footprint.utils.AES128;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umc.footprint.src.footprints.model.*;
import static com.umc.footprint.config.BaseResponseStatus.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FootprintProvider {
    private final FootprintDao footprintDao;
    private final EncryptProperties encryptProperties;

    @Autowired
    public FootprintProvider(FootprintDao footprintDao, EncryptProperties encryptProperties) {
        this.footprintDao = footprintDao;
        this.encryptProperties = encryptProperties;
    }

    // 발자국 조회
    public List<GetFootprintRes> getFootprints(int walkIdx) throws BaseException {
        try {
            List<GetFootprintRes> getFootprintRes = footprintDao.getFootprints(walkIdx);

            /* 발자국 조회시 복호화를 위한 코드 : write, photo, tag 복호화 필요 */

            for(GetFootprintRes footprintRes : getFootprintRes){
                List<String> decryptPhotoList = new ArrayList<>();
                List<String> decryptTagList = new ArrayList<>();

                footprintRes.setWrite(new AES128(encryptProperties.getKey()).decrypt(footprintRes.getWrite())); // write 복호화

                for(String photo : footprintRes.getPhotoList()){    // photoList 복호화
                    decryptPhotoList.add(new AES128(encryptProperties.getKey()).decrypt(photo));
                }
                footprintRes.setPhotoList(decryptPhotoList);

                for(String tag : footprintRes.getTagList()){    // tagList 복호화
                    decryptTagList.add(new AES128(encryptProperties.getKey()).decrypt(tag));
                }
                footprintRes.setTagList(decryptTagList);
            }

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
            log.debug("walkIdx: {} ", walkIdx);
            log.debug("footprintIdx: {} ", footprintIdx);
            int wholeFootprintIdx = footprintDao.getFootprintWholeIdx(walkIdx, footprintIdx);
            log.debug("wholeFootprintIdx: {}", wholeFootprintIdx);
            return wholeFootprintIdx;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
