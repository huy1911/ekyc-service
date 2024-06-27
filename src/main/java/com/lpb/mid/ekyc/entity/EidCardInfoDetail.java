package com.lpb.mid.ekyc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "EID_CARD_INFO_DETAIL")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EidCardInfoDetail{
    @Id
    @GeneratedValue(
            generator = "system-uuid"
    )
    @GenericGenerator(
            name = "system-uuid",
            strategy = "uuid"
    )
    @Column(name = "GUID")
    private String GUID;
    @Column(name = "ID_NUMBER")
    private String idNumber;
    @Column(name = "FC")
    private String fc;
    @Column(name = "FM")

    private String fm;
    @Column(name = "DG1")

    private String dg1;
    @Column(name = "DG2")

    private String dg2;
    @Column(name = "DG13")

    private String dg13;
    @Column(name = "SOD")

    private String  sod;


    @Column(name = "CREATED_BY")

    private String createBy;
    @Column(name = "CREATED_DATE")

    private Date createDate;
    @Column(name = "UDF1")

    private String UDF1;
    @Column(name = "UDF2")

    private String  UDF2;

    @Column(name = "UDF3")
    private String  UDF3;

    @Column(name = "GENDER")
    private String  gender;

    @Column(name = "EMPL_CODE")
    private String  emplCode;

    @Column(name = "LOGGING")
    private String  logging;

    @Column(name = "NATIONALITY")
    private String  nationality;

    @Column(name = "NATION")
    private String  nation;

    @Column(name = "RELIGION")
    private String  religion;

    @Column(name = "HOMETOWN")
    private String  homeTown;

    @Column(name = "IDENTIFYING")
    private String  identifying;

    @Column(name = "EXPIRED_DATE")
    private String  expiredDate;

    @Column(name = "FATHER_NAME")
    private String  fatherName;

    @Column(name = "MOTHER_NAME")
    private String  motherName;

    @Column(name = "HUSBAND_OR_WIFE_NAME")
    private String  husbandOrWifeName;

    @Column(name = "ID_ISSUE_DATE")
    private String  issueDate;

    @Column(name = "PLACE_OF_RESIDENCE")
    private String placeOfResidence;

    @Column(name = "DOCUMENT_SIGNING_CERTIFICATE")
    private String documentSigningCertificate;
}
