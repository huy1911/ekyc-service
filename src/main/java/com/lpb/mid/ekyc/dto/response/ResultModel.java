package com.lpb.mid.ekyc.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultModel {
    @JsonProperty("refNo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refNo;
    @JsonProperty("hashId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String hashId;
    @JsonProperty("mac")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mac;
    @JsonProperty("customerInfo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CustomerInfo customerInfo;
    @JsonProperty("ekycBiometricId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ekycBiometricId;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerInfo{
        @JsonProperty("type")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String type;
        @JsonProperty("isVerifyRar")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean isVerifyRar;
        @JsonProperty("cifHash")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String cifHash;
    }
}
