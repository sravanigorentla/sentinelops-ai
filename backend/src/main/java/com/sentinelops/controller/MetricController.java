package com.sentinelops.controller;

import com.sentinelops.dto.MetricDto;
import com.sentinelops.service.MetricLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metrics")
public class MetricController {

    private final MetricLogService metricLogService;

    public MetricController(MetricLogService metricLogService) {
        this.metricLogService = metricLogService;
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<MetricDto>> getMetricsForService(@PathVariable Long serviceId,
                                                                 @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(metricLogService.getMetricsForService(serviceId, limit));
    }
}
