package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.walks.WalkDao;
import com.umc.footprint.src.walks.model.Walk;

import java.time.Duration;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;
import com.umc.footprint.config.BaseResponse;

import com.umc.footprint.src.users.model.GetUserTodayRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import static com.umc.footprint.config.BaseResponseStatus.*;

@Service
public class UserProvider {

    private final WalkDao walkDao;

    @Autowired
    public UserProvider(WalkDao walkDao) {
        this.walkDao = walkDao;
    }


    public List<GetUserTodayRes> getUserToday(int userIdx) throws BaseException {

        List<GetUserTodayRes> userTodayRes = userDao.getUserToday(userIdx);

        return userTodayRes;
    }


    // 해당 userIdx를 갖는 User의 정보 조회
    public GetUserRes getUser(int userIdx) throws BaseException {
        try {
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
