package com.umc.footprint.src.walks;

import com.umc.footprint.src.walks.model.DeleteWalkRes;
import com.umc.footprint.src.walks.model.GetWalkInfo;
import com.umc.footprint.src.walks.model.GetWalkTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class WalkDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public GetWalkInfo getWalkInfo(int walkIdx) {
        String getTimeQuery = "select date_format(date(startAt), '%Y.%m.%d') as date, \n" +
                "       date_format(time(startAt),'%H:%i') as startAt,\n" +
                "       date_format(time(endAt),'%H:%i') as endAt, \n" +
                "       (timestampdiff(second, startAt, endAt)) as timeString from walk where walkIdx=?;";
        GetWalkTime getWalkTime = this.jdbcTemplate.queryForObject(getTimeQuery,
                (rs, rowNum) -> new GetWalkTime(
                        rs.getString("date"),
                        rs.getString("startAt"),
                        rs.getString("endAt"),
                        rs.getString("timeString")
                ),walkIdx);

        getWalkTime.convTimeString();

        String getFootCountQuery = "select count(footprintIdx) as footCount from footprint where walkIdx=? and status='ACTIVE';";
        Integer footCount = this.jdbcTemplate.queryForObject(getFootCountQuery,
                (rs, rowNum) -> rs.getInt("footCount"), walkIdx);


        String getWalkInfoQuery = "select walkIdx, calorie, distance, pathImageUrl from walk where walkIdx=?;";
        GetWalkInfo getWalkInfo = this.jdbcTemplate.queryForObject(getWalkInfoQuery,
                (rs,rowNum) -> new GetWalkInfo(
                        rs.getInt("walkIdx"),
                        getWalkTime,
                        rs.getInt("calorie"),
                        rs.getDouble("distance"),
                        footCount,
                        rs.getString("pathImageUrl")), walkIdx);

        return getWalkInfo;
    }

    public String deleteWalk(int walkIdx) {
        String deleteWalkQuery = "update footprint set status='INACTIVE' where walkIdx=? and status='ACTIVE';"; // 실행될 동적 쿼리문
        this.jdbcTemplate.update(deleteWalkQuery, walkIdx);
        //String checkDeleteQuery = "select count(footprintIdx) as footCount from footprint where walkIdx=? and status='ACTIVE';"; // 전체 삭제 확인
        //Integer footCount = this.jdbcTemplate.queryForObject(checkDeleteQuery,
        //        (rs, rowNum) -> rs.getInt("footCount"), walkIdx);

        return "Success Delete walk record!";
    }
}
