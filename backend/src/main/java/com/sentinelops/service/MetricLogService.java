package com.sentinelops.service;

import com.sentinelops.domain.entity.LogEntity;
import com.sentinelops.domain.entity.MetricEntity;
import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.LogLevel;
import com.sentinelops.dto.LogDto;
import com.sentinelops.dto.MetricDto;
import com.sentinelops.exception.ResourceNotFoundException;
import com.sentinelops.repository.LogRepository;
import com.sentinelops.repository.MetricRepository;
import com.sentinelops.repository.ServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class MetricLogService {

    private final MetricRepository metricRepository;
    private final LogRepository logRepository;
    private final ServiceRepository serviceRepository;

    public MetricLogService(MetricRepository metricRepository,
                            LogRepository logRepository,
                            ServiceRepository serviceRepository) {
        this.metricRepository = metricRepository;
        this.logRepository = logRepository;
        this.serviceRepository = serviceRepository;
    }

    public MetricDto ingestMetric(Long serviceId, Double cpuUsage, Double memoryUsage, Double requestRate, Double latencyMs, Double errorRate) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));

        MetricEntity metric = new MetricEntity();
        metric.setService(service);
        metric.setCpuUsage(cpuUsage);
        metric.setMemoryUsage(memoryUsage);
        metric.setRequestRate(requestRate);
        metric.setLatencyMs(latencyMs);
        metric.setErrorRate(errorRate);
        metric.setTimestamp(ZonedDateTime.now());

        MetricEntity saved = metricRepository.save(metric);
        return new MetricDto(saved);
    }

    public LogDto ingestLog(Long serviceId, String environment, LogLevel level, String message, String requestId, String metadata) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));

        LogEntity log = new LogEntity();
        log.setService(service);
        log.setEnvironment(environment != null ? environment : service.getEnvironment());
        log.setLogLevel(level);
        log.setMessage(message);
        log.setRequestId(requestId);
        log.setMetadata(metadata);
        log.setTimestamp(ZonedDateTime.now());

        LogEntity saved = logRepository.save(log);
        return new LogDto(saved);
    }

    public List<MetricDto> getMetricsForService(Long serviceId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("timestamp").descending());
        return metricRepository.findByServiceIdOrderByTimestampDesc(serviceId, pageable)
                .stream().map(MetricDto::new).toList();
    }

    public Page<LogDto> searchLogs(Long serviceId, LogLevel level, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return logRepository.searchLogs(serviceId, level, search, pageable).map(LogDto::new);
    }

    public List<LogDto> getRecentLogsForService(Long serviceId, int windowMinutes) {
        ZonedDateTime since = ZonedDateTime.now().minusMinutes(windowMinutes);
        return logRepository.findRecentLogsForService(serviceId, since).stream().map(LogDto::new).toList();
    }
}
