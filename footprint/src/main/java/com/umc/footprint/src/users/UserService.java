package com.umc.footprint.src.users;

import static com.umc.footprint.config.BaseResponseStatus.*;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponseStatus;
import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }


    public BadgeInfo patchRepBadge(int userIdx, int badgeIdx) throws BaseException {
        try {
            BadgeInfo patchRepBadgeInfo = userDao.patchRepBadge(userIdx, badgeIdx);
            return patchRepBadgeInfo;
          } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 닉네임 수정(Patch)
    public void modifyNickname(PatchNicknameReq patchNicknameReq) throws BaseException {
        try {
            int nicknameExist = userDao.nicknameExist(patchNicknameReq);
            int result = userDao.modifyNickname(patchNicknameReq);

            if (nicknameExist != 0) { // 중복된 닉네임
                throw new BaseException(NICKNAME_EXIST);
            }
            else if (result == 0) { // 닉네임 변경 실패
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

    // 월이 변하면 Goal(Day)Next 데이터를 Goal(Day) 로 옯겨줌 
    public void monthlyChangeGoal(){

    }


    // 해당 userIdx를 갖는 Goal 정보 저장
    @Transactional(rollbackOn = Exception.class)
    public int postGoal(int userIdx, PostUserGoalReq postUserGoalReq) throws BaseException{
        try {
            int result = userDao.postGoal(userIdx, postUserGoalReq);
            int resultNext = userDao.postGoalNext(userIdx, postUserGoalReq);

            if(result == 0 || resultNext ==0)
                return 0;
            return 1;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
