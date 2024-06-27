package com.lpb.mid.ekyc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailEidCardRequest {

    @NotBlank(message = "idNumber không được để trống ")
    private String idNumber;

    @NotBlank(message = "issueDate không được để trống ")
    private String issueDate;

    @NotBlank(message = "mac không được để trống ")
    private String mac;
}
