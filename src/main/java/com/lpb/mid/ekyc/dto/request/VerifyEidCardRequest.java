package com.lpb.mid.ekyc.dto.request;

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
public class VerifyEidCardRequest {

    @NotEmpty(message = "idNumber không được để trống ")
    @JsonProperty("idNumber")
    private String idNumber;

    @JsonProperty("oldIdNumber")
    private String oldIdNumber;

    @NotEmpty(message = "IssueDate không được để trống ")
    @JsonProperty("IssueDate")
    private String IssueDate;

    @NotEmpty(message = "mac không được để trống ")
    @JsonProperty("mac")
    private String mac;

    public VerifyEidCardRequest(VerifyEidCardRequest verifyEidCardRequest) {
        this.idNumber = verifyEidCardRequest.getIdNumber();
        this.oldIdNumber = verifyEidCardRequest.getOldIdNumber();
        this.IssueDate = verifyEidCardRequest.getIssueDate();
    }
}
