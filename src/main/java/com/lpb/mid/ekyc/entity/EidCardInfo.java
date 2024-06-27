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
@Table(name = "EID_CARD_INFO")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EidCardInfo {
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
    @Column(name = "CUSTOMER_NO")
    private String customerNo;
    @Column(name = "CHANNEL")
    private String channel;
    @Column(name = "ID_NUMBER")
    private String idNumber;
    @Column(name = "FULL_NAME")
    private String fullName;
    @Column(name = "DOB")
    private String dob;
    @Column(name = "CUSTOMER_TYPE")
    private String customerType;
    @Column(name = "OUTOFDATE")
    private String  outoDate;
    @Column(name = "IMAGE")
    private String image;
    @Column(name = "CREATED_BY")
    private String createdBy;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "REF_NO")
    private String  refNo;
    @Column(name = "UDF1")
    private String  udf1;
    @Column(name = "UDF2")
    private String  udf2;
    @Column(name = "UDF3")
    private String  udf3;
    @Column(name = "UDF4")
    private String  udf4;
    @Column(name = "UDF5")
    private String  udf5;
    @Column(name = "UDF6")
    private String  udf6;
    @Column(name = "DATE_VERIFY")
    private Date  dateVerify;
    @Column(name = "IS_VERIFY")
    private String  isVerify;
    @Column(name = "CHANEL_VERIFY")
    private String  chanelVerify;
    @Column(name = "OLD_IDENTIFY")
    private String  oldIdentify;
    @Column(name = "ID_ISSUE_DATE")
    private String  issueDate;
}
