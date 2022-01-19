package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

import static com.umc.footprint.config.BaseResponseStatus.EXIST_USER_ERROR;

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

    public int modifyUserGoalTime(int userIdx, PatchUserGoalReq patchUserGoalReq){

        // Goal Table에 userIdx에 맞는 walkGoalTime, walkTimeSlot MODIFY
        String modifyUserGoalTimeQuery = "UPDATE Goal SET walkGoalTime = ?, walkTimeSlot = ? WHERE userIdx = ?";
        Object[] modifyUserGoalTimeParams = new Object[]{
                patchUserGoalReq.getWalkGoalTime(), patchUserGoalReq.getWalkTimeSlot(), userIdx
        };
        return this.jdbcTemplate.update(modifyUserGoalTimeQuery,modifyUserGoalTimeParams);

    }

    // ******* 수정 로직 미정 *******
    public int modifyUserGoalDay(int userIdx, PatchUserGoalReq patchUserGoalReq){
        // Goal Table에 userIdx에 맞는 요일(dayIdx) MODIFY
        // *** 어떤 방식으로 수정을 저장할 것인가??

        String modifyUserGoalDayQuery = "UPDATE GoalDay SET dayIdx = ? WHERE userIdx = ?";
        int resultSum = 0;

        for (Integer dayIdx : patchUserGoalReq.getDayIdx()) // GoalDay Table에 요일 하나하나당 튜플 생성
        {
            Object[] modifyUserGoalDayParams = new Object[]{ dayIdx, userIdx };
            resultSum += this.jdbcTemplate.update(modifyUserGoalDayQuery,modifyUserGoalDayParams);
        }
        if (resultSum < patchUserGoalReq.getDayIdx().size())
            return 0;

        return 1;
    }

}
