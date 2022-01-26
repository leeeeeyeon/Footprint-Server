package com.umc.footprint.src.walks;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.src.walks.model.PostWalkReq;
import com.umc.footprint.src.walks.model.PostWalkRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("")
    public BaseResponse<PostWalkRes> saveRecord(@ModelAttribute PostWalkReq request) throws BaseException {
        try {
            PostWalkRes postWalkRes = walkService.saveRecord(request);
            return new BaseResponse<>(postWalkRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
