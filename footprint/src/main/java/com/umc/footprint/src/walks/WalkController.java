package com.umc.footprint.src.walks;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.config.BaseResponseStatus;
import com.umc.footprint.src.walks.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/walks")
public class WalkController {

    private final WalkProvider walkProvider;
    private final WalkService walkService;

    @Autowired
    public WalkController(WalkService walkService, WalkProvider walkProvider) {
        this.walkService = walkService;
        this.walkProvider = walkProvider;
    }

    /**
     *  실시간 처리 API
     *  [Post] /walks
     */
    @PostMapping("") // (POST) 127.0.0.1:3000/walks/
    public BaseResponse<List<PostWalkRes>> saveRecord(@ModelAttribute PostWalkReq request) throws BaseException {
        try {
            List<PostWalkRes> postWalkResList = walkService.saveRecord(request);
            return new BaseResponse<>(postWalkResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{walkIdx}") // (GET) 127.0.0.1:3000/walks/{walkIdx}
    public BaseResponse<GetWalkInfo> getWalkInfo(@PathVariable("walkIdx") int walkIdx) {
        try {
            GetWalkInfo getWalkInfo = walkProvider.getWalkInfo(walkIdx);
            return new BaseResponse<>(getWalkInfo);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //해당 산책의 기록(발자국) 전체 삭제
    @ResponseBody
    @PatchMapping("/{walkIdx}/status") // (Patch) 127.0.0.1:3000/walks/{walkIdx}/status
    public BaseResponse<String> deleteWalk(@PathVariable("walkIdx") int walkIdx) {
        if (walkIdx == 0) {
            return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
        }
        try {
            String result = walkService.deleteWalk(walkIdx);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
