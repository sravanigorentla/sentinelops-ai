package com.sentinelops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.IncidentStatus;
import com.sentinelops.dto.*;
import com.sentinelops.service.AIServiceClient;
import com.sentinelops.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IncidentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IncidentService incidentService;

    @Mock
    private AIServiceClient aiServiceClient;

    @InjectMocks
    private IncidentController incidentController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(incidentController).build();
    }

    @Test
    void filterIncidents_Success() throws Exception {
        IncidentDto dto = new IncidentDto();
        dto.setId(1L);
        dto.setTitle("Database latency spike");
        dto.setSeverity(IncidentSeverity.HIGH);
        dto.setStatus(IncidentStatus.OPEN);

        PageImpl<IncidentDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 20), 1);
        when(incidentService.filterIncidents(any(), any(), any(), eq(0), eq(20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/incidents")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Database latency spike"));
    }

    @Test
    void getIncidentById_Success() throws Exception {
        IncidentDto dto = new IncidentDto();
        dto.setId(1L);
        dto.setTitle("Service Unavailable");
        when(incidentService.getIncidentById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Service Unavailable"));
    }

    @Test
    void getComments_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setEmail("dev@sentinelops.dev");
        userDto.setFullName("Dev User");

        IncidentCommentDto commentDto = new IncidentCommentDto();
        commentDto.setId(10L);
        commentDto.setComment("Investigating connection pool leak");
        commentDto.setUser(userDto);
        commentDto.setCreatedAt(ZonedDateTime.now());

        when(incidentService.getCommentsForIncident(1L)).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/api/v1/incidents/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].comment").value("Investigating connection pool leak"))
                .andExpect(jsonPath("$[0].user.fullName").value("Dev User"));
    }
}
