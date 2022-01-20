package com.umc.footprint.src.users;

import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<GetUserTodayRes> getUserToday(int userIdx){
        String getUserTodayQuery = "SELECT SUM(W.goalRate) as goalRate, G.walkGoalTime, " +
                "SUM(TIMESTAMPDIFF(minute,W.startAt,W.endAt)) as walkTime, " +
                "SUM(W.distance) as distance, " +
                "SUM(W.calorie) as calorie " +
                "FROM Walk W " +
                "INNER JOIN Goal G " +
                "ON W.userIdx = G.userIdx " +
                "WHERE W.userIdx = ? AND DATE(W.startAt) = DATE(NOW()) ";
        int getUserIdxParam = userIdx;

        return this.jdbcTemplate.query(getUserTodayQuery,
                (rs, rowNum) -> new GetUserTodayRes(
                        rs.getFloat("goalRate"),
                        rs.getInt("walkGoalTime"),
                        rs.getInt("walkTime"),
                        rs.getDouble("distance"),
                        rs.getInt("calorie")
                ),getUserIdxParam);
    }



    // 해당 userIdx를 갖는 유저조회
    public GetUserRes getUser(int userIdx) {
        String getUserQuery = "select * from footprint.User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickname"),
                        rs.getInt("badgeIdx"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getInt("sex"),
                        rs.getInt("height"),
                        rs.getInt("weight")
                ),
                userIdx);
    }

    public GetUserGoalRes getUserGoal(int userIdx){

        // 1-1. get UserGoalDay
        String getUserGoalDayQuery = "SELECT sun, mon, tue, wed, thu, fri, sat FROM GoalDay WHERE userIdx = ?";
        UserGoalDay userGoalDay = this.jdbcTemplate.queryForObject(getUserGoalDayQuery,
                (rs,rowNum) -> new UserGoalDay(
                        rs.getBoolean("sun"),
                        rs.getBoolean("mon"),
                        rs.getBoolean("tue"),
                        rs.getBoolean("wed"),
                        rs.getBoolean("thu"),
                        rs.getBoolean("fri"),
                        rs.getBoolean("sat")
                ),userIdx);

        // 1-2. List<Integer> 형태로 변형
        List<Integer> dayIdx = new ArrayList<>();
        if(userGoalDay.isSun() == true)
            dayIdx.add(1);
        if(userGoalDay.isMon() == true)
            dayIdx.add(2);
        if(userGoalDay.isTue() == true)
            dayIdx.add(3);
        if(userGoalDay.isWed() == true)
            dayIdx.add(4);
        if(userGoalDay.isThu() == true)
            dayIdx.add(5);
        if(userGoalDay.isFri() == true)
            dayIdx.add(6);
        if(userGoalDay.isSat() == true)
            dayIdx.add(7);

        // 2. get UserGoalTime
        String getUserGoalTimeQuery = "SELECT walkGoalTime, walkTimeSlot FROM Goal WHERE userIdx = ?";
        UserGoalTime userGoalTime = this.jdbcTemplate.queryForObject(getUserGoalTimeQuery,
                (rs,rowNum) -> new UserGoalTime(
                        rs.getInt("walkGoalTime"),
                        rs.getInt("walkTimeSlot")
                ), userIdx);

        // 3. GetUserGoalRes에 dayIdx 와 userGoalTime 합침
        return new GetUserGoalRes(dayIdx,userGoalTime);

    }


}
