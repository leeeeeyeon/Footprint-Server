package com.umc.footprint.src.users;

import static com.umc.footprint.config.BaseResponseStatus.*;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponseStatus;
import com.umc.footprint.src.AwsS3Service;
import com.umc.footprint.src.users.model.*;

import com.umc.footprint.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService, AwsS3Service awsS3Service) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.awsS3Service = awsS3Service;
    }


    // yummy 12
    @Transactional(rollbackFor = Exception.class)
    public BadgeInfo modifyRepBadge(int userIdx, int badgeIdx) throws BaseException {
        try {
            // 해당 뱃지가 Badge 테이블에 존재하는 뱃지인지?
            if(!userDao.badgeCheck(badgeIdx)) {
                throw new BaseException(INVALID_BADGEIDX);
            }

            // 유저가 해당 뱃지를 갖고 있고, ACTIVE 뱃지인지?
            if(!userDao.userBadgeCheck(userIdx, badgeIdx)) {
                throw new BaseException(NOT_EXIST_USER_BADGE);
            }

            BadgeInfo patchRepBadgeInfo = userDao.modifyRepBadge(userIdx, badgeIdx);
            return patchRepBadgeInfo;
          } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 유저 정보 수정(Patch)
    public void modifyUserInfo(int userIdx, PatchUserInfoReq patchUserInfoReq) throws BaseException {
        try {
            int result = userDao.modifyUserInfo(userIdx, patchUserInfoReq);

            if (result == 0) { // 유저 정보 변경 실패
                throw new BaseException(MODIFY_USERINFO_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void modifyGoal(int userIdx, PatchUserGoalReq patchUserGoalReq) throws BaseException{
        try{
            int resultTime = userDao.modifyUserGoalTime(userIdx, patchUserGoalReq);
            if(resultTime == 0)
                throw new BaseException(BaseResponseStatus.MODIFY_USER_GOAL_FAIL);

            int resultDay = userDao.modifyUserGoalDay(userIdx, patchUserGoalReq);
            if(resultDay == 0)
                throw new BaseException(BaseResponseStatus.MODIFY_USER_GOAL_FAIL);

        } catch(Exception exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    // 해당 userIdx를 갖는 Goal 정보 저장
    @Transactional(rollbackFor = Exception.class)
    public int postUserInfo(int userIdx, PatchUserInfoReq patchUserInfoReq) throws BaseException{
        try {
            int resultInfo = userDao.modifyUserInfo(userIdx, patchUserInfoReq);
            System.out.println("resultInfo = " + resultInfo);
            int result = userDao.postGoal(userIdx, patchUserInfoReq);
            System.out.println("result = " + result);
            int resultNext = userDao.postGoalNext(userIdx, patchUserInfoReq);
            System.out.println("resultNext = " + resultNext);

            //yummy 뱃지 추가
            userDao.postUserBadge(userIdx, 1); // 발자국 스타터 뱃지 부여
            modifyRepBadge(userIdx, 1); //대표 뱃지로 설정

            if(resultInfo == 0 || result == 0 || resultNext == 0)
                return 0;
            return 1;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public PostLoginRes postUserLogin(PostLoginReq postLoginReq) throws BaseException {
        { // email 중복 확인 있으면 status에 Done 넣고 return
            System.out.println("UserService.postUserLogin1");
            PostLoginRes result = userProvider.checkEmail(postLoginReq.getEmail());
            System.out.println("result.getStatus() = " + result.getStatus());
            // status: NONE -> 회원가입(유저 정보 db에 등록 필요)
            // status: ACTIVE -> 로그인
            // status: ACTIVE -> 정보 입력 필요
            switch (result.getStatus()) {
                case "NONE":
                    try {
                        System.out.println("UserService.postUserLogin2");
                        // 암호화
                        String jwt = jwtService.createJwt(postLoginReq.getUserId());
                        // 유저 정보 db에 등록
                        userDao.postUserLogin(postLoginReq);

                        return PostLoginRes.builder()
                                .jwtId(jwt)
                                .status("ONGOING")
                                .checkMonthChanged(false)
                                .build();
                    } catch (Exception exception) {
                        throw new BaseException(DATABASE_ERROR);
                    }
                case "ACTIVE":
                case "ONGOING":
                    return result;
            }
            return null;
        }
    }

    public PostLoginRes modifyUserLogAt(int userIdx) throws BaseException {
        try {
            boolean result = true;

            // 이전에 로그인 했던 시간
            AutoLoginUser autoLoginUser = userDao.getUserLogAt(userIdx);
            PostLoginRes postLoginRes = PostLoginRes.builder()
                    .status(autoLoginUser.getStatus())
                    .build();
            LocalDateTime beforeLogAt = autoLoginUser.getLogAt();
            ZonedDateTime seoulDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
            LocalDateTime now = seoulDateTime.toLocalDateTime();
            // 달이 같은 경우
            if (beforeLogAt.getMonth() == LocalDateTime.now().getMonth()) {
                // 달이 바뀌지 않았다고 response에 저장
                postLoginRes.setCheckMonthChanged(false);
            } else {
                // 달이 바뀌었다고 response에 저장
                postLoginRes.setCheckMonthChanged(true);
            }

            // 현재 로그인하는 시간 logAt에 저장
            System.out.println("now = " + now);
            userDao.modifyUserLogAt(now, userIdx);

            return postLoginRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(int userIdx) throws BaseException {
        try{
            // GoalNext 테이블
            userDao.deleteGoalNext(userIdx);
            // GoalDayNext 테이블
            userDao.deleteGoalDayNext(userIdx);
            // UserBadge 테이블
            userDao.deleteUserBadge(userIdx);
            // Tag 테이블
            userDao.deleteTag(userIdx);

            // Photo 테이블 -> s3에서 이미지 url 먼저 삭제 후 테이블 삭제 필요
            List<String> imageUrlList = userDao.getImageUrlList(userIdx); //S3에서 사진 삭제
            for(String imageUrl : imageUrlList) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/")+1); // 파일 이름만 자르기
                awsS3Service.deleteFile(fileName);
            }
            userDao.deletePhoto(userIdx); //Photo 테이블에서 삭제

            // Footprint 테이블
            userDao.deleteFootprint(userIdx);
            // Walk 테이블
            userDao.deleteWalk(userIdx);
            // User 테이블
            userDao.deleteUser(userIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
