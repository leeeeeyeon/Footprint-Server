package com.umc.footprint.src.walks;

import com.umc.footprint.src.walks.model.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Slf4j
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
                "       (timestampdiff(second, startAt, endAt)) as timeString from Walk where walkIdx=? and status='ACTIVE';";
        GetWalkTime getWalkTime = this.jdbcTemplate.queryForObject(getTimeQuery,
                (rs, rowNum) -> new GetWalkTime(
                        rs.getString("date"),
                        rs.getString("startAt"),
                        rs.getString("endAt"),
                        rs.getString("timeString")
                ),walkIdx);

        getWalkTime.convTimeString();

        String getFootCountQuery = "select count(footprintIdx) as footCount from Footprint where walkIdx=? and status='ACTIVE';";
        Integer footCount = this.jdbcTemplate.queryForObject(getFootCountQuery,
                (rs, rowNum) -> rs.getInt("footCount"), walkIdx);


        String getWalkInfoQuery = "select walkIdx, calorie, distance, pathImageUrl from Walk where walkIdx=? and status='ACTIVE';";
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


        String deleteFootprintQuery = "update Footprint set status='INACTIVE' where walkIdx=? and status='ACTIVE';"; // 발자국 INACTIVE
        this.jdbcTemplate.update(deleteFootprintQuery, walkIdx);

        String deleteWalkQuery = "update Walk set status='INACTIVE' where walkIdx=? and status='ACTIVE';"; // 산책 INACTIVE
        this.jdbcTemplate.update(deleteWalkQuery, walkIdx);

        return "Success Delete walk record!";
    }

    //Walk 테이블에 insert
    public int addWalk(SaveWalk walk, String pathImgUrl) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String walkInsertQuery = "insert into Walk(startAt, endAt, distance, coordinate, pathImageUrl, userIdx, goalRate, calorie) " +
                "values (?,?,?,?,?,?,?,?)";

        log.debug("walk startAt: {}", walk.getStartAt());
        log.debug("walk endAt: {}", walk.getEndAt());
        log.debug("walk distance: {}", walk.getDistance());
        log.debug("walk userIdx: {}", walk.getUserIdx());
        log.debug("walk strCoordinate: {}", walk.getStrCoordinates());
        log.debug("walk pathImgUrl: {}", pathImgUrl);
        log.debug("walk goalRate: {}", walk.getGoalRate());
        log.debug("walk calorie: {}", walk.getCalorie());
        log.debug("walk photoMatchNumList: {}", walk.getPhotoMatchNumList());

        TimeZone default_time_zone = TimeZone.getTimeZone(ZoneId.of("Asia/Seoul"));

        TimeZone.setDefault(default_time_zone);
        Timestamp timestampStartAt = Timestamp.valueOf(walk.getStartAt());
        Timestamp timestampEndAt = Timestamp.valueOf(walk.getEndAt());

        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement preparedStatement = con.prepareStatement(walkInsertQuery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setTimestamp(1, timestampStartAt);
                preparedStatement.setTimestamp(2, timestampEndAt);
                preparedStatement.setDouble(3, walk.getDistance());
                preparedStatement.setString(4, walk.getStrCoordinates());
                preparedStatement.setString(5, pathImgUrl);
                preparedStatement.setInt(6, walk.getUserIdx());
                preparedStatement.setDouble(7, walk.getGoalRate());
                preparedStatement.setInt(8, walk.getCalorie());

                return preparedStatement;
            }
        }, keyHolder);

        // 생성된 id값 int형으로 변환해서 반환
        return keyHolder.getKey().intValue();
    }

    public void addFootprint(List<SaveFootprint> footprintList, int walkIdx) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String footprintInsertQuery = "insert into `Footprint`(`coordinate`, `write`, `recordAt`, `walkIdx`, `updateAt`, `onWalk`)" +
                "values (?,?,?,?,?,?)";

        for (SaveFootprint footprint : footprintList){
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement preparedStatement = con.prepareStatement(footprintInsertQuery, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, footprint.getStrCoordinate());
                    preparedStatement.setString(2, footprint.getWrite());
                    preparedStatement.setTimestamp(3, Timestamp.valueOf(footprint.getRecordAt()));
                    preparedStatement.setInt(4, walkIdx);
                    preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    preparedStatement.setInt(6, footprint.getOnWalk());
                    return preparedStatement;
                }
            }, keyHolder);
            // 자동 생성되는 인덱스 리스트에 추가
            footprint.setFootprintIdx(keyHolder.getKey().intValue());

            log.debug("발자국 인덱스: {}", footprint.getFootprintIdx());
            log.debug("발자국 좌표(String): {}", footprint.getStrCoordinate());
            log.debug("발자국 내용: {}", footprint.getWrite());
            log.debug("발자국 기록 시간: {}", footprint.getRecordAt());
            log.debug("발자국 walkIdx: {}", walkIdx);
            log.debug("발자국 onWalk: {}", footprint.getOnWalk());
        }
    }

        //Photo 테이블에 insert
    public void addPhoto(int userIdx, List<SaveFootprint> footprintList) {
        String photoInsertQuery = "insert into `Photo`(`imageUrl`, `userIdx`, `footprintIdx`) values (?,?,?)";

        for (SaveFootprint footprint : footprintList) {
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

    public List<Pair<Integer, Integer>> addHashtag(List<SaveFootprint> footprintList) {
        String hashtagInsertQuery = "insert into Hashtag(hashtag) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        //Pair<hashtagIdx, footprintIdx> mapping (tag) idx list
        List<Pair<Integer, Integer>> tagIdxList = new ArrayList<>();

        // footprint당 hashtag list 삽입
        for (SaveFootprint footprint : footprintList) {
            if (footprint.getHashtagList().size() != 0){
                for (String hashtag : footprint.getHashtagList()) {
                    this.jdbcTemplate.update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            PreparedStatement preparedStatement = con.prepareStatement(hashtagInsertQuery, Statement.RETURN_GENERATED_KEYS);
                            preparedStatement.setString(1, hashtag);
                            return preparedStatement;
                        }
                    }, keyHolder);
                    // tag list에 삽입
                    tagIdxList.add(Pair.of(keyHolder.getKey().intValue(), footprint.getFootprintIdx()));
                }
            }
            log.debug("발자국 해시태그들: {}" + footprint.getHashtagList());
        }

        log.debug("tag 인덱스들: {}", tagIdxList);
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
    public Long getWalkGoalTime(int userIdx) {
        log.debug("userIdx: {}", userIdx);
        String getTimeQuery = "select walkGoalTime from Goal where userIdx = ? and MONTH(createAt) = MONTH(NOW())";
        int getTimeParam = userIdx;
        return this.jdbcTemplate.queryForObject(getTimeQuery, Long.class, getTimeParam);
    }

    public GetBadgeIdx getAcquiredBadgeIdxList(int userIdx) {
        // 거리, 기록 관련 쿼리

        String getDisRecBadgeQuery = "SELECT \n" +
                "       CASE\n" +
                "            WHEN (sum(Walk.distance) between 10 and 30) then 2\n" +
                "            when (sum(Walk.distance) between 30 and 50) then 3\n" +
                "            WHEN (sum(Walk.distance) between 50 and 100) then 4\n" +
                "            WHEN (sum(Walk.distance) > 100) then 5\n" +
                "        else 0\n" +
                "        end as distanceBadgeIdx,\n" +
                "       CASE\n" +
                "            when (count(Walk.walkIdx) = 1) then 1" +
                "            when (count(Walk.walkIdx) between 10 and 19) then 6\n" +
                "            when (count(Walk.walkIdx) between 20 and 29) then 7\n" +
                "            when (count(Walk.walkIdx) >= 30) then 8\n" +
                "        else 0\n" +
                "        end as recordBadgeIdx\n" +
                "From Walk\n" +
                "Where userIdx = ?\n and status = 'ACTIVE'" +
                "group by Walk.userIdx";

        GetBadgeIdx getBadgeIdx = this.jdbcTemplate.queryForObject(getDisRecBadgeQuery,
                (rs, rowNum) -> GetBadgeIdx.builder()
                        .distanceBadgeIdx(rs.getInt("distanceBadgeIdx"))
                        .recordBadgeIdx(rs.getInt("recordBadgeIdx"))
                        .build()
                , userIdx);
        log.debug("가지고 있던 기록 관련 뱃지중 가장 큰 인덱스: {}", getBadgeIdx.getRecordBadgeIdx());
        log.debug("가지고 있던 거리 관련 뱃지중 가장 큰 인덱스: {}", getBadgeIdx.getDistanceBadgeIdx());
        return getBadgeIdx;
    }

    // 원래 가지고 있던 뱃지 조회
    public List<Integer> getOriginBadgeIdxList(int userIdx) {
        String getBadgeIdxListQuery = "select badgeIdx from UserBadge where userIdx = ?";

        return this.jdbcTemplate.queryForList(getBadgeIdxListQuery, int.class, userIdx);
    }

    // 뱃지 정보 조회
    public List<PostWalkRes> getBadgeInfo(List<Integer> badgeIdxList) {
        String getBadgeInfoQuery = "select badgeIdx, badgeName, badgeUrl from Badge where badgeIdx = ?";
        List<PostWalkRes> postWalkResList = new ArrayList<PostWalkRes>();
        for (Integer badgeIdx : badgeIdxList) {
            postWalkResList.add(this.jdbcTemplate.queryForObject(getBadgeInfoQuery,
                            (rs, rowNum) -> PostWalkRes.builder()
                                    .badgeIdx(rs.getInt("badgeIdx"))
                                    .badgeName(rs.getString("badgeName"))
                                    .badgeUrl(rs.getString("badgeUrl"))
                                    .build()
                    , badgeIdx));
        }

        return postWalkResList;
    }

    // 유저의 산책 회수 반환
    public int checkFirstWalk(int userIdx) {
        String checkFirstWalkQuery = "select count(walkIdx) from Walk where userIdx = ?";
        return this.jdbcTemplate.queryForObject(checkFirstWalkQuery, int.class, userIdx);
    }

    public int getWalkWholeIdx(int walkIdx, int userIdx) {
        String getWalkWholeIdxQuery = "select walkIdx from Walk where userIdx = ? and status = 'ACTIVE' ORDER BY startAt ASC LIMIT ?,1";
        return this.jdbcTemplate.queryForObject(getWalkWholeIdxQuery, int.class, userIdx, walkIdx-1);
    }

    public int checkWalkVal(int walkIdx) {
        log.debug("WalkDao.checkWalkVal");
        String checkWalkValQuery = "select EXISTS (select walkIdx from Walk where walkIdx=? and status='ACTIVE') as success;";
        return this.jdbcTemplate.queryForObject(checkWalkValQuery, int.class, walkIdx);
    }

    public List<Integer> getFootprintIdxList(int walkIdx) {
        String getFootprintQuery = "select footprintIdx from Footprint where walkIdx=?;";
        List<Integer> footprintIdxList = jdbcTemplate.queryForList(getFootprintQuery, int.class, walkIdx);
        return footprintIdxList;
    }

    // 해당 발자국의 사진 inactive
    public void inactivePhoto(int footprintIdx) {
        String inactivePhotoQuery = "update Photo set status='INACTIVE' where footprintIdx=? and status='ACTIVE';"; // 사진 INACTIVE
        this.jdbcTemplate.update(inactivePhotoQuery, footprintIdx);
    }

    // 해당 발자국의 태그 inactive
    public void inactiveTag(int footprintIdx) {
        String inactiveTagQuery = "update Tag set status='INACTIVE' where footprintIdx=? and status='ACTIVE';"; // 태그 INACTIVE
        this.jdbcTemplate.update(inactiveTagQuery, footprintIdx);
    }
}
