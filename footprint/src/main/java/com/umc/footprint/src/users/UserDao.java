package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static com.umc.footprint.config.BaseResponseStatus.INVALID_USERIDX;

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

    // 해당 userIdx를 갖는 유저의 "이번달" 목표 조회
    public GetUserGoalRes getUserGoal(int userIdx) throws BaseException {

        // Validation 1. Goal Table에 없는 userIdx인지 확인
        List<ExistUser> existUserIdx = this.jdbcTemplate.query("SELECT userIdx FROM Goal WHERE userIdx = ? ",
                (rs, rowNum) -> new ExistUser(rs.getInt("userIdx")),userIdx);
        if(existUserIdx.size() == 0)
            throw new BaseException(INVALID_USERIDX);


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



    // 해당 userIdx를 갖는 유저의 달성정보 조회
    public UserInfoAchieve getUserInfoAchieve(int userIdx){

        /*
         *   [ 1. 오늘 목표 달성률 계산 ] = todayGoalRate
         * */
        String getUserTodayGoalRateQuery = "SELECT SUM(goalRate) FROM Walk WHERE userIdx = ? and DATE(startAt) = DATE(NOW())";
        int todayGoalRate = this.jdbcTemplate.queryForObject(getUserTodayGoalRateQuery,int.class,userIdx);


        /*
        *   [ 2. 이번달 목표 달성률 계산 ] = monthGoalRate
        * */

        // method monthlyGoalRate(userIdx,0) 호출
        int monthGoalRate = getMonthGoalRate(userIdx,0);

        /*
         *   [ 3. 산책 횟수 계산 ] = userWalkCount
         * */

        String getUserWalkCountQuery = "SELECT COUNT(*) as walkCount FROM Walk WHERE userIdx = ?";
        int userWalkCount = this.jdbcTemplate.queryForObject(getUserWalkCountQuery,int.class,userIdx);


        return new UserInfoAchieve(todayGoalRate,monthGoalRate,userWalkCount);
    }

    // 유저 통계치 정보 제공
    public void getUserInfoStat(int userIdx){

        // [ 1. 이전 3달 기준 요일별 산책 비율 ] = List<String> mostWalkDay & List<Double> userWeekDayRate

        // 1-1. 이전 3달 기준 요일별 산책 수 확인
        List<Integer> userWeekDayCount = new ArrayList<>();
        List<Double> userWeekDayRate = new ArrayList<>();

        String getUserSunCountQuery = "SELECT count(*) FROM Walk Where userIdx = ? and WEEKDAY(startAt) = 6 and startAt > DATE_SUB(DATE(CURRENT_TIMESTAMP), INTERVAL 3 MONTH )";
        userWeekDayCount.add(this.jdbcTemplate.queryForObject(getUserSunCountQuery,int.class,userIdx));

        String getUserMonCountQuery = "SELECT count(*) FROM Walk Where userIdx = ? and WEEKDAY(startAt) = 0 and startAt > DATE_SUB(DATE(CURRENT_TIMESTAMP), INTERVAL 3 MONTH )";
        userWeekDayCount.add(this.jdbcTemplate.queryForObject(getUserMonCountQuery,int.class,userIdx));

        String getUserTueCountQuery = "SELECT count(*) FROM Walk Where userIdx = ? and WEEKDAY(startAt) = 1 and startAt > DATE_SUB(DATE(CURRENT_TIMESTAMP), INTERVAL 3 MONTH )";
        userWeekDayCount.add(this.jdbcTemplate.queryForObject(getUserTueCountQuery,int.class,userIdx));

        String getUserWedCountQuery = "SELECT count(*) FROM Walk Where userIdx = ? and WEEKDAY(startAt) = 2 and startAt > DATE_SUB(DATE(CURRENT_TIMESTAMP), INTERVAL 3 MONTH )";
        userWeekDayCount.add(this.jdbcTemplate.queryForObject(getUserWedCountQuery,int.class,userIdx));

        String getUserThuCountQuery = "SELECT count(*) FROM Walk Where userIdx = ? and WEEKDAY(startAt) = 3 and startAt > DATE_SUB(DATE(CURRENT_TIMESTAMP), INTERVAL 3 MONTH )";
        userWeekDayCount.add(this.jdbcTemplate.queryForObject(getUserThuCountQuery,int.class,userIdx));

        String getUserFriCountQuery = "SELECT count(*) FROM Walk Where userIdx = ? and WEEKDAY(startAt) = 4 and startAt > DATE_SUB(DATE(CURRENT_TIMESTAMP), INTERVAL 3 MONTH )";
        userWeekDayCount.add(this.jdbcTemplate.queryForObject(getUserFriCountQuery,int.class,userIdx));

        String getUserSatCountQuery = "SELECT count(*) FROM Walk Where userIdx = ? and WEEKDAY(startAt) = 5 and startAt > DATE_SUB(DATE(CURRENT_TIMESTAMP), INTERVAL 3 MONTH )";
        userWeekDayCount.add(this.jdbcTemplate.queryForObject(getUserSatCountQuery,int.class,userIdx));

        // 1-2. 이전 3달 기준 전체 산책 수 구하기
        int entireCount = 0;
        for(Integer dayCount : userWeekDayCount){
            entireCount += dayCount;
        }

        // 1-3. 가장 산책이 많은 요일 추출 (동일 max 존재시 둘다 return) = List<String> mostWalkDay
        List<String> mostWalkDay = new ArrayList<>();

        if(entireCount == 0) // 최근 3개월간 산책 기록이 없을때
            mostWalkDay.add("최근 3개월간 산책을 하지 않았어요");
        else{
            int max = Collections.max(userWeekDayCount);

            for (int i=0; i<userWeekDayCount.size(); i++){
                if(userWeekDayCount.get(i) == max) {
                    switch (i) {
                        case 0:
                            mostWalkDay.add("일요일");
                            break;
                        case 1:
                            mostWalkDay.add("월요일");
                            break;
                        case 2:
                            mostWalkDay.add("화요일");
                            break;
                        case 3:
                            mostWalkDay.add("수요일");
                            break;
                        case 4:
                            mostWalkDay.add("목요일");
                            break;
                        case 5:
                            mostWalkDay.add("금요일");
                            break;
                        case 6:
                            mostWalkDay.add("토요일");
                            break;
                    }
                }
            }
        }

        // 1-4. 요일별 비율 구하기
        // *** 순서 : 일 월 화 수 목 금 토 ***
        for(Integer dayCount : userWeekDayCount){
            userWeekDayRate.add(dayCount/(double)entireCount*100);
        }

        // [ 2. 이전 6달 범위 월별 산책 횟수 ] = thisMonthWalkCount + List<Integer> monthlyWalkCount
        // List 순서 : -6달 , -5달 , ... , 전달 , 이번달 (총 7개 element)

        // 2-1. 이번달 산책 횟수 가져오기
        String userThisMonthWalkCountQuery = "SELECT count(*) FROM Walk WHERE userIdx = ? and MONTH(startAt) = MONTH(CURRENT_TIMESTAMP)";
        int thisMonthWalkCount = this.jdbcTemplate.queryForObject(userThisMonthWalkCountQuery, int.class, userIdx);

        // 2-2. 이전 6달 범위 유저 월별 산책 횟수 가져오기
        String userMonthlyWalkCountQuery = "SELECT count(*) FROM Walk WHERE userIdx = ? and MONTH(startAt) = MONTH(DATE_SUB(CURRENT_TIMESTAMP, INTERVAL ? MONTH))";

        List<Integer> monthlyWalkCount = new ArrayList<>();
        for(int i=6; i>=0; i--)
            monthlyWalkCount.add(this.jdbcTemplate.queryForObject(userMonthlyWalkCountQuery, int.class, userIdx, i));

        System.out.println("[ thisMonthWalk ]");
        System.out.println(thisMonthWalkCount);

        System.out.println("[ monthlyWalkCount ]");
        for(Integer m : monthlyWalkCount)
            System.out.println(m);


        // [ 3. 이전 5달 범위 월별 달성률 & 평균 달성률 ] = monthlyGoalRate + avgGoalRate

        List<Integer> monthlyGoalRate = new ArrayList<>();
        int sumGoalRate = 0;
        for(int i=0; i<6; i++) {
            monthlyGoalRate.add(getMonthGoalRate(userIdx, i));
            sumGoalRate += monthlyGoalRate.get(i);
        }

        int avgGoalRate = (int)((double)sumGoalRate / 6);


    }

    // 월 단위 달성률 계산 method
    public int getMonthGoalRate(int userIdx, int beforeMonth){

        // 1. 사용자의 원하는 달 전체 산책 시간 확인 (초 단위)
        String getUserMonthWalkTimeQuery = "SELECT SUM(TIMESTAMPDIFF(second ,startAt,endAt)) as monthWalkTime FROM Walk WHERE userIdx = ? and MONTH(startAt) = MONTH(DATE_SUB(CURRENT_TIMESTAMP, INTERVAL ? MONTH)";
        int userMonthWalkTime = this.jdbcTemplate.queryForObject(getUserMonthWalkTimeQuery,int.class,userIdx,beforeMonth);

        // 2. 이번달 목표 시간 계산
        // 2-1. 사용자 목표 요일 정보 확인
        String getUserGoalDayQuery = "SELECT sun, mon, tue, wed, thu, fri, sat FROM GoalDay WHERE userIdx = ? and MONTH(createAt) = MONTH(DATE_SUB(CURRENT_TIMESTAMP, INTERVAL ? MONTH)";
        UserGoalDay userGoalDay = this.jdbcTemplate.queryForObject(getUserGoalDayQuery,
                (rs,rowNum) -> new UserGoalDay(
                        rs.getBoolean("sun"),
                        rs.getBoolean("mon"),
                        rs.getBoolean("tue"),
                        rs.getBoolean("wed"),
                        rs.getBoolean("thu"),
                        rs.getBoolean("fri"),
                        rs.getBoolean("sat")
                ),userIdx,beforeMonth);

        // 2-2. 원하는 달 요일별 횟수 정보 확인
        // 2-2-1. 원하는 달 최대 일수 알아오기
        LocalDate month = LocalDate.now().minusMonths(beforeMonth);
        int monthLength = month.lengthOfMonth();

        // 2-2-2. 해당 월 첫 요일 알아오기
        // *** 1:월 / 2:화 / ... / 7:일 ***
        LocalDate firstDay = LocalDate.of(month.getYear(), month.getMonth(),1);
        DayOfWeek dayOfWeek = firstDay.getDayOfWeek();
        int firstDayIdx = dayOfWeek.getValue(); // 첫 요일 인덱스

        // 2-2-3. 첫 요일 기준 그 달의 요일 수 계산
        int weekNum = monthLength/7;
        int moreDay = monthLength%7;

        // 2-2-4. 해당 달의 요일별 횟수 저장
        // dayCountArray[0] = 해당 달의 첫 요일(2021년 1월 기준 토요일)
        int[] dayCountArray = {weekNum,weekNum,weekNum,weekNum,weekNum,weekNum,weekNum};
        for (int i=0; i<moreDay; i++){
            dayCountArray[i]++;
        }

        // 2-3. 해당 달 목표 시간 계산
        // 2-3-1. GoalDay Table이 true 인 요일만 countDay에 sum
        int countDay =0 ; // 해당 달의 선택한 일수 총합
        int loopIdx = firstDayIdx; // firstDayIdx 를 시작으로 loop 을 돌 idx
        for (int i=0; i<7; i++) {
            switch (loopIdx % 7) {
                case 1: // 월요일
                    if(userGoalDay.isMon() == true)
                        countDay += dayCountArray[i];
                    break;
                case 2: // 화요일
                    if(userGoalDay.isTue() == true)
                        countDay += dayCountArray[i];
                    break;
                case 3: // 수요일
                    if(userGoalDay.isWed() == true)
                        countDay += dayCountArray[i];
                    break;
                case 4: // 목요일
                    if(userGoalDay.isThu() == true)
                        countDay += dayCountArray[i];
                    break;
                case 5: // 금요일
                    if(userGoalDay.isFri() == true)
                        countDay += dayCountArray[i];
                    break;
                case 6: // 토요일
                    if(userGoalDay.isSat() == true)
                        countDay += dayCountArray[i];
                    break;
                case 0: // 일요일
                    if(userGoalDay.isSun() == true)
                        countDay += dayCountArray[i];
                    break;
            }
            loopIdx++;
        }

        // 2-3-2. 하루 산책 목표 시간 확인
        String getUserWalkGoalTimeQuery = "SELECT walkGoalTime FROM Goal WHERE userIdx = ? and MONTH(createAt) = MONTH(DATE_SUB(CURRENT_TIMESTAMP, INTERVAL ? MONTH)";
        int userWalkGoalTime = this.jdbcTemplate.queryForObject(getUserWalkGoalTimeQuery,int.class,userIdx,beforeMonth);

        // 2-3-3. 목표 시간 계산
        int userMonthGoalTime = countDay * userWalkGoalTime;

        // 3. 이번달 목표 달성률 계산
        int monthGoalRate = (int)((userMonthWalkTime/ (double)( userMonthGoalTime*60 )) * 100);

        return monthGoalRate;
    }

}
