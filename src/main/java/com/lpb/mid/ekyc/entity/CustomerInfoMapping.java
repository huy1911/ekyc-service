package com.lpb.mid.ekyc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "CUSTOMER_INFO_MAPPING")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfoMapping {

    @Id
    @GeneratedValue(
            generator = "system-uuid"
    )
    @GenericGenerator(
            name = "system-uuid",
            strategy = "uuid"
    )
    @Column(name = "GUID")
    private String guid;

    @Column(name = "CUSTOMER_NO")
    private String customerNo;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "HASH_ID")
    private String hashId;

    @Column(name = "CREATE_DATE")
    private LocalDate createDate;

    @Column(name = "INPUTTER")
    private String inputter;

    @Column(name = "CHECKER")
    private String checker;

    @Column(name = "UDF1")
    private String udf1;

    @Column(name = "UDF2")
    private String udf2;

    @Column(name = "UDF3")
    private String udf3;

    @Column(name = "UDF4")
    private String udf4;
    @Column(name = "UDF5")
    private String udf5;

}
