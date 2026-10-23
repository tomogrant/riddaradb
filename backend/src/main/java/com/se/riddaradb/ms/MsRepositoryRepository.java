package com.se.riddaradb.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MsRepositoryRepository extends JpaRepository<MsRepositoryEntity, Integer> {

}
