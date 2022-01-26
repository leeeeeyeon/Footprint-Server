package com.umc.footprint.src.footprints;

import com.umc.footprint.src.footprints.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FootprintDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 발자국의 사진 리스트 - getFootprints()에서 사용
    public List<String> getPhotoList(int footprintIdx) {
        String getPhotoQuery = "select imageUrl from Photo where footprintIdx=? and status =?";
        List<String> photoList = jdbcTemplate.queryForList(getPhotoQuery, String.class, footprintIdx, "ACTIVE");

        return photoList;
    }

    // 발자국의 태그 리스트
    public List<String> getTagList(int footprintIdx) {
        String getTagQuery = "select hashtag from Tag T\n" +
                "inner join Hashtag H on T.hashtagIdx = H.hashtagIdx\n" +
                "where footprintIdx=?";
        List<String> tagList = jdbcTemplate.queryForList(getTagQuery, String.class, footprintIdx);

        return tagList;
    }

    // 해당 인덱스의 산책 기록이 존재하는지 조회 - validation에 사용
    public int walkExist(int walkIdx) {
        String walkExistQuery = "select count(*) from Walk where walkIdx=? and status=?";
        return this.jdbcTemplate.queryForObject(walkExistQuery, int.class, walkIdx, "ACTIVE");
    }

    // 해당 인덱스의 발자국이 존재하는지 조회 - validation에 사용
    public int footprintExist(int footprintIdx) {
        String footprintExistQuery = "select count(*) from Footprint where footprintIdx=?";
        return this.jdbcTemplate.queryForObject(footprintExistQuery, int.class, footprintIdx);
    }

    // 삭제된 발자국인지 조회 - validation에 사용
    public int activeFootprint(int footprintIdx) {
        String activeFootprintQuery = "select count(*) from Footprint where footprintIdx=? and status=?";
        return this.jdbcTemplate.queryForObject(activeFootprintQuery, int.class, footprintIdx, "ACTIVE");
    }

    // 산책 기록 내 전체 발자국 조회
    public List<GetFootprintRes> getFootprints(int walkIdx) {
        String getFootprintsQuery = "select footprintIdx, `write`, recordAt, walkIdx\n" +
                "from Footprint where walkIdx=? and status=?";
        return this.jdbcTemplate.query(getFootprintsQuery,
                (rs, rowNum) -> new GetFootprintRes(
                        rs.getInt("footprintIdx"),
                        rs.getTimestamp("recordAt"),
                        rs.getString("write"),
                        getPhotoList(rs.getInt("footprintIdx")),
                        getTagList(rs.getInt("footprintIdx"))
                )
                , walkIdx, "ACTIVE"
        );
    }

    // 발자국 수정 (미완)
    /*
    public int modifyFootprint(PatchFootprintReq patchFootprintReq) {
        int footprintIdx = patchFootprintReq.getFootprintIdx();
        List<String> photoList = patchFootprintReq.getPhotoList();

        String modifyPhotoQuery = "update Photo set imageUrl = ? where footprintIdx = ?";
        Object[] modifyPhotoParams = new Object[]{patchFootprintReq.getPhotoList(), patchFootprintReq.getFootprintIdx()};
        int modifyPhoto = this.jdbcTemplate.update(modifyPhotoQuery, modifyPhotoParams);

        String modifyWriteQuery = "update Footprint set `write` = ? where footprintIdx = ?";
        Object[] modifyWriteParams = new Object[]{patchFootprintReq.getPhotoList(), patchFootprintReq.getFootprintIdx()};
        int modifyWrite = this.jdbcTemplate.update(modifyWriteQuery, modifyWriteParams);

        int result = (modifyPhoto == modifyWrite) ? 1 : 0

        return result;
        // 대응시켜 매핑시켜 쿼리 요청(수정했으면 1, 실패했으면 0)
    }
     */

    // 발자국 삭제
    public int deleteFootprint(int footprintIdx){
        String deleteFootprintQuery = "update Footprint set status=? where footprintIdx=? and status=?";
        Object[] deleteFootprintParams = new Object[]{"INACTIVE", footprintIdx, "ACTIVE"};
        int deleteFootprint = this.jdbcTemplate.update(deleteFootprintQuery, deleteFootprintParams);

        String deletePhotoQuery = "update Photo set status=? where footprintIdx=? and status=?";
        Object[] deletePhotoParams = new Object[]{"INACTIVE", footprintIdx, "ACTIVE"};
        int deletePhoto = this.jdbcTemplate.update(deletePhotoQuery, deletePhotoParams);

        return ( (deleteFootprint != 0 && deletePhoto != 0) ? 1 : 0 );
        // 대응시켜 매핑시켜 쿼리 요청(성공했으면 1, 실패했으면 0)
    }
}
