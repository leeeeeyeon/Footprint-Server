package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.BadgeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public BadgeInfo patchRepBadge(int userIdx, int badgeIdx) throws BaseException {
        try {
            BadgeInfo patchRepBadgeInfo = userDao.patchRepBadge(userIdx, badgeIdx);
            return patchRepBadgeInfo;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
