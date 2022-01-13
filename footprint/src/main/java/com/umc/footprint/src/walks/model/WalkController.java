package com.umc.footprint.src.walks.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/walks")
public class WalkController {

    @Autowired
    private final WalkProvider walkProvider;
    @Autowired
    private final WalkService walkService;

    public WalkController(WalkProvider walkProvider, WalkService walkService) {
        this.walkProvider = walkProvider;
        this.walkService = walkService;
    }
}
