package com.lpb.mid.ekyc.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompareOcrNfcResponse {
    private String refNo;
    private nfcCheckValidResult nfcCheckValidResult;
    private String mac;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class nfcCheckValidResult{
        private String fc;
        private String fm;
        private citizenInfo citizenInfo;
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
        private String image;
        private String dg1;
        private String dg2;
        private String dg13;
        private String sod;
    }
}
