package com.lpb.mid.ekyc.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyDsCertResponse {
    @JsonProperty("success")
    private String success;
    @JsonProperty("error")
    private error error;
    @JsonProperty("data")
    private data data;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class error{
        @JsonProperty("code")
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class data{
        @JsonProperty("transaction_code")
        private String transaction_code;
        @JsonProperty("is_valid_id_card")
        private Boolean isValidIdCard;
        @JsonProperty("detail_message")
        private String detailMessage;
        @JsonProperty("signature")
        private String signature;
        @JsonProperty("responds")
        private responds responds;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class responds{
        @JsonProperty("responseId")
        private String responseId;
        @JsonProperty("exitcode")
        private Integer exitCode;
        @JsonProperty("result")
        private Boolean result;
        @JsonProperty("message")
        private String message;
        @JsonProperty("time")
        private Integer time;
    }
}
