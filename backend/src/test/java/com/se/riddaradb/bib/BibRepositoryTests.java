package com.se.riddaradb.bib;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BibRepositoryTests {

    @Autowired
    private BibRepository bibRepository;

    @Test
    public void BibRepository_SaveBib_ReturnsBib(){

        BibEntity bibEntity = new BibEntity();

        bibEntity.setAuthors("T. Grant");
        bibEntity.setTitle("Title");
        bibEntity.setBook("Book");
        bibEntity.setPublisher("Publisher");
        bibEntity.setPageNumbers("10-15");

        BibEntity savedBibEntity = bibRepository.save(bibEntity);

        Assertions.assertThat(savedBibEntity).isNotNull();
        Assertions.assertThat(savedBibEntity.getAuthors()).isNotNull();
    }

}
