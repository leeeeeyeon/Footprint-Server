package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.GetFootprintCount;
import com.umc.footprint.src.users.model.GetMonthInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class UserProvider {

    private final UserDao userDao;

    @Autowired
    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
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

    //월별 달성률 및 누적 정보 조회
    public GetMonthInfoRes getMonthRes(int userIdx, int year, int month) throws BaseException {
        //GetMonthInfoRes getMonthInfoRes = userDao.getMonthInfoRes(userIdx, year, month);
        //return getMonthInfoRes;
        try {
            GetMonthInfoRes getMonthInfoRes = userDao.getMonthInfoRes(userIdx, year, month);
            return getMonthInfoRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
