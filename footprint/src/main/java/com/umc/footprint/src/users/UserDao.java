package com.umc.footprint.src.users;

import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // [ GET ]
    // 해당 userIdx를 갖는 오늘 산책 관련 정보 조회
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

    public int checkUserInWalk(int userIdx){

        String checkUserWalkQuery = "SELECT userIdx FROM Walk WHERE userIdx = ? GROUP BY userIdx";
        List<Integer> existUser =  this.jdbcTemplate.queryForList(checkUserWalkQuery,int.class,userIdx);

        if(existUser.size() != 0)
            return 1;

        return 0;
    }

    public int checkUserDateWalk(int userIdx, String date){

        String checkUserWalkQuery = "SELECT userIdx FROM Walk WHERE DATE(startAt) = DATE(?) and userIdx = ? GROUP BY userIdx";
        List<Integer> existWalk =  this.jdbcTemplate.queryForList(checkUserWalkQuery,int.class,date,userIdx);

        if (existWalk.size() != 0)
            return 1;

        return 0;
    }

    // 해당 userIdx를 갖는 date의 산책 관련 정보 조회
    public List<GetUserDateRes> getUserDate(int userIdx, String date) {

        // 1. Walk 정보 가져오기
        String getUserDateWalkQuery = "SELECT walkIdx, DATE_FORMAT(startAt,'%H:%i') as startTime, DATE_FORMAT(endAt,'%H:%i') as endTime, pathImageUrl " +
                "FROM Walk " +
                "WHERE userIdx = ? and DATE(startAt) = DATE(?) ";

        List<UserDateWalk> userDateWalkInfo = this.jdbcTemplate.query(getUserDateWalkQuery, (rs, rowNum) -> new UserDateWalk(
                rs.getInt("walkIdx"),
                rs.getString("startTime"),
                rs.getString("endTime"),
                rs.getString("pathImageUrl")
        ),userIdx,date);

        // 2-1. Hashtag 정보 가져오기
        String getHashtagQuery = "SELECT F.walkIdx ,H.hashtag " +
                "FROM Hashtag H " +
                "    INNER JOIN Tag T ON H.hashtagIdx = T.hashtagIdx " +
                "    INNER JOIN Footprint F on T.footprintIdx = F.footprintIdx ";

        List<Hashtag> entireHashtag = this.jdbcTemplate.query(getHashtagQuery, (rs, rowNum) -> new Hashtag(
                rs.getInt("walkIdx"),
                rs.getString("hashtag")
                ));


        // 2-2. entireHashtag 를 WalkIdx 단위로 묶어주기(entireHashtag -> hashtagList)
        List<ArrayList<String>> hashtagList = new ArrayList<ArrayList<String>>(); // 2차원 Arraylist 생성
        int count = 0;  // 1차원단 count 값
        for (int i=0 ; i<entireHashtag.size() ; i++){
            if(i == 0)  // 초기 i=0 일때 1차원단에 ArrayList 하나 생성
                hashtagList.add(new ArrayList<String>());

            hashtagList.get(count).add(entireHashtag.get(i).getHashtag());  // entireHashtag의 hashtag값을 순서대로 2차원단 ArrayList에 추가

            // i 가 마지막 loop일때 && 다음 나올 entireHashTag의 값이 다른 값일 때, 1차원단 ArrayList 하나 추가 AND count++
            if(i != (entireHashtag.size()-1) && entireHashtag.get(i).getWalkIdx() != entireHashtag.get(i+1).getWalkIdx()) {
                hashtagList.add(new ArrayList<String>());
                count++;
            }
        }

        // 3. Walk 와 HashTag 정보 묶어 처리하기
        List<GetUserDateRes> getUserDateRes = new ArrayList<GetUserDateRes>();
        for(int i=0; i<userDateWalkInfo.size(); i++){   // userDateWalkInfo.size() == hashtagList.size() 이므로 userDateWalkInfo.size() 만큼 loop
            getUserDateRes.add(new GetUserDateRes(userDateWalkInfo.get(i),hashtagList.get(i)));
        }

        return getUserDateRes;

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
