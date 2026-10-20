package com.se.riddaradb.motif;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/motifs/")
public class MotifController {

    final MotifService motifService;

    public MotifController(MotifService motifService) {
        this.motifService = motifService;
    }

    @GetMapping("getmotifs")
    Collection<MotifDto> getMotifEntries(){
        return motifService.getMotifEntries();
    }

    @GetMapping("getmotifbyid/{id}")
    MotifDto getMotifEntryById(@PathVariable int id){
        return motifService.getMotifEntryById(id);
    }

    @GetMapping("getmotifsbyids")
    Collection<MotifDto> getMotifsByIds(@RequestParam(value="id") Integer[] ids){
        return motifService.getMotifsByIds(Set.of(ids));
    }

    @GetMapping("searchmotifs/{searchTerm}")
    Collection<MotifSearchResult> searchMotif(@PathVariable String searchTerm){
        return motifService.search(searchTerm);
    }

    @GetMapping("searchmotifsexact/{searchTerm}")
    Collection<MotifSearchResult> searchMotifExact(@PathVariable String searchTerm){
        return motifService.searchExact(searchTerm);
    }

    @GetMapping("getrootmotifs")
    Collection<MotifDto> getRootMotifs(){
        return motifService.getRootMotifs();
    }

    @GetMapping("getchildmotifsbyid/{id}")
    Collection<MotifDto> getChildMotifsById(@PathVariable int id){
        return motifService.getChildMotifs(id);
    }

    @PostMapping("postmotif")
    MotifDto postMotifEntry(@RequestBody @Valid MotifDto motifDto){
        return motifService.saveMotifEntry(motifDto);
    }

    @PutMapping("putmotif")
    MotifDto putMotifEntry(@RequestBody @Valid MotifDto motifDto){
        return motifService.updateMotifEntry(motifDto);
    }

    @DeleteMapping("deletemotif/{id}")
    void deleteMotifEntry(@PathVariable int id) throws AccessDeniedException {
        motifService.deleteMotifEntryById(id);
    }
}
