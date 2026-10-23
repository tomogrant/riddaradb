package com.se.riddaradb.bib;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class BibController {

    final BibService bibService;

    public BibController(BibService bibService) {
        this.bibService = bibService;
    }

    @GetMapping("/bibentries/getbibentries")
    Collection<BibDto> getBibEntries(){
        return bibService.getBibEntries();
    }

    @GetMapping("/bibentries/getbibentrybyid/{id}")
    BibDto getBibEntryById(@PathVariable int id){
        return bibService.getBibEntryById(id);
    }

    @PostMapping("/bibentries/postbibentry")
    BibDto postBibEntry(@RequestBody @Valid BibDto bibDto){
        return bibService.saveBibEntry(bibDto);
    }

    @PutMapping("/bibentries/putbibentry")
    BibDto putBibEntry(@RequestBody @Valid BibDto bibDto){
        return bibService.saveBibEntry(bibDto);
    }

    @DeleteMapping("/bibentries/deletebibentry/{id}")
    void deleteSaga(@PathVariable int id){
        bibService.deleteBibEntryById(id);
    }
}
