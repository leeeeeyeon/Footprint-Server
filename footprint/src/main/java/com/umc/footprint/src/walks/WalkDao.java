package com.umc.footprint.src.walks;

import com.umc.footprint.src.walks.model.Footprint;
import com.umc.footprint.src.walks.model.GetBadgeIdx;
import com.umc.footprint.src.walks.model.Walk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
  
    //Walk 테이블에 insert
    public int addWalk(Walk walk, String pathImgUrl) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String walkInsertQuery = "insert into Walk(startAt, endAt, distance, coordinate, pathImageUrl, userIdx, goalRate, calorie) " +
                "values (?,?,?,ST_GeomFromText(?),?,?,?,?)";


        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement preparedStatement = con.prepareStatement(walkInsertQuery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setTimestamp(1, Timestamp.valueOf(walk.getStartAt()));
                preparedStatement.setTimestamp(2, Timestamp.valueOf(walk.getEndAt()));
                preparedStatement.setDouble(3, walk.getDistance());
                // coordinate 형식에 따라 string으로 받을 지 multilinestringdm로 받을 지 정함
                preparedStatement.setString(4, walk.getCoordinate());
                preparedStatement.setString(5, pathImgUrl);
                preparedStatement.setInt(6, walk.getUserIdx());
                preparedStatement.setFloat(7, walk.getGoalRate());
                preparedStatement.setInt(8, walk.getCalorie());

                return preparedStatement;
            }
        }, keyHolder);

        // 생성된 id값 int형으로 변환해서 반환
        return keyHolder.getKey().intValue();
    }

    public void addFootprint(List<Footprint> footprintList, int walkIdx) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String footprintInsertQuery = "insert into `Footprint`(`coordinate`, `write`, `recordAt`, `walkIdx`)" +
                "values (ST_GeomFromText(?),?,?,?)";

        for (Footprint f : footprintList){
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement preparedStatement = con.prepareStatement(footprintInsertQuery, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, f.getStr_coordinate());
                    preparedStatement.setString(2, f.getWrite());
                    preparedStatement.setTimestamp(3, Timestamp.valueOf(f.getRecordAt()));
                    preparedStatement.setInt(4, walkIdx);
                    return preparedStatement;
                }
            }, keyHolder);
            // 자동 생성되는 인덱스 리스트에 추가
            f.setFootprintIdx(keyHolder.getKey().intValue());
        }
    }

        //Photo 테이블에 insert
    public void addPhoto(int userIdx, List<Footprint> footprintList) {
        String photoInsertQuery = "insert into `Photo`(`imageUrl`, `userIdx`, `footprintIdx`) values (?,?,?)";

        for (Footprint footprint : footprintList) {
            this.jdbcTemplate.batchUpdate(photoInsertQuery,
                    new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, footprint.getImgUrlList().get(i));
                        ps.setInt(2, userIdx);
                        ps.setInt(3, footprint.getFootprintIdx());
                    }

                    @Override
                    public int getBatchSize() {
                        return footprint.getImgUrlList().size();
                    }
            });
        }

    }

    public List<Pair<Integer, Integer>> addHashtag(List<Footprint> footprintList) {
        String hashtagInsertQuery = "insert into Hashtag(hashtag) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        //Pair<hashtagIdx, footprintIdx> mapping (tag) idx list
        List<Pair<Integer, Integer>> tagIdxList = new ArrayList<Pair<Integer, Integer>>();

        // footprint당 hashtag list 삽입
        for (Footprint f : footprintList) {
            for (String hashtag : f.getHashtagList()) {
                this.jdbcTemplate.update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement preparedStatement = con.prepareStatement(hashtagInsertQuery, Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setString(1, hashtag);
                        return preparedStatement;
                    }
                }, keyHolder);
            }
            // tag list에 삽입
            tagIdxList.add(Pair.of(keyHolder.getKey().intValue(), f.getFootprintIdx()));
        }

        return tagIdxList;
    }

    public void addTag(List<Pair<Integer, Integer>> tagIdxList, int userIdx) {
        String tagInsertQuery = "insert into Tag(hashtagIdx, footprintIdx, userIdx) values (?,?,?)";

        this.jdbcTemplate.batchUpdate(tagInsertQuery,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, tagIdxList.get(i).getFirst());
                        ps.setInt(2, tagIdxList.get(i).getSecond());
                        ps.setInt(3, userIdx);
                    }

                    @Override
                    public int getBatchSize() {
                        return tagIdxList.size();
                    }
                });

    }

    // 획득한 뱃지 매핑 테이블에 삽입
    public void addUserBadge(List<Integer> acquiredBadgeIdxList, int userIdx) {
        String userBadgeInsertQuery = "insert into UserBadge(userIdx, badgeIdx) values (?,?)";

        this.jdbcTemplate.batchUpdate(userBadgeInsertQuery,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, userIdx);
                        ps.setInt(2, acquiredBadgeIdxList.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return acquiredBadgeIdxList.size();
                    }
                });
    }

    // 유저의 목표 시간 반환
    public int getWalkGoalTime(int userIdx) {
        String getTimeQuery = "select walkGoalTime from Goal where userIdx = ?";
        int getTimeParam = userIdx;
        return this.jdbcTemplate.queryForObject(getTimeQuery, int.class, getTimeParam);
    }

    public GetBadgeIdx getAcquiredBadgeIdxList(int userIdx) {
        // 거리, 기록 관련 쿼리
        String getDisRecBadgeQuery = "SELECT \n" +
                "       CASE\n" +
                "            WHEN (sum(walk.distance) between 10 and 30) then 2\n" +
                "            when (sum(walk.distance) between 30 and 50) then 3\n" +
                "            WHEN (sum(walk.distance) between 50 and 100) then 4\n" +
                "            WHEN (sum(walk.distance) > 100) then 5\n" +
                "        else 0\n" +
                "        end as distanceBadgeIdx,\n" +
                "       CASE\n" +
                "            when (count(walk.walkIdx) between 10 and 30) then 6\n" +
                "            when (count(walk.walkIdx) between 30 and 50) then 7\n" +
                "            when (count(walk.walkIdx) > 50) then 8\n" +
                "        else 0\n" +
                "        end as recordBadgeIdx\n" +
                "From walk\n" +
                "Where userIdx = ?\n" +
                "group by walk.userIdx";

        return this.jdbcTemplate.queryForObject(getDisRecBadgeQuery,
                (rs, rowNum) -> new GetBadgeIdx(
                        rs.getInt("distanceBadgeIdx"),
                        rs.getInt("recordBadgeIdx")
                ), userIdx);
    }

    // 원래 가지고 있던 뱃지 조회
    public List<Integer> getOriginBadgeIdxList(int userIdx) {
        String getBadgeIdxListQuery = "select badgeIdx from UserBadge where userIdx = ?";

        return this.jdbcTemplate.queryForList(getBadgeIdxListQuery, int.class, userIdx);
    }

}
