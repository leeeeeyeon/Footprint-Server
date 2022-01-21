package com.umc.footprint.src.users;

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

    //월별 발자국(일기) 갯수 조회
    public List<GetFootprintCount> getMonthFootprints(int userIdx, int year, int month) {
        String Query = "select day(recordAt) as day, count(footprintIdx) as footprintCount from footprint\n" +
                "    where walkIdx in (select walkIdx from walk where userIdx=? && year(startAt)=? && month(startAt)=?)\n" +
                "    group by day(recordAt);";
        Object[] getMonthResParams = new Object[]{userIdx, year, month};
        return this.jdbcTemplate.query(Query,
                (rs, rowNum) -> new GetFootprintCount(
                        rs.getInt("day"),
                        rs.getInt("footprintCount")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getMonthResParams);
    }


    //월별 달성률 및 누적 정보 조회
    public GetMonthInfoRes getMonthInfoRes(int userIdx, int year, int month) {
        // 목표 요일 조회 (boolean)
        String getGoalDaysQuery = "select G.sun, G.mon, G.tue, G.wed, G.thu, G.fri, G.sat from GoalDay as G where userIdx=?;";
        GetGoalDays getgetGoalDays = this.jdbcTemplate.queryForObject(getGoalDaysQuery,
                (rs,rowNum) -> new GetGoalDays(
                        rs.getBoolean("sun"),
                        rs.getBoolean("mon"),
                        rs.getBoolean("tue"),
                        rs.getBoolean("wed"),
                        rs.getBoolean("thu"),
                        rs.getBoolean("fri"),
                        rs.getBoolean("sat")), userIdx);

        //이번달 일별 달성률 조회(List)
        String getDayRateQuery = "select day(startAt) as day, sum(goalRate) as rate from walk where userIdx=? group by day(startAt);";
        List<GetDayRateRes> getDayRatesRes = this.jdbcTemplate.query(getDayRateQuery,
                (rs, rowNum) -> new GetDayRateRes(
                        rs.getInt("day"),
                        rs.getFloat("rate")),
                userIdx);

        //사용자의 이번 달 누적 시간, 거리, 칼로리 -> 평균 칼로리로 수정하기!!!!!!!!
        String getMonthInfoQuery = "select cast((sum(endAt-startAt))as unsigned ) as monthTotalMin,\n" +
                "    sum(distance) as monthTotalDistance, sum(calorie) as monthPerCal\n" +
                "    from walk where userIdx=? and year(startAt)=? and month(startAt)=?;";
        Object[] getMonthInfoParams = new Object[]{userIdx, year, month};
        GetMonthTotal getMonthTotal = this.jdbcTemplate.queryForObject(getMonthInfoQuery,
                (rs, rowNum) -> new GetMonthTotal(
                        rs.getInt("monthTotalMin"),
                        rs.getDouble("monthTotalDistance"),
                        rs.getInt("monthPerCal")),
                getMonthInfoParams);

        int dayCount = getDayRatesRes.toArray().length;
        getMonthTotal.avgCal(dayCount); // 산책 날짜 기준 평균 칼로리


        GetMonthInfoRes getMonthInfoRes = new GetMonthInfoRes(getgetGoalDays,getDayRatesRes,getMonthTotal);
        return getMonthInfoRes;
    }
}
