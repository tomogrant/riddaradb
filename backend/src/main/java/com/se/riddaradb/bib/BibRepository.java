package com.se.riddaradb.bib;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BibRepository extends JpaRepository<BibEntity, Integer> {
}
