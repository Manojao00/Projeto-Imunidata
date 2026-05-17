package com.imunidata.repository;

import com.imunidata.model.RegistroVacinaCsv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroVacinaCsvRepository extends JpaRepository<RegistroVacinaCsv, Long> {
}
