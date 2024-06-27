package com.lpb.mid.ekyc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EidInfoRequest {
    @NotBlank(message = "idNumber không được để trống ")
    private String idNumber;

    private String oldIdNumber;

    @NotBlank(message = "fullName không được để trống ")
    private String fullName;

    @NotBlank(message = "dob không được để trống ")
    private String dob;

    @NotBlank(message = "gender không được để trống ")
    private String gender;

    @NotBlank(message = "issueDate không được để trống ")
    private String issueDate;

    private String emplCode;

    private String logging;

    private String eidImage;

    private String isVerify;

    @NotBlank(message = "nationality không được để trống ")
    private String nationality;

    @NotBlank(message = "ethnicity không được để trống ")
    @JsonProperty("ethnicity")
    private String nation;

    @NotBlank(message = "religion không được để trống ")
    private String religion;

    @NotBlank(message = "homeTown không được để trống ")
    private String homeTown;

    @NotBlank(message = "identifying không được để trống ")
    private String identifying;

    @NotBlank(message = "expiredDate không được để trống ")
    private String expiredDate;

    private String fatherName;

    private String motherName;

    private String husbandOrWifeName;

    @NotBlank(message = "placeOfResidence không được để trống ")
    private String placeOfResidence;

    private String documentSigningCertificate;

    @NotBlank(message = "mac không được để trống ")
    private String mac;
}
