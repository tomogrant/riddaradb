package com.se.riddaradb.sagaversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SagaVersionRepository extends JpaRepository<SagaVersionEntity, Integer> {
    List<SagaVersionEntity> findByTitleContainsIgnoreCase(String searchTerm);
}
