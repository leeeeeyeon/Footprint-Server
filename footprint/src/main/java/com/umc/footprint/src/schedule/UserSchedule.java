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
    @Scheduled(cron = "0 45 20 29 * ?")
    public void changeMonthGoalTest(){

        userDao.updateGoal();
        userDao.updateGoalDay();

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        log.info("=== Schedule now : {} ===",now);
        log.debug("=== Schedule now : {} ===",now);
        
    }


}
