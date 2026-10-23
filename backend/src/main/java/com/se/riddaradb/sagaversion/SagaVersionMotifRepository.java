package com.se.riddaradb.sagaversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SagaVersionMotifRepository extends JpaRepository<SagaVersionMotifEntity, SagaVersionMotifKey> {

    Set<SagaVersionMotifEntity> findBySagaVersionEntityId(int id);

    Set<SagaVersionMotifEntity> findByMotifEntityId(int id);

}
