package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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

    // Goal Table에 userIdx에 맞는 walkGoalTime, walkTimeSlot MODIFY
    public int modifyUserGoalTime(int userIdx, PatchUserGoalReq patchUserGoalReq){

        String modifyUserGoalTimeQuery = "UPDATE Goal SET walkGoalTime = ?, walkTimeSlot = ? WHERE userIdx = ?";
        Object[] modifyUserGoalTimeParams = new Object[]{ patchUserGoalReq.getWalkGoalTime(), patchUserGoalReq.getWalkTimeSlot(), userIdx };
        return this.jdbcTemplate.update(modifyUserGoalTimeQuery,modifyUserGoalTimeParams);

    }

    // Goal Table에 userIdx에 맞는 요일(dayIdx) MODIFY
    public int modifyUserGoalDay(int userIdx, PatchUserGoalReq patchUserGoalReq){

        Boolean[] days = {false,false,false,false,false,false,false};   // false로 초기화

        for (int dayIdx : patchUserGoalReq.getDayIdx()){ // dayIdx에 해당하는 요일만 true로 변경
            days[dayIdx-1] = true;
        }

        String modifyUserGoalDayQuery = "UPDATE GoalDay SET sun = ?, mon = ?, tue = ?, wed = ?, thu = ?, fri = ?, sat = ? WHERE userIdx = ?";
        Object[] modifyUserGoalDayParams = new Object[]{ days[0], days[1], days[2], days[3], days[4], days[5], days[6], userIdx };
        return this.jdbcTemplate.update(modifyUserGoalDayQuery,modifyUserGoalDayParams);

    }

}
