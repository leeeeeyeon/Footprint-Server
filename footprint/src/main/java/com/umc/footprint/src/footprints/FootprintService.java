package com.umc.footprint.src.footprints;

import com.umc.footprint.config.BaseException;
import static com.umc.footprint.config.BaseResponseStatus.*;

import com.umc.footprint.config.EncryptProperties;
import com.umc.footprint.src.AwsS3Service;
import com.umc.footprint.src.footprints.model.PatchFootprintReq;
import com.umc.footprint.utils.AES128;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class FootprintService {
    private final FootprintDao footprintDao;
    private final FootprintProvider footprintProvider;
    private final AwsS3Service awsS3Service;
    private final EncryptProperties encryptProperties;

    @Autowired
    public FootprintService(FootprintDao footprintDao, FootprintProvider footprintProvider, AwsS3Service awsS3Service, EncryptProperties encryptProperties) {
        this.footprintDao = footprintDao;
        this.footprintProvider = footprintProvider;
        this.awsS3Service = awsS3Service;
        this.encryptProperties = encryptProperties;
    }

    // 발자국 수정 (Patch)
    @Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
    public void modifyFootprint(PatchFootprintReq patchFootprintReq, int footprintIdx, int userIdx) throws BaseException {
        try {
            // validation - 존재하지 않는, 삭제된 발자국
            int activeFootprint = footprintDao.activeFootprint(footprintIdx);
            int footprintExist = footprintDao.footprintExist(footprintIdx);

            if (footprintExist == 0) { // 발자국이 존재하지 않을 때
                throw new BaseException(NO_EXIST_FOOTPRINT);
            }
            else if (activeFootprint == 0) { // 이미 삭제된 발자국
                throw new BaseException(DELETED_FOOTPRINT);
            }

            // 발자국 수정 과정
            // 1. 본문 수정
            if(patchFootprintReq.getWrite() != null) {

                /* write 부호화 */
                patchFootprintReq.setWrite(new AES128(encryptProperties.getKey()).encrypt(patchFootprintReq.getWrite()));

                int modifyWrite = footprintDao.modifyWrite(patchFootprintReq, footprintIdx);
            }

            // 2. 사진 수정
            // DB에 저장되어 있는 파일 리스트
            List<String> dbPhotoList = footprintDao.getPhotoList(footprintIdx);
            // 전달되어온 파일 리스트
            List<MultipartFile> photos = patchFootprintReq.getPhotos();
            
            if(photos != null) { // 본문만 수정(사진 수정 X)하는 경우 photos 자체가 null이 됨
                if(dbPhotoList.isEmpty()) { // 발자국에 저장된 사진이 존재하지 않음
                    if(!("".equals(photos.get(0).getOriginalFilename()))) { // 전달된 파일이 하나라도 존재
                        uploadImg(photos, userIdx, footprintIdx); // 새로운 사진들 업로드
                    }
                }
                else { // 발자국에 저장된 기존 사진들이 존재
                    if(photos.size()==1 && "".equals(photos.get(0).getOriginalFilename())) { // 전달된 파일이 없음 > 사진을 지우고 싶다는 의미 > 기존 사진 삭제만 진행
                        footprintDao.deletePhotos(footprintIdx); // 기존 사진들 테이블에서 삭제
                    }
                    else {
                        footprintDao.deletePhotos(footprintIdx);
                        uploadImg(photos, userIdx, footprintIdx); // 새로운 사진들 업로드
                    }
                }
            }

            // 3. 태그 수정
            // DB에 저장되어 있는 태그 리스트
            List<String> dbTagList = footprintDao.getTagList(footprintIdx);
            // 전달되어온 태그 리스트
            List<String> tags = new ArrayList<>();

            if(patchFootprintReq.getTagList() != null) {
                for(String tag : patchFootprintReq.getTagList()){
                    tags.add(new AES128(encryptProperties.getKey()).encrypt(tag));
                }
                if(dbTagList.isEmpty()) { // 발자국에 저장된 태그가 존재하지 않음
                    if(!tags.isEmpty()) { // 전달된 태그가 하나라도 존재
                        footprintDao.addTag(tags, userIdx, footprintIdx);
                    }
                }
                else { // 발자국에 저장된 기존 태그들이 존재
                    if(tags.isEmpty()) { // 전달된 리스트가 없음 > 태그를 지우고 싶다는 의미 > 기존 태그 삭제만 진행
                        footprintDao.deleteHashtags(footprintIdx); // 기존 해시태그들 테이블에서 삭제
                    }
                    else {
                        footprintDao.deleteHashtags(footprintIdx);
                        footprintDao.addTag(tags, userIdx, footprintIdx); // 새로운 해시태그들 업로드
                    }
                }
            }

            // updateAt 업데이트
            footprintDao.updateAt(patchFootprintReq, footprintIdx);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 발자국 삭제 (PATCH) - 사진 삭제, 태그 삭제, 발자국 기록 삭제
    @Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
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

            footprintDao.deletePhotos(footprintIdx);
            footprintDao.deleteHashtags(footprintIdx);
            footprintDao.deleteFootprint(footprintIdx); // 발자국 삭제 성공 - 1, 실패 - 0
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 이미지 URL 생성 > S3 업로드 > Photo 테이블 삽입
    @Transactional(rollbackFor = Exception.class)
    public List<String> uploadImg(List<MultipartFile> photos, int userIdx, int footprintIdx) throws BaseException {
        List<String> photoList = new ArrayList<>(); // URL 저장할 리스트

        // 이미지 URL 생성 및 S3 업로드
        try {
            for (MultipartFile photo : photos) {
                String imgUrl = awsS3Service.uploadFile(photo);
                photoList.add(new AES128(encryptProperties.getKey()).encrypt(imgUrl));
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

        // Photo 테이블에 insert
        footprintDao.addPhoto(photoList, userIdx, footprintIdx);

        return photoList;
    }
}
