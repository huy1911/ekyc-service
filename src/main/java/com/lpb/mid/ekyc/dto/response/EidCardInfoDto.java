package com.lpb.mid.ekyc.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EidCardInfoDto {
    private String idNumber;
    @JsonProperty("oldIdNumber")
    private String oldIdentify;
    private String fullName;
    private String dob;
    private String gender;
    @JsonProperty("issueDate")
    private String idIssueDate;
    private String emplCode;
    private String logging;
    @JsonProperty("eidImage")
    private String image;
    private String isVerify;
    private String nationality;
    @JsonProperty("ethnicity")
    private String nation;
    private String religion;
    private String homeTown;
    private String identifying;
    private String expiredDate;
    private String fatherName;
    private String motherName;
    private String husbandOrWifeName;
    private String placeOfResidence;

    public EidCardInfoDto(String idNumber, String oldIdentify, String fullName, String dob, String gender, String idIssueDate, String emplCode, String logging, String image, String isVerify, String nationality, String nation, String religion, String homeTown, String identifying, String expiredDate, String fatherName, String motherName, String husbandOrWifeName, String placeOfResidence) {
        this.idNumber = idNumber;
        this.oldIdentify = oldIdentify;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.idIssueDate = idIssueDate;
        this.emplCode = emplCode;
        this.logging = logging;
        this.image = image;
        this.isVerify = isVerify;
        this.nationality = nationality;
        this.nation = nation;
        this.religion = religion;
        this.homeTown = homeTown;
        this.identifying = identifying;
        this.expiredDate = expiredDate;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.husbandOrWifeName = husbandOrWifeName;
        this.placeOfResidence = placeOfResidence;
    }

    private String mac;
}
