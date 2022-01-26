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
        String getPhotoQuery = "select imageUrl from Photo where footprintIdx=?";
        List<String> photoList = jdbcTemplate.queryForList(getPhotoQuery, String.class, footprintIdx);

        return photoList;
    }

    // 해당 인덱스의 산책 기록이 존재하는지 조회 - validation에 사용
    public int walkExist(int walkIdx) {
        String walkExistQuery = "select count(*) from Walk where walkIdx=?";
        return this.jdbcTemplate.queryForObject(walkExistQuery, int.class, walkIdx);
    }

    // 산책 기록 내 전체 발자국 조회
    public List<GetFootprintRes> getFootprints(int walkIdx) {
        String getFootprintsQuery = "select footprintIdx, `write`, recordAt, walkIdx\n" +
                "from Footprint where walkIdx=?";
        return this.jdbcTemplate.query(getFootprintsQuery,
                (rs, rowNum) -> new GetFootprintRes(
                        rs.getInt("footprintIdx"),
                        rs.getTimestamp("recordAt"),
                        rs.getString("write"),
                        getPhotoList(rs.getInt("footprintIdx"))
                )
                , walkIdx
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
        String deleteFootprintQuery = "update Footprint set status=? where footprintIdx=?";
        Object[] deleteFootprintParams = new Object[]{"INACTIVE", footprintIdx};

        return this.jdbcTemplate.update(deleteFootprintQuery, deleteFootprintParams);
        // 대응시켜 매핑시켜 쿼리 요청(성공했으면 1, 실패했으면 0)
    }
}
