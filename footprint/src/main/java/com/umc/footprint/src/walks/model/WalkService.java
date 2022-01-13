package com.umc.footprint.src.walks.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class WalkService {
    private final WalkDao walkDao;
    private final WalkProvider walkProvider;

    @Autowired
    public WalkService(WalkDao walkDao, WalkProvider walkProvider) {
        this.walkDao = walkDao;
        this.walkProvider = walkProvider;
    }
}
