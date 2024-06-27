package com.lpb.mid.ekyc.repository;

import com.lpb.mid.ekyc.dto.response.EidCardInfoDto;
import com.lpb.mid.ekyc.dto.response.InfoDto;
import com.lpb.mid.ekyc.entity.EidCardInfoDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EidCardInfoDetailRepository extends JpaRepository<EidCardInfoDetail, String> {

//    @Query( value = "SELECT new  com.lpb.mid.ekyc.dto.response.InfoDto" +
//            "(b.isVerify,a.hashId,b.oldIdentify," +
//            "a.udf1,a.udf2,b.udf1,b.refNo,b.udf2," +
//            "b.udf3,b.udf4,b.udf5,b.udf6,c.idNumber)" +
//            " FROM CustomerInfoMapping a " +
//            "JOIN EidCardInfo b " +
//            "ON a.customerNo = b.customerNo " +
//            "JOIN EidCardInfoDetail c " +
//            "ON c.idNumber = b.idNumber " +
//            "WHERE a.customerNo  =:customerNo")
//    InfoDto queryInfo(@Param("customerNo") String customerNo);

    @Query( value = "SELECT new  com.lpb.mid.ekyc.dto.response.InfoDto(b.isVerify,b.oldIdentify,b.refNo,b.udf1,b.udf2,b.udf3,b.udf4,b.udf5,b.udf6,c.idNumber) FROM EidCardInfo b JOIN EidCardInfoDetail c ON c.idNumber = b.idNumber WHERE b.customerNo  =:customerNo order by b.createdDate desc")
    List<InfoDto> queryCifInfo(@Param("customerNo") String customerNo);
    @Query( value = "SELECT new  com.lpb.mid.ekyc.dto.response.InfoDto(b.isVerify,b.oldIdentify,b.refNo,b.udf1,b.udf2,b.udf3,b.udf4,b.udf5,b.udf6,c.idNumber) FROM EidCardInfo b JOIN EidCardInfoDetail c ON c.idNumber = b.idNumber WHERE b.idNumber  =:idNumber order by b.createdDate desc")
    List<InfoDto> queryCifInfoByIdNumber(@Param("idNumber") String idNumber);

    @Query( value = "SELECT new com.lpb.mid.ekyc.dto.response.EidCardInfoDto(b.idNumber, b.oldIdentify, b.fullName, b.dob, c.gender, b.issueDate, c.emplCode, c.logging, b.image, b.isVerify, c.nationality, c.nation, c.religion, c.homeTown, c.identifying, c.expiredDate, c.fatherName, c.motherName, c.husbandOrWifeName, c.placeOfResidence) FROM EidCardInfo b JOIN EidCardInfoDetail c ON b.idNumber = c.idNumber WHERE b.idNumber  =:idNumber and b.issueDate = :issueDate")
    EidCardInfoDto findDetailInfoByIdNumberAndIssueDate(@Param("idNumber") String idNumber, @Param("issueDate") String issueDate);

    EidCardInfoDetail findFirstByIdNumberOrderByCreateByDesc(String idNumber);

    EidCardInfoDetail findFirstByIdNumberAndIssueDateOrderByCreateByDesc(String idNumber,String issueDate);
    EidCardInfoDetail findByIdNumber(String idNumber);
}
