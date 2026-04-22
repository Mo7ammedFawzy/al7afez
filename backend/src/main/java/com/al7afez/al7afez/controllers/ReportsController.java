package com.al7afez.al7afez.controllers;

import com.al7afez.al7afez.dto.ReportsOverviewResponse;
import com.al7afez.al7afez.service.ReportsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {
    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/overview")
    public ReportsOverviewResponse getOverview() {
        return reportsService.getOverview();
    }
}
