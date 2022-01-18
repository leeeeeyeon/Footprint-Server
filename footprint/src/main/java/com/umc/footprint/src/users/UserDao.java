package com.umc.footprint.src.users;

import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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

    public List<GetUserDateRes> getUserDate(int userIdx, String date){
    String getUserDateQuery = "SELECT W.walkIdx, DATE_FORMAT(W.startAt,'%H:%i') as startTime, DATE_FORMAT(W.endAt,'%H:%i') as endTime, WH.hashtag " +
            "FROM Walk W " +
            "    INNER JOIN (SELECT F.walkIdx ,T.footprintIdx ,H.hashtag " +
            "                FROM Hashtag H " +
            "                INNER JOIN Tag T ON H.hashtagIdx = T.hashtagIdx " +
            "                INNER JOIN Footprint F on T.footprintIdx = F.footprintIdx) as WH " +
            "        ON W.walkIdx = WH.walkIdx " +
            "WHERE W.userIdx = ? and DATE(W.startAt) = DATE(?) ";

    return this.jdbcTemplate.query(getUserDateQuery,
            (rs, rowNum) -> new GetUserDateRes(
                    rs.getInt("walkIdx"),
                    rs.getString("startTime"),
                    rs.getString("endTime"),
                    rs.getString("hashtag")
            ),userIdx,date);

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


}
