package com.docGenSvc.repository;

import com.docGenSvc.model.entity.DocGenNgStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocGenNgRepository extends JpaRepository<DocGenNgStatus, String> {
}
