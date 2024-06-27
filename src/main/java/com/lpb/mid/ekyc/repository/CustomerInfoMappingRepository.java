package com.lpb.mid.ekyc.repository;

import com.lpb.mid.ekyc.entity.CustomerInfoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInfoMappingRepository extends JpaRepository<CustomerInfoMapping, String> {

    List<CustomerInfoMapping> findByCustomerNoOrUdf4(String customerNo, String cmt);

    CustomerInfoMapping findFirstByCustomerNo(String customerNo);
}
