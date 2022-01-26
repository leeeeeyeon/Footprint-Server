package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.transaction.Transactional;



import static com.umc.footprint.config.BaseResponseStatus.*;

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
                "INNER JOIN (SELECT useridx, walkGoalTime FROM Goal WHERE useridx = ? and MONTH(createAt) = MONTH(NOW())) as G " +
                "ON W.userIdx = G.userIdx " +
                "WHERE W.userIdx = ? AND DATE(W.startAt) = DATE(NOW()) " +
                "GROUP BY G.walkGoalTime ";
        int getUserIdxParam = userIdx;

        return this.jdbcTemplate.query(getUserTodayQuery,
                (rs, rowNum) -> new GetUserTodayRes(
                        rs.getFloat("goalRate"),
                        rs.getInt("walkGoalTime"),
                        rs.getInt("walkTime"),
                        rs.getDouble("distance"),
                        rs.getInt("calorie")
                ),getUserIdxParam,getUserIdxParam);
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
