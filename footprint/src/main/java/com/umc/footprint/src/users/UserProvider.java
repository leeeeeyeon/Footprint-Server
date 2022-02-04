package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.walks.WalkDao;

import com.umc.footprint.src.users.model.GetUserTodayRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.umc.footprint.src.users.model.*;

import java.util.List;

import static com.umc.footprint.config.BaseResponseStatus.*;

@Service
public class UserProvider {

    private final WalkDao walkDao;
    private final UserDao userDao;

    @Autowired
    public UserProvider(WalkDao walkDao,UserDao userDao) {
        this.walkDao = walkDao;
        this.userDao = userDao;
    }

    // 해당 userIdx를 갖는 오늘 산책 관련 정보 조회
    public List<GetUserTodayRes> getUserToday(int userIdx) throws BaseException {

        List<GetUserTodayRes> userTodayRes = userDao.getUserToday(userIdx);

        return userTodayRes;
    }


    //월별 발자국(일기) 갯수 조회
    public List<GetFootprintCount> getMonthFootprints(int userIdx, int year, int month) throws BaseException {
        try {
            List<GetFootprintCount> getMonthFootprints = userDao.getMonthFootprints(userIdx, year, month);
            return getMonthFootprints;
        } catch (Exception exception) {
          throw new BaseException(DATABASE_ERROR);
        }
    }
  

    // 해당 userIdx를 갖는 date의 산책 관련 정보 조회
    public List<GetUserDateRes> getUserDate(int userIdx, String date) throws BaseException {

        try {
            // Validation 2. Walk Table 안 존재하는 User인지 확인
            boolean existUserResult = userDao.checkUser(userIdx,"Walk");
            if (existUserResult == false)
                throw new BaseException(NOT_EXIST_USER_IN_WALK);

            // Validation 3. 해당 날짜에 User가 기록한 Walk가 있는지 확인
            int existUserDateResult = userDao.checkUserDateWalk(userIdx, date);
            if (existUserDateResult == 0)
                throw new BaseException(NO_EXIST_WALK);

            List<GetUserDateRes> userDateRes = userDao.getUserDate(userIdx, date);

            return userDateRes;
        } catch(Exception exception){

            throw new BaseException(DATABASE_ERROR);
        }
    }


    //월별 달성률 및 누적 정보 조회
    public GetMonthInfoRes getMonthInfoRes(int userIdx, int year, int month) throws BaseException {
        try {
            GetMonthInfoRes getMonthInfoRes = userDao.getMonthInfoRes(userIdx, year, month);
            return getMonthInfoRes;
          } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 해당 userIdx를 갖는 User의 정보 조회
    public GetUserRes getUser(int userIdx) throws BaseException {
        try {
            boolean userExist = userDao.checkUser(userIdx, "User");
            if (userExist == false) {
                throw new BaseException(INVALID_USERIDX);
            }

            String status = userDao.getStatus(userIdx, "User");
            if (status.equals("INACTIVE")) {
                throw new BaseException(INACTIVE_USER);
            }
            else if (status.equals("BLACK")) {
                throw new BaseException(BLACK_USER);
            }
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //yummy
    public GetUserBadges getUserBadges(int userIdx) throws BaseException {
        try {
            GetUserBadges getUserBadges = userDao.getUserBadges(userIdx);
            return getUserBadges;
          } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 해당 userIdx를 갖는 "이번달" Goal 정보 조회
    public GetUserGoalRes getUserGoal(int userIdx) throws BaseException{
        try{
            GetUserGoalRes getUserGoalRes = userDao.getUserGoal(userIdx);
            return getUserGoalRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 "다음달" Goal 정보 조회
    public GetUserGoalRes getUserGoalNext(int userIdx) throws BaseException{
        try{
            GetUserGoalRes getUserGoalRes = userDao.getUserGoalNext(userIdx);
            return getUserGoalRes;
        } catch (Exception exception){
          throw new BaseException(DATABASE_ERROR);
        }
    }
  
    // 해당 userIdx를 갖는 User의 세부 정보 조회
    public GetUserInfoRes getUserInfo(int userIdx) throws BaseException {
        try {
            // 1. user 달성률 정보
            UserInfoAchieve userInfoAchieve = userDao.getUserInfoAchieve(userIdx);
            // 2. user 이번달 목표 정보
            GetUserGoalRes getUserGoalRes = userDao.getUserGoal(userIdx);
            // 3. user 통계 정보
            UserInfoStat userInfoStat = userDao.getUserInfoStat(userIdx);

            // 4. 1+2+3
            return new GetUserInfoRes(userInfoAchieve,getUserGoalRes,userInfoStat);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 유저의 산책기록 중 태그를 포함하는 산책기록 조회
    public List<GetTagRes> getTagResult(int userIdx, String tag) throws BaseException {
        try {
            List<GetTagRes> getTagResult = userDao.getWalks(userIdx, tag);
            if (getTagResult.isEmpty()) { // 검색결과가 없음
                throw new BaseException(NO_EXIST_RESULT);
            }
            return getTagResult;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //yummy 13
    // 이번달 사용자가 얻은 뱃지 조회 (PRO, LOVER, MASTER)
    public BadgeInfo getMonthlyBadgeStatus(int userIdx) throws BaseException {
        try {
            BadgeInfo getBadgeInfo = userDao.getMonthlyBadgeStatus(userIdx);
            if(getBadgeInfo==null) {
                throw new BaseException(NO_MONTHLY_BADGE);
            }
            return getBadgeInfo;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // email을 통해 유저 중복 검사
    public PostLoginRes checkEmail(String email) throws BaseException {
        System.out.println("UserProvider.checkEmail1");
        try {
            if (userDao.checkEmail(email) == 1) {
                System.out.println("UserProvider.checkEmail2");
                PostLoginRes postLoginRes = userDao.getUserIdAndStatus(email);
                postLoginRes.setStatus("DONE");
                return postLoginRes;
            } else {
                return new PostLoginRes("", "NONE");
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
