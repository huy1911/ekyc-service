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
public class ValidateVerifyEKYCTransactionRequest {
    @JsonProperty("refNo")
    private String refNo;
    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("extTransactionId")
    private String extTransactionId;
    @JsonProperty("bioId")
    private String bioId;


}
