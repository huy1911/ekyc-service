package com.lpb.mid.ekyc.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lpb.mid.ekyc.data.EkycResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OCRPlusWithAdvanceDataInfoResponse {
    @JsonProperty("refNo")
    private String refNo;
    @JsonProperty("result")
    private EkycResult result;
    @JsonProperty("data")
    private data data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class citizenInfo{
        private String birthDate;
        private String citizenPid;
        private String dateProvide;
        private String ethnic;
        private String fatherName;
        private String fullName;
        private String gender;
        private String homeTown;
        private String identifyCharacteristics;
        private String motherName;
        private String nationality;
        private String oldIdentify;
        private String outOfDate;
        private String regPlaceAddress;
        private String religion;
        private String docSigningCertificate;
        private String unkidNumber;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class data{
        private String nfcSessionId;
        private citizenInfo citizenInfo;

    }
}
