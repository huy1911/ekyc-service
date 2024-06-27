package com.lpb.mid.ekyc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateNFCInfoRequest {
    @JsonProperty("refNo")
    private String refNo;
    @JsonProperty("ekycPartner")
    private String ekycPartner;
    @JsonProperty("ekycDeviceId")
    private String ekycDeviceId;
    @JsonProperty("ekycBiometricId")
    private String ekycBiometricId;
    @JsonProperty("ekycSessionId")
    private String ekycSessionId;
    @JsonProperty("ekycType")
    private String ekycType;
    @JsonProperty("nfcSession")
    private String nfcSession;
    @JsonProperty("nfcType")
    private String nfcType;
    @JsonProperty("idNumber")
    private String idNumber;
    @JsonProperty("fullName")
    private String fullName;
    @JsonProperty("birthDate")
    private String birthDate;
    @JsonProperty("idIssueDate")
    private String idIssueDate;
    @JsonProperty("idExpireDate")
    private String idExpireDate;
}
