package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.umc.footprint.config.BaseResponseStatus.*;

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

        String modifyUserGoalTimeQuery = "UPDATE GoalNext SET walkGoalTime = ?, walkTimeSlot = ?, updateAt = NOW() WHERE userIdx = ?";
        Object[] modifyUserGoalTimeParams = new Object[]{ patchUserGoalReq.getWalkGoalTime(), patchUserGoalReq.getWalkTimeSlot(), userIdx };
        return this.jdbcTemplate.update(modifyUserGoalTimeQuery,modifyUserGoalTimeParams);

    }

    // Goal Table에 userIdx에 맞는 요일(dayIdx) MODIFY
    public int modifyUserGoalDay(int userIdx, PatchUserGoalReq patchUserGoalReq){

        Boolean[] days = {false,false,false,false,false,false,false};   // false로 초기화

        for (int dayIdx : patchUserGoalReq.getDayIdx()){ // dayIdx에 해당하는 요일만 true로 변경
            days[dayIdx-1] = true;
        }

        String modifyUserGoalDayQuery = "UPDATE GoalDayNext SET sun = ?, mon = ?, tue = ?, wed = ?, thu = ?, fri = ?, sat = ?, updateAt = NOW() WHERE userIdx = ?";
        Object[] modifyUserGoalDayParams = new Object[]{ days[0], days[1], days[2], days[3], days[4], days[5], days[6], userIdx };
        return this.jdbcTemplate.update(modifyUserGoalDayQuery,modifyUserGoalDayParams);

    }

    // 해당 userIdx를 갖는 Goal의 Time 정보 & GoalDay의 요일 정보 CREATE
    public int postGoal(int userIdx, PostUserGoalReq postUserGoalReq) throws BaseException {

        // Validation 7. Goal Table에 이미 존재하는 userIdx인지 확인
        if(checkUser(userIdx,"GoalNext") == true)
            throw new BaseException(EXIST_USER_ERROR);

        // 1. Goal Table에 userIdx에 맞는 walkGoalTime, walkTimeSlot Create
        int result1; // 1에서 update 확인용 result
        String createUserGoalTimeQuery = "INSERT INTO Goal (userIdx,walkGoalTime,walkTimeSlot) VALUES (?,?,?)";
        Object[] createUserGoalTimeParams = new Object[]{userIdx, postUserGoalReq.getWalkGoalTime(), postUserGoalReq.getWalkTimeSlot()};
        result1 = this.jdbcTemplate.update(createUserGoalTimeQuery,createUserGoalTimeParams);

        // 2. GoalDay Table에 sun~fri Create
        int result2; // 2에서 update 확인용 result
        Boolean[] days = {false,false,false,false,false,false,false};   // false로 초기화

        for (int dayIdx : postUserGoalReq.getDayIdx()){ // dayIdx에 해당하는 요일만 true로 변경
            days[dayIdx-1] = true;
        }

        String createUserGoalDayQuery = "INSERT INTO GoalDay (userIdx,sun,mon,tue,wed,thu,fri,sat) VALUES (?,?,?,?,?,?,?,?)";
        Object[] createUserGoalDayParams = new Object[]{ userIdx, days[0], days[1], days[2], days[3], days[4], days[5], days[6]};
        result2 = this.jdbcTemplate.update(createUserGoalDayQuery,createUserGoalDayParams);

        if (result1 == 0 || result2 == 0) // result1 과 result2 중 하나라도 0이면(영향을 미치지 못함) 0 return
            return 0;

        return 1;
    }


    // 해당 userIdx를 갖는 GoalNext의 Time 정보 & GoalDayNext의 요일 정보 CREATE
    public int postGoalNext(int userIdx, PostUserGoalReq postUserGoalReq) throws BaseException {

        // Validation 7. GoalNext Table에 이미 존재하는 userIdx인지 확인
        if(checkUser(userIdx,"GoalNext") == true)
                throw new BaseException(EXIST_USER_ERROR);

        // 1. GoalNext Table에 userIdx에 맞는 walkGoalTime, walkTimeSlot Create
        int result1; // 1에서 update 확인용 result
        String createUserGoalTimeQuery = "INSERT INTO GoalNext (userIdx,walkGoalTime,walkTimeSlot) VALUES (?,?,?)";
        Object[] createUserGoalTimeNextParams = new Object[]{userIdx, postUserGoalReq.getWalkGoalTime(), postUserGoalReq.getWalkTimeSlot()};
        result1 = this.jdbcTemplate.update(createUserGoalTimeQuery,createUserGoalTimeNextParams);

        // 2. GoalDayNext Table에 sun~fri Create
        int result2; // 2에서 update 확인용 result
        Boolean[] days = {false,false,false,false,false,false,false};   // false로 초기화

        for (int dayIdx : postUserGoalReq.getDayIdx()){ // dayIdx에 해당하는 요일만 true로 변경
            days[dayIdx-1] = true;
        }

        String createUserGoalDayQuery = "INSERT INTO GoalDayNext (userIdx,sun,mon,tue,wed,thu,fri,sat) VALUES (?,?,?,?,?,?,?,?)";
        Object[] createUserGoalDayNextParams = new Object[]{ userIdx, days[0], days[1], days[2], days[3], days[4], days[5], days[6]};
        result2 = this.jdbcTemplate.update(createUserGoalDayQuery,createUserGoalDayNextParams);

        if (result1 == 0 || result2 == 0) // result1 과 result2 중 하나라도 0이면(영향을 미치지 못함) 0 return
            return 0;

        return 1;
    }

    // true = 유저 있다 & false = 유저 없다.
    public boolean checkUser(int userIdx, String tableName) throws BaseException {
        int userCount = this.jdbcTemplate.queryForObject("SELECT count(*) FROM "+ tableName + " WHERE userIdx = ? ",int.class,userIdx);

        if(userCount != 0)
            return true;
        return false;
    }

}
