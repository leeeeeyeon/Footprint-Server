package com.umc.footprint.src.footprints;

import com.umc.footprint.src.footprints.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FootprintDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
        String getFootprintsQuery = "select footprintIdx, `write`, recordAt, walkIdx, onWalk\n" +
                "from Footprint where walkIdx=? and status=?";
        return this.jdbcTemplate.query(getFootprintsQuery,
                (rs, rowNum) -> new GetFootprintRes(
                        rs.getInt("footprintIdx"),
                        rs.getObject("recordAt", LocalDateTime.class),
                        rs.getString("write"),
                        getPhotoList(rs.getInt("footprintIdx")),
                        getTagList(rs.getInt("footprintIdx")),
                        rs.getInt("onWalk")
                )
                , walkIdx, "ACTIVE"
        );
    }

    // 발자국 내용 수정
    public int modifyWrite(PatchFootprintReq patchFootprintReq, int footprintIdx) {
        String modifyWriteQuery = "update Footprint set `write`=? where footprintIdx=? and status=?";
        Object[] modifyWriteParams = new Object[] { patchFootprintReq.getWrite(), footprintIdx, "ACTIVE"};

        return this.jdbcTemplate.update(modifyWriteQuery, modifyWriteParams); // 성공 - 1, 실패 - 0
    }

    // 발자국 수정 시간 업데이트
    public int updateAt(PatchFootprintReq patchFootprintReq, int footprintIdx) {
        String updateAtQuery = "update Footprint set updateAt=NOW() where footprintIdx=? and status=?";
        Object[] updateAtParams = new Object[] { footprintIdx, "ACTIVE" };

        return this.jdbcTemplate.update(updateAtQuery, updateAtParams); // 성공 - 1, 실패 - 0
    }

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

    /**
     * Tool Method
     */

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
                "where footprintIdx=? and T.status=?";
        List<String> tagList = jdbcTemplate.queryForList(getTagQuery, String.class, footprintIdx, "ACTIVE");

        return tagList;
    }

    // 기존 사진들 삭제 (INACTIVE 처리)
    public void deletePhotos(int footprintIdx) {
        String deletePhotosQuery = "update Photo set status=? where footprintIdx=? and status=?";
        Object[] deletePhotosParams = new Object[]{"INACTIVE", footprintIdx, "ACTIVE"};
        this.jdbcTemplate.update(deletePhotosQuery, deletePhotosParams);
    }

    // 기존 태그들 삭제 (INACTIVE 처리)
    public void deleteHashtags(int footprintIdx) {
        String deleteHashtagsQuery = "update Tag set status=? where footprintIdx=? and status=?";
        Object[] deleteHashtagsParams = new Object[] {"INACTIVE", footprintIdx, "ACTIVE"};
        this.jdbcTemplate.update(deleteHashtagsQuery, deleteHashtagsParams);
    }
    // 리스트에 담긴 이미지 URL들을 Photo 테이블에 저장
    public void addPhoto(List<String> photoList, int userIdx, int footprintIdx) {
        for(String imgUrl : photoList) {
            String addPhotoQuery = "insert Photo(imageUrl, userIdx, footprintIdx) values(?,?,?)";
            Object[] addPhotoParams = new Object[] {imgUrl, userIdx, footprintIdx};
            this.jdbcTemplate.update(addPhotoQuery, addPhotoParams);
        }
    }

    // 리스트에 담긴 Tag들을 Hashtag, Tag 테이블에 저장
    public void addTag(List<String> tagList, int userIdx, int footprintIdx) {
        for(String hashtag : tagList) {

            // Hashtag 테이블에 insert
            String addHashtagQuery = "insert into Hashtag (hashtag) values (?)";
            this.jdbcTemplate.update(addHashtagQuery, hashtag);

            // TODO: last_insert_id()에 대해 좀 더 찾아보자
            String lastInsertQuery = "select last_insert_id()";
            int hashtagIdx = this.jdbcTemplate.queryForObject(lastInsertQuery, int.class);

            // Tag 테이블에 insert
            String addTagQuery = "insert into Tag (hashtagIdx, footprintIdx, userIdx) values (?,?,?)";
            Object[] addTagParams = new Object[] {hashtagIdx, footprintIdx, userIdx};
            this.jdbcTemplate.update(addTagQuery, addTagParams);
        }
    }

    // footprintIdx로부터 userIdx 찾아내기
    public int findUserIdx(int footprintIdx) {
        String findUserIdxQuery = "select W.userIdx from Footprint\n" +
                "inner join Walk W on Footprint.walkIdx = W.walkIdx\n" +
                "where footprintIdx=?";
        int userIdx = jdbcTemplate.queryForObject(findUserIdxQuery, int.class, footprintIdx);
        if((Integer)userIdx == null) {

        }

        return userIdx;
    }
}
