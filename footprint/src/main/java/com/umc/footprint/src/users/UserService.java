package com.umc.footprint.src.users;

import com.umc.footprint.src.users.model.PatchNicknameReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.umc.footprint.config.BaseException;
import static com.umc.footprint.config.BaseResponseStatus.*;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
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
}
