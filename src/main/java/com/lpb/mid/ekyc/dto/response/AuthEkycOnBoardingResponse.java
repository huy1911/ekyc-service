package com.lpb.mid.ekyc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthEkycOnBoardingResponse {
    private String refNo;
    private String mac;
}
