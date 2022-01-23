package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.walks.WalkDao;
import com.umc.footprint.src.walks.model.Walk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class UserProvider {

    private final WalkDao walkDao;

    @Autowired
    public UserProvider(WalkDao walkDao) {
        this.walkDao = walkDao;
    }

}
