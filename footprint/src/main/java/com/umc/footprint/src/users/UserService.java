package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.GetUserRes;
import com.umc.footprint.src.users.model.PostUserGoalReq;
import com.umc.footprint.src.users.model.PostUserGoalRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class UserService {
    private final UserDao userDao;
    private final UserProvider userProvider;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider) {
        this.userDao = userDao;
        this.userProvider = userProvider;
    }

    // 해당 userIdx를 갖는 Goal 정보 저장
    @Transactional(rollbackOn = Exception.class)
    public PostUserGoalRes postGoal(int userIdx, PostUserGoalReq postUserGoalReq) throws BaseException{
        try {
            PostUserGoalRes postUserGoalRes = userDao.postGoalTime(userIdx, postUserGoalReq);
            return postUserGoalRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
