package com.docGenSvc.repository;

import com.docGenSvc.model.entity.DocGenNgStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocGenNgRepository extends JpaRepository<DocGenNgStatus, String> {

}
