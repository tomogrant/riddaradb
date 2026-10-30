package com.se.riddaradb.bib;

import com.se.riddaradb.saga.SagaEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bibliography_entry")
public class BibEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private PublicationType publicationType;

    private String authors;

    private String editors;

    private String translators;
    
    private String title;

    private String url;

    private String bookEditors;

    private String book;

    private String bookSeries;

    private String volume;

    private String numOfVolumes;

    private String placeOfPublication;

    private String publisher;

    private String publicationYear;

    private String pageNumbers;

    private Boolean recommended;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "bibEntities")
    private Set<SagaEntity> sagaEntities = new HashSet<>();

    public BibEntity(){
    }

    public BibEntity(Integer id,
                     PublicationType publicationType,
                     String authors,
                     String editors,
                     String translators,
                     String title,
                     String url,
                     String bookEditors,
                     String book,
                     String bookSeries,
                     String volume,
                     String numOfVolumes,
                     String placeOfPublication,
                     String publisher,
                     String publicationYear,
                     String pageNumbers,
                     Boolean recommended,
                     String description) {
        this.id = id;
        this.publicationType = publicationType;
        this.authors = authors;
        this.editors = editors;
        this.translators = translators;
        this.title = title;
        this.url = url;
        this.bookEditors = bookEditors;
        this.book = book;
        this.bookSeries = bookSeries;
        this.volume = volume;
        this.numOfVolumes = numOfVolumes;
        this.placeOfPublication = placeOfPublication;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.pageNumbers = pageNumbers;
        this.recommended = recommended;
        this.description = description;
    }

    public enum PublicationType {
        UNDEFINED,
        JOURNAL_ARTICLE,
        BOOK_CHAPTER,
        EDITION,
        TRANSLATION,
        MONOGRAPH,
        EDITED_COLLECTION,
        THESIS,
        WEBSITE,
        OTHER
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PublicationType getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(PublicationType publicationType) {
        this.publicationType = publicationType;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getEditors() {
        return editors;
    }

    public void setEditors(String editors) {
        this.editors = editors;
    }

    public String getTranslators() {
        return translators;
    }

    public void setTranslators(String translators) {
        this.translators = translators;
    }

    public @NotBlank String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBookEditors() {
        return bookEditors;
    }

    public void setBookEditors(String bookEditors) {
        this.bookEditors = bookEditors;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getBookSeries() {
        return bookSeries;
    }

    public void setBookSeries(String bookSeries) {
        this.bookSeries = bookSeries;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNumOfVolumes() {
        return numOfVolumes;
    }

    public void setNumOfVolumes(String numOfVolumes) {
        this.numOfVolumes = numOfVolumes;
    }

    public String getPlaceOfPublication() {
        return placeOfPublication;
    }

    public void setPlaceOfPublication(String placeOfPublication) {
        this.placeOfPublication = placeOfPublication;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPageNumbers() {
        return pageNumbers;
    }

    public void setPageNumbers(String pageNumbers) {
        this.pageNumbers = pageNumbers;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<SagaEntity> getSagaEntities() {
        return sagaEntities;
    }

    public void setSagaEntities(Set<SagaEntity> sagaEntities) {
        this.sagaEntities = sagaEntities;
    }
}
