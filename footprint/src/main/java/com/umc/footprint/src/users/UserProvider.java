package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;

import com.umc.footprint.src.users.model.GetUserTodayRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProvider {

    private final UserDao userDao;

    @Autowired
    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<GetUserTodayRes> getUserToday(int userIdx) throws BaseException {

        List<GetUserTodayRes> userTodayRes = userDao.getUserToday(userIdx);

        return userTodayRes;
    }

}
