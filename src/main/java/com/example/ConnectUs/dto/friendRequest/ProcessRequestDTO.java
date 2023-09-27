package com.example.ConnectUs.dto.friendRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ProcessRequestDTO {
    private Integer requestId;
    private boolean accepted;
}
