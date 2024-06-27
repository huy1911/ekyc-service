package com.lpb.mid.ekyc.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyEKYCTransactionRequest {
    @NotEmpty(message = "refNo không được để trống ")
    @JsonProperty("refNo")
    private String refNo;

    @NotEmpty(message = "requestId không được để trống ")
    @JsonProperty("requestId")
    private String requestId;

    @NotEmpty(message = "extTransactionId không được để trống ")
    @JsonProperty("extTransactionId")
    private String extTransactionId;

    @NotEmpty(message = "bioId không được để trống ")
    @JsonProperty("bioId")
    private String bioId;

    @NotEmpty(message = "mac không được để trống ")
    @JsonProperty("mac")
    private String mac;

    public VerifyEKYCTransactionRequest(VerifyEKYCTransactionRequest request) {
        this.refNo = request.getRefNo();
        this.requestId = request.requestId;
        this.extTransactionId = request.getExtTransactionId();
        this.bioId = request.getBioId();
    }
}
