package com.se.riddaradb.saga;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SagaMsRepository extends JpaRepository<SagaMsEntity, SagaMsKey> {

    Set<SagaMsEntity> findBySagaEntityId(int id);

    Set<SagaMsEntity> findByMsEntityId(int id);

}
