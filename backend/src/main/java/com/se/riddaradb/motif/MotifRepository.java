package com.se.riddaradb.motif;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotifRepository extends JpaRepository<MotifEntity, Integer> {

    List<MotifEntity> findByParentIsNull();

    List<MotifEntity> findByParentId(int id);

    List<MotifEntity> findByMotifCodeIgnoreCase(String searchTerm);

    List<MotifEntity> findByMotifCodeContainsIgnoreCase(String searchTerm);

    List<MotifEntity> findByMotifNameContainsIgnoreCase(String searchTerm);

    List<MotifEntity> findByDescriptionContainsIgnoreCase(String searchTerm);


}
