package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponseStatus;
import com.umc.footprint.src.users.model.PatchUserGoalReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;
    private final UserProvider userProvider;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider) {
        this.userDao = userDao;
        this.userProvider = userProvider;
    }

    public void modifyGoal(int userIdx, PatchUserGoalReq patchUserGoalReq) throws BaseException{
        try{
            int result = userDao.modifyUserGoal(userIdx, patchUserGoalReq);
            if(result == 0)
                throw new BaseException(BaseResponseStatus.MODIFY_USER_GOAL_FAIL);
        } catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
