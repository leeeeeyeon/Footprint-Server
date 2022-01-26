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

    // 유저 상태 조회 - validation에 사용
    public String getStatus(int userIdx) {
        String getStatusQuery = "select status from User where userIdx=?";
        return this.jdbcTemplate.queryForObject(getStatusQuery, String.class, userIdx);
    }

    // 유저 존재 여부 조회
    public int userExist(int userIdx) {
        String userExistQuery = "select count(*) from User where userIdx=?";
        return this.jdbcTemplate.queryForObject(userExistQuery, int.class, userIdx);
    }

    // 해당 userIdx를 갖는 유저조회
    public GetUserRes getUser(int userIdx) {
        String getUserQuery = "select userIdx, nickname, `name`, email, status, User.badgeIdx, badgeUrl, age, sex, height, weight\n" +
                "from User inner join Badge B on User.badgeIdx = B.badgeIdx where userIdx=?";
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickname"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("status"),
                        rs.getInt("badgeIdx"),
                        rs.getString("badgeUrl"),
                        rs.getInt("age"),
                        rs.getString("sex"),
                        rs.getInt("height"),
                        rs.getInt("weight")
                ),
                userIdx);
    }

    // 회원정보 변경
    public int modifyNickname(PatchNicknameReq patchNicknameReq) {
        String modifyNicknameQuery = "update User set nickname = ? where userIdx = ?";
        Object[] modifyNicknameParams = new Object[]{patchNicknameReq.getNickname(), patchNicknameReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyNicknameQuery, modifyNicknameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 중복 닉네임 검사
    public int nicknameExist(PatchNicknameReq patchNicknameReq) {
        String nickname = patchNicknameReq.getNickname();
        String nicknameExistQuery = "select count(*) from User where nickname =?";

        return jdbcTemplate.queryForObject(nicknameExistQuery, int.class, nickname);
    }

}
