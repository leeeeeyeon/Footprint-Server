package com.umc.footprint.src.walks;

import com.umc.footprint.config.BaseException;

import com.umc.footprint.src.AwsS3Service;
import com.umc.footprint.src.users.UserService;
import com.umc.footprint.src.walks.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.umc.footprint.config.BaseResponseStatus.*;


@Service
public class WalkService {
    private final WalkDao walkDao;
    private final WalkProvider walkProvider;
    private final UserService userService;
    private final AwsS3Service awsS3Service;


    @Autowired
    public WalkService(WalkDao walkDao, WalkProvider walkProvider, UserService userService, AwsS3Service awsS3Service) {
        this.walkDao = walkDao;
        this.walkProvider = walkProvider;
        this.userService = userService;
        this.awsS3Service = awsS3Service;
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
    public List<PostWalkRes> saveRecord(PostWalkReq request) throws BaseException {
        System.out.println("Validation 1. 사진이 하나도 안왔을 때");
        if (request.getPhotos().size() == 1 && ("".equals(request.getPhotos().get(0).getOriginalFilename()))){
            System.out.println("request.getPhotos() = " + request.getPhotos());
            System.out.println("request.getPhotos().get(0).getOriginalFilename() = " + request.getPhotos().get(0).getOriginalFilename());
            System.out.println("request.getPhotos().isEmpty() = " + request.getPhotos().isEmpty());
            throw new BaseException(EMPTY_WALK_PHOTO);
        }

        try {
            System.out.println("1. 동선 이미지: file -> url ");
            // 경로 이미지 URL 생성 및 S3 업로드
            String pathImgUrl = awsS3Service.uploadFile(request.getPhotos().get(0));
            // 동선 이미지 photos에서 pop
            List<MultipartFile> removedPathImgPhotos = request.getPhotos();
            removedPathImgPhotos.remove(0);
            System.out.println("removedPathImgPhotos = " + removedPathImgPhotos);
            request.setRemovedPathImgPhotos(removedPathImgPhotos);
            System.out.println("pathImgUrl = " + pathImgUrl);

            // 라인에 좌표가 올 때 나는 오류 방지 (복제)
            List<List<Double>> safeCoordinate = changeSafeCoordinate(request.getWalk().getCoordinates());

            System.out.println("2. url로 바꾼 동선 이미지 SaveWalk 객체에 저장");
            // string으로 변환한 동선 저장
            request.setWalkStrCoordinate(
                    SaveWalk.builder()
                            .startAt(request.getWalk().getStartAt())
                            .endAt(request.getWalk().getEndAt())
                            .distance(request.getWalk().getDistance())
                            .str_coordinates(convert2DListToString(safeCoordinate))
                            .userIdx(request.getWalk().getUserIdx())
                            .goalRate(walkProvider.getGoalRate(request.getWalk()))
                            .calorie(request.getWalk().getCalorie())
                            .photoMatchNumList(request.getWalk().getPhotoMatchNumList())
                            .build()
            );

            // Walk Table에 삽입 후 생성된 walkIdx return
            System.out.println("3. Walk 테이블에 insert 후 walkIdx 반환");
            int walkIdx = walkDao.addWalk(request.getWalk(), pathImgUrl);


            System.out.println("walkIdx = " + walkIdx);

            if (!request.getFootprintList().isEmpty()) {
                System.out.println("4. 발자국 기록 사진들 List<MultipartFile> -> List<String> 으로 변환");
                List<String> imgUrlList = new ArrayList<>();
                if (request.getPhotos().size() != 0) {
                    imgUrlList = awsS3Service.uploadFile(request.getPhotos());
                }

                //  발자국 Photo 이미지 URL 생성 및 S3 업로드
                System.out.println("5. 발자국 좌표 List<Double> -> String 으로 변환 후 SaveFootprint 객체에 저장");
                ArrayList<SaveFootprint> convertedFootprints = new ArrayList<>();
                int imgInputStartIndex = 0;
                for (int i = 0; i < request.getWalk().getPhotoMatchNumList().size(); i++) {
                    String convertedCoordinate = convertListToString(request.getFootprintList().get(i).getCoordinates());
                    System.out.println("convertedCoordinate = " + convertedCoordinate);
                    int imgInputEndIndex = imgInputStartIndex + request.getWalk().getPhotoMatchNumList().get(i);
                    System.out.println("imgInputEndIndex = " + imgInputEndIndex);
                    if (imgInputEndIndex > request.getPhotos().size()) {
                        throw new BaseException(EXCEED_FOOTPRINT_SIZE);
                    }
                    List<String> imgInputList = new ArrayList<String>(imgUrlList.subList(imgInputStartIndex, imgInputEndIndex));
                    SaveFootprint convertedFootprint = SaveFootprint.builder()
                            .strCoordinate(convertedCoordinate)
                            .write(request.getFootprintList().get(i).getWrite())
                            .recordAt(request.getFootprintList().get(i).getRecordAt())
                            .walkIdx(request.getFootprintList().get(i).getWalkIdx())
                            .hashtagList(request.getFootprintList().get(i).getHashtagList())
                            .imgUrlList(imgInputList)
                            .onWalk(request.getFootprintList().get(i).getOnWalk())
                            .build();

                    convertedFootprints.add(convertedFootprint);
                    imgInputStartIndex = imgInputEndIndex;
                }
                System.out.println("WalkService.saveRecord1");
                request.setConvertedFootprints(convertedFootprints);
                System.out.println("WalkService.saveRecord2");

                // Footprint Table에 삽입 후 생성된 footprintIdx Footprint에 초기화
                System.out.println("6. Footprint 테이블에 삽입후 footprintIdx SaveFootprint 테이블에 삽입");
                for (SaveFootprint footprint : request.getFootprintList()) {
                    System.out.println("footprint.getWrite() = " + footprint.getWrite());
                }
                walkDao.addFootprint(request.getFootprintList(), walkIdx);

                for (SaveFootprint footprint : request.getFootprintList()) {
                    System.out.println("footprint.getFootprintIdx() = " + footprint.getFootprintIdx());
                }


                // Photo Table에 삽입
                System.out.println("7. Photo 테이블에 삽입");
                walkDao.addPhoto(request.getWalk().getUserIdx(), request.getFootprintList());

                // Hashtag Table에 삽입 후 tagIdx(hashtagIdx, footprintIdx) mapping pair 반환
                System.out.println("8. Hashtag 테이블에 삽입 후 매핑된 Idx 반환 ");
                List<Pair<Integer, Integer>> tagIdxList = walkDao.addHashtag(request.getFootprintList());

                System.out.println("tagIdxList = " + tagIdxList);
                if (tagIdxList.size() != 0){
                    for (Pair<Integer, Integer> tag : tagIdxList) {
                        System.out.println("tag.getFirst() = " + tag.getFirst());
                        System.out.println("tag.getSecond() = " + tag.getSecond());
                    }
                }

                if (tagIdxList.size() != 0){// Tag Table에 삽입
                    System.out.println("9. Tag 테이블에 삽입");
                    walkDao.addTag(tagIdxList, request.getWalk().getUserIdx());
                }
            }

            // badge 획득 여부 확인 및 id 반환
            System.out.println("10. badge 획득 여부 확인 후 얻은 badgeIdxList 반환");
            List<PostWalkRes> postWalkResList = new ArrayList<>();
            List<Integer> acquiredBadgeIdxList = walkProvider.getAcquiredBadgeIdxList(request.getWalk().getUserIdx());

            // UserBadge 테이블에 획득한 뱃지 삽입
            System.out.println("11. 얻은 뱃지 리스트 UserBadge 테이블에 삽입");
            if (!acquiredBadgeIdxList.isEmpty()) { // 획득한 뱃지가 있을 경우 삽입
                walkDao.addUserBadge(acquiredBadgeIdxList, request.getWalk().getUserIdx());
            }

            // 처음 산책인지 확인
            if (walkProvider.checkFirstWalk(request.getWalk().getUserIdx()) == 0) {
                userService.modifyRepBadge(request.getWalk().getUserIdx(), 1); //대표 뱃지로 설정
            }

            System.out.println("acquiredBadgeIdxList = " + acquiredBadgeIdxList);
            //획득한 뱃지 넣기 (뱃지 아이디로 뱃지 이름이랑 그림 반환)

            System.out.println("12. 뱃지 리스트로 이름과 url 반환 후 request 객체에 저장");
            postWalkResList = walkProvider.getBadgeInfo(acquiredBadgeIdxList);
            System.out.println("postWalkResList = " + postWalkResList);

            return postWalkResList;

        } catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private ArrayList<List<Double>> changeSafeCoordinate(List<List<Double>> coordinates) {
        ArrayList<List<Double>> safeCoordinate = new ArrayList<>();
        for (List<Double> line : coordinates) {
            // 좌표가 하나만 있는 라인이 있을 때
            System.out.println("line = " + line);
            if (line.size() == 2) {
                System.out.println("if statement enter");
                line.add(line.get(0));
                line.add(line.get(1));
                System.out.println("line = " + line);
            }
            safeCoordinate.add(line);
        }
        return safeCoordinate;
    }

    // List<List<>> -> String in WalkDao
    public String convert2DListToString(List<List<Double>> inputList){

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

    public String convertListToString(List<Double> inputList) {
        System.out.println("inputList = " + inputList);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("POINT(");
        stringBuilder.append(inputList.get(0));
        stringBuilder.append(" ");
        stringBuilder.append(inputList.get(1));
        stringBuilder.append(")");
        String result = stringBuilder.toString();

        return result;
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
    public String deleteWalk(int walkIdx) throws BaseException {
        try {
            //walkIdx 로 footprintIdx 모두 얻어오기
            List<Integer> footprintIdxList = walkDao.getFootprintIdxList(walkIdx);
            for(int footprintIdx : footprintIdxList) {
                //footprintIdx에 해당하는 photo, tag 모두 INACTIVE
                walkDao.inactivePhoto(footprintIdx);
                walkDao.inactiveTag(footprintIdx);
            }

            String result = walkDao.deleteWalk(walkIdx);

            return result;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
