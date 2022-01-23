package com.umc.footprint.src.schedule;


import com.umc.footprint.src.users.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
public class UserSchedule {

    private final UserDao userDao;

    @Autowired
    public UserSchedule(UserDao userDao){
        this.userDao = userDao;
    }

    @Scheduled(cron = "0 0 12 1 1/1 ? *")
    public void changeMonthGoal(){
        userDao.updateGoal();
        userDao.updateGoalDay();
    }


}
