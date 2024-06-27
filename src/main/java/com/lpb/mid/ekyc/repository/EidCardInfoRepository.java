package com.lpb.mid.ekyc.repository;

import com.lpb.mid.ekyc.dto.response.InfoDto;
import com.lpb.mid.ekyc.entity.EidCardInfo;
import com.lpb.mid.ekyc.entity.EidCardInfoDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EidCardInfoRepository extends JpaRepository<EidCardInfo, String> {

    EidCardInfo findFirstByCustomerNo(String customerNo);
    EidCardInfo findFirstByIdNumber(String cmd);
    List<EidCardInfo> findByCustomerNoOrIdNumber(String customerNo,String idNumber);
    EidCardInfo findFirstByCustomerNoOrIdNumberOrderByCreatedByDesc(String customerNo,String idNumber);
    EidCardInfo findByIdNumberAndIssueDate(String idNumber, String issueDate);
    EidCardInfo findByIdNumber(String idNumber);
}
