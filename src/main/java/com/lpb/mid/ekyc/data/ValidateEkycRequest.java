package com.lpb.mid.ekyc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateEkycRequest {
    @JsonProperty("refNo")
    private String refNo;
    @JsonProperty("ekycPartner")
    private String ekycPartner;
    @JsonProperty("ekycDeviceId")
    private String ekycDeviceId;
    @JsonProperty("ekycTrustLvl")
    private String ekycTrustLvl;
    @JsonProperty("ekycBiometricId")
    private String ekycBiometricId;
    @JsonProperty("ekycSessionId")
    private String ekycSessionId;
    @JsonProperty("idTypNo")
    private String idTypNo;
    @JsonProperty("ekycType")
    private String ekycType;
}
