package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.List;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;
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

    // 해당 userIdx를 갖는 Goal의 Time정보 저장 (Goal Table)
    public PostUserGoalRes postGoalTime(int userIdx, PostUserGoalReq postUserGoalReq) throws BaseException {

        // Goal Table에 이미 존재하는 userIdx인지 확인 >> GoalDay도 해줘야할까?
        List<ExistUser> existUserIdx = this.jdbcTemplate.query("SELECT userIdx FROM Goal  ",
                (rs, rowNum) -> new ExistUser(rs.getInt("userIdx")));
        for (ExistUser existUser : existUserIdx){
            if(userIdx == existUser.getUserIdx()){
                throw new BaseException(EXIST_USER_ERROR);
            }
        }

        // 1. Goal Table에 userIdx에 맞는 walkGoalTime, walkTimeSlot Create
        String createUserGoalTimeQuery = "INSERT INTO Goal (userIdx,walkGoalTime,walkTimeSlot) VALUES (?,?,?)";
        Object[] createUserGoalTimeParams = new Object[]{
                userIdx, postUserGoalReq.getWalkGoalTime(), postUserGoalReq.getWalkTimeSlot()
        };
        this.jdbcTemplate.update(createUserGoalTimeQuery,createUserGoalTimeParams);

        // 2. Goal Table에 userIdx에 맞는 요일(dayIdx) Create
        String createUserGoalDayQuery = "INSERT INTO GoalDay (userIdx,dayIdx) VALUES (?,?)";

        for (Integer dayIdx : postUserGoalReq.getDayIdx()) // GoalDay Table에 요일 하나하나당 튜플 생성
        {
            Object[] createUserGoalDayParams = new Object[]{ userIdx, dayIdx };
            this.jdbcTemplate.update(createUserGoalDayQuery,createUserGoalDayParams);
        }

        // response 일단 안줌 <- PostUserGoalRes 형식으로 줄까 고민중!
        return null;

    }




}
