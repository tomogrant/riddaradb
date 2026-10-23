package com.se.riddaradb.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MsRepository extends JpaRepository<MsEntity, Integer> {

    List<MsEntity> findByMsRepositoryEntityId(Integer id);
}
