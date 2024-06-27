package com.lpb.mid.ekyc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfoRequest {
    private String ekycBiometricId;
    private String ekycDeviceId;
    private String ekycSessionId;
    private String ekycType;
    private String nfcSession;
    private String nfcType;
    private String refNo;
}
