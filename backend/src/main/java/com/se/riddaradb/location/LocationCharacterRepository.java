package com.se.riddaradb.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LocationCharacterRepository extends JpaRepository<LocationCharacterEntity, LocationCharacterKey> {

    Set<LocationCharacterEntity> findByLocationEntityId(Integer id);

    Set<LocationCharacterEntity> findByCharacterEntityId(Integer id);


}
