package com.kaan.tracelab.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse getProjectDashboard(
            @PathVariable Long projectId
    ) {
        return dashboardService
                .getProjectDashboard(projectId);
    }
}