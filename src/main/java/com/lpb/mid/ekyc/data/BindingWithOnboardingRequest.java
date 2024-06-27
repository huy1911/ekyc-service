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
public class BindingWithOnboardingRequest {
    @JsonProperty("refNo")
    private String refNo;
    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("ekycDeviceId")
    private String ekycDeviceId;
    @JsonProperty("ekycTrustLvl")
    private String ekycTrustLvl;
    @JsonProperty("ekycSessionId")
    private String ekycSessionId;
    @JsonProperty("ekycBiometricId")
    private String ekycBiometricId;
    @JsonProperty("cif")
    private String cif;
    @JsonProperty("ekycType")
    private String ekycType;
    @JsonProperty("customerInfo")
    private CustomerInfo customerInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerInfo{
        @JsonProperty("dateOfBirth")
        private String dateOfBirth;
        @JsonProperty("gender")
        private String gender;
        @JsonProperty("idCode")
        private String idCode;
        @JsonProperty("idIssueDate")
        private String idIssueDate;
        @JsonProperty("idIssuePlace")
        private String idIssuePlace;
        @JsonProperty("custName")
        private String custName;
        @JsonProperty("fullAddress")
        private String fullAddress;
        @JsonProperty("idType")
        private String idType;
    }

}
