package com.sentinelops.controller;

import com.sentinelops.domain.enums.LogLevel;
import com.sentinelops.dto.LogDto;
import com.sentinelops.service.MetricLogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

    private final MetricLogService metricLogService;

    public LogController(MetricLogService metricLogService) {
        this.metricLogService = metricLogService;
    }

    @GetMapping
    public ResponseEntity<Page<LogDto>> searchLogs(@RequestParam(required = false) Long serviceId,
                                                    @RequestParam(required = false) LogLevel level,
                                                    @RequestParam(required = false) String search,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(metricLogService.searchLogs(serviceId, level, search, page, size));
    }
}
