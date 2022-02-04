package com.umc.footprint.src.walks;

import com.umc.footprint.config.BaseException;

import com.umc.footprint.src.AwsS3Service;
import com.umc.footprint.src.walks.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;


import javax.transaction.Transactional;

import java.util.ArrayList;
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
    public List<PostWalkRes> saveRecord(PostWalkReq request) throws BaseException {
        try {
            // 경로 이미지 URL 생성 및 S3 업로드
            String pathImgUrl = awsS3Service.uploadFile(request.getWalk().getPathImg());

            System.out.println("pathImgUrl = " + pathImgUrl);
            // string으로 변환한 동선 저장
            request.setWalk(new Walk(
                    request.getWalk().getStartAt(),
                    request.getWalk().getEndAt(),
                    convertListToString(request.getCoordinates()),
                    request.getWalk().getDistance(),
                    request.getWalk().getUserIdx(),
                    request.getWalk().getGoalRate(),
                    request.getWalk().getCalorie()
            ));

            // Walk Table에 삽입 후 생성된 walkIdx return
            int walkIdx = walkDao.addWalk(walkProvider.getGoalRate(request.getWalk()), pathImgUrl);

            System.out.println("walkIdx = " + walkIdx);

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
            List<PostWalkRes> postWalkResList = new ArrayList<PostWalkRes>();
            List<Integer> acquiredBadgeIdxList = walkProvider.getAcquiredBadgeIdxList(request.getWalk().getUserIdx());

            // UserBadge 테이블에 획득한 뱃지 삽입
            if (!acquiredBadgeIdxList.isEmpty()) { // 획득한 뱃지가 있을 경우 삽입
                walkDao.addUserBadge(acquiredBadgeIdxList, request.getWalk().getUserIdx());
            }
            System.out.println("acquiredBadgeIdxList = " + acquiredBadgeIdxList);
            //획득한 뱃지 넣기 (뱃지 아이디로 뱃지 이름이랑 그림 반환)

            postWalkResList = walkProvider.getBadgeInfo(acquiredBadgeIdxList);
            System.out.println("postWalkResList = " + postWalkResList);

            return postWalkResList;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // List<List<>> -> String in WalkDao
    public String convertListToString(List<List<Double>> inputList){

        System.out.println("inputList : "+inputList);

        StringBuilder str = new StringBuilder();
        str.append("MULTILINESTRING(");
        int count = 0;  // 1차원 범위의 List 경과 count (마지막 "," 빼기 위해)
        for(List<Double> list : inputList){
            str.append("(");
            for(int i=0;i<list.size();i++){
                str.append(list.get(i));

                if(i == list.size()-1) {    // 마지막은 " " , "," 추가하지 않고 ")"
                    str.append(")");
                    break;
                }

                if (i%2 == 0)   // 짝수 번째 인덱스는 " " 추가
                    str.append(" ");
                else            // 홀수 번째 인덱스는 "," 추가
                    str.append(",");
            }
            count++;
            if(count != inputList.size())    // 1차원 범위의 List에서 마지막을 제외하고 "," 추가
                str.append(",");
        }
        str.append(")");
        String result = str.toString();

        return result;
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
