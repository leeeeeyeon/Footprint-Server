package com.umc.footprint.src.users;

import static com.umc.footprint.config.BaseResponseStatus.*;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponseStatus;
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

@Service
public class UserService {
    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
    }


    // yummy 12
    @Transactional(rollbackFor = Exception.class)
    public BadgeInfo modifyRepBadge(int userIdx, int badgeIdx) throws BaseException {
        try {
            //todo
            // 해당 뱃지가 Badge 테이블에 존재하는 뱃지인지?
            // 유저가 해당 뱃지를 갖고 있고, ACTIVE 뱃지인지?

            BadgeInfo patchRepBadgeInfo = userDao.modifyRepBadge(userIdx, badgeIdx);
            return patchRepBadgeInfo;
          } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 닉네임 수정(Patch)
    public void modifyNickname(PatchNicknameReq patchNicknameReq) throws BaseException {
        try {
            int result = userDao.modifyNickname(patchNicknameReq);

            if (result == 0) { // 닉네임 변경 실패
                throw new BaseException(MODIFY_NICKNAME_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
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

            if(resultInfo == 0 || result == 0 || resultNext == 0)
                return 0;
            return 1;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public PostLoginRes postUserLogin(PostLoginReq postLoginReq) throws BaseException {
        // email 중복 확인 있으면 status에 Done 넣고 return
        System.out.println("UserService.postUserLogin1");
        PostLoginRes result = userProvider.checkEmail(postLoginReq.getEmail());
        System.out.println("result.getStatus() = " + result.getStatus());
        switch (result.getStatus()) {
            case "NONE":
                try {
                    System.out.println("UserService.postUserLogin2");
                    // 암호화
                    String jwt = jwtService.createJwt(postLoginReq.getUserId());
                    userDao.postUserLogin(postLoginReq);

                    return PostLoginRes.builder()
                            .jwtId(jwt)
                            .status("ONGOING").build();
                } catch (Exception exception) {
                    throw new BaseException(DATABASE_ERROR);
                }
            case "ACTIVE":
                try {
                    System.out.println("UserService.postUserLogin ACTIVE USER");
                    // userId(구글이나 카카오에서 보낸 ID) 추출 (복호화)
                    String userId = jwtService.getUserId();
                    System.out.println("userId = " + userId);
                    // userId로 userIdx 추출
                    int userIdx = userProvider.getUserIdx(userId);

                    // 이전에 로그인 했던 시간
                    LocalDateTime beforeLogAt = userDao.getUserLogAt(userIdx);
                    ZonedDateTime seoulDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
                    LocalDateTime now = seoulDateTime.toLocalDateTime();
                    // 달이 같은 경우
                    if (beforeLogAt.getMonth() == LocalDateTime.now().getMonth()) {
                        // 달이 바뀌지 않았다고 response에 저장
                        result.setCheckMonthChanged(false);
                    } else {
                        // 달이 바뀌었다고 response에 저장
                        result.setCheckMonthChanged(true);
                    }
                    // 현재 로그인하는 시간 logAt에 저장
                    System.out.println("now = " + now);
                    userDao.modifyUserLogAt(now, userIdx);

                    return result;
                } catch (Exception exception) {
                    throw new BaseException(DATABASE_ERROR);
                }
            case "ONGOING":
                return result;
        }
        return null;
    }
}
