package com.umc.footprint.src.schedule;


import com.umc.footprint.src.users.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@EnableScheduling
@Configuration
public class UserSchedule {

    private final UserDao userDao;

    @Autowired
    public UserSchedule(UserDao userDao){
        this.userDao = userDao;
    }



    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void changeMonthGoal(){
        userDao.updateGoal();
        userDao.updateGoalDay();
    }

    @Transactional
    @Scheduled(cron = "0 10 9 29 * ?")
    public void changeMonthGoalTestBack(){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        log.info("now_back : {}",now);
        log.debug("now_back : {}",now);
    }

    @Transactional
    @Scheduled(cron = "0 10 18 29 * ?")
    public void changeMonthGoalTest(){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        log.info("now : {}",now);
        log.debug("now : {}",now);
    }

    @Transactional
    @Scheduled(cron = "0 10 3 30 * ?")
    public void changeMonthGoalTesFore(){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        log.info("now_fore : {}",now);
        log.debug("now_fore : {}",now);
    }

}
