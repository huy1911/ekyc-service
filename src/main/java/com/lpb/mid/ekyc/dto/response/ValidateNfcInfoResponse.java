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
public class ValidateNfcInfoResponse {
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
    public static class nfcCheckValidResult{
        private String fc;
        private String fm;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class citizenInfo{
        private String birthDate;
        private String citizenPid;
        private String dateProvide;
        private String fullName;
        private String outOfDate;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class data{
        private nfcCheckValidResult nfcCheckValidResult;
        private citizenInfo citizenInfo;
        private String image;
        private String dg1;
        private String dg2;
        private String dg13;
        private String sod;
    }
}
