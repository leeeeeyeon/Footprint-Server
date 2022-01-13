package com.umc.footprint.src.walks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalkProvider {
    private final WalkDao walkDao;

    @Autowired
    public WalkProvider(WalkDao walkDao) {
        this.walkDao = walkDao;
    }
}
