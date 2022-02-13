package com.umc.footprint.src.walks.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostWalkReq {
    private SaveWalk walk;
    private List<SaveFootprint> footprintList;
    private List<MultipartFile> photos;

    @Builder
    public PostWalkReq(SaveWalk walk, List<SaveFootprint> footprintList, List<MultipartFile> photos) {
        this.walk = walk;
        this.footprintList = footprintList;
        this.photos = photos;
    }

    public void setWalkStrCoordinate(SaveWalk newCoordinateWalk) {
        this.walk = newCoordinateWalk;
    }

    public void setConvertedFootprints(ArrayList<SaveFootprint> convertedFootprints) {
        this.footprintList = convertedFootprints;
    }

    public void setRemovedPathImgPhotos(List<MultipartFile> removedPathImgPhotos) {
        this.photos = photos;
    }
}