package com.umc.footprint.src.walks;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.AwsS3Service;
import com.umc.footprint.src.walks.model.Footprint;
import com.umc.footprint.src.walks.model.GetBadgeIdx;
import com.umc.footprint.src.walks.model.PostWalkReq;
import com.umc.footprint.src.walks.model.PostWalkRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;

import java.util.List;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;


@Service
public class WalkService {
    private final WalkDao walkDao;
    private final WalkProvider walkProvider;
    private final AwsS3Service awsS3Service;


    @Autowired
    public WalkService(WalkDao walkDao, WalkProvider walkProvider, AwsS3Service awsS3Service) {
        this.walkDao = walkDao;
        this.walkProvider = walkProvider;
        this.awsS3Service = awsS3Service;
    }

    @Transactional
    public PostWalkRes saveRecord(PostWalkReq request) throws BaseException {
        try {
            // 경로 이미지 URL 생성 및 S3 업로드
            String pathImgUrl = awsS3Service.uploadFile(request.getWalk().getPathImg());

            System.out.println("pathImgUrl = " + pathImgUrl);

            // Walk Table에 삽입 후 생성된 walkIdx return
            int walkIdx = walkDao.addWalk(walkProvider.getGoalRate(request.getWalk()), pathImgUrl);

            System.out.println("walkIdx = " + walkIdx);

            for (Footprint footprint : request.getFootprintList()) {
                System.out.println("footprint.getCoordinate() = " + footprint.getCoordinate());
                System.out.println("footprint.getWrite() = " + footprint.getWrite());
                System.out.println("footprint.getRecordAt() = " + footprint.getRecordAt());
            }

            // Footprint Table에 삽입 후 생성된 footprintIdx Footprint에 초기화
            walkDao.addFootprint(request.getFootprintList(), walkIdx);

            for (Footprint footprint : request.getFootprintList()) {
                System.out.println("footprint.getFootprintIdx() = " + footprint.getFootprintIdx());
            }

            //  발자국 Photo 이미지 URL 생성 및 S3 업로드
            for (Footprint footprint : request.getFootprintList()) {
                footprint.setImgUrlList(awsS3Service.uploadFile(footprint.getPhotos()));
                System.out.println("footprint.getImgUrlList() = " + footprint.getImgUrlList());
            }

            // Photo Table에 삽입
            walkDao.addPhoto(request.getWalk().getUserIdx(), request.getFootprintList());

            // Hashtag Table에 삽입 후 tagIdx(hashtagIdx, footprintIdx) mapping pair 반환
            List<Pair<Integer, Integer>> tagIdxList = walkDao.addHashtag(request.getFootprintList());

            for (Pair<Integer, Integer> tag : tagIdxList) {
                System.out.println("tag.getFirst() = " + tag.getFirst());
                System.out.println("tag.getSecond() = " + tag.getSecond());
            }

            // Tag Table에 삽입
            walkDao.addTag(tagIdxList, request.getWalk().getUserIdx());

            // badge 획득 여부 확인 및 id 반환
            PostWalkRes postWalkRes = new PostWalkRes();
            List<Integer> acquiredBadgeIdxList = walkProvider.getAcquiredBadgeIdxList(request.getWalk().getUserIdx());

            // UserBadge 테이블에 획득한 뱃지 삽입
            if (!acquiredBadgeIdxList.isEmpty()) { // 획득한 뱃지가 있을 경우 삽입
                walkDao.addUserBadge(acquiredBadgeIdxList, request.getWalk().getUserIdx());
            }
            System.out.println("acquiredBadgeIdxList = " + acquiredBadgeIdxList);
            //획득한 뱃지 넣기 (뱃지 아이디로 뱃지 이름이랑 그림 반환)

            return postWalkRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
