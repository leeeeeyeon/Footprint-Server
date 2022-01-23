package com.umc.footprint.src.walks.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostWalkReq {
    private Walk walk;
    private List<Footprint> footprintList;
}
