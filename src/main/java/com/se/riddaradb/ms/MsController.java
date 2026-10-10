package com.se.riddaradb.ms;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/ms/")
public class MsController {

    final MsService msService;

    public MsController(MsService msService) {
        this.msService = msService;
    }

    @GetMapping("getmsentries")
    Collection<MsDto> getMsEntries(){
        return msService.getMsEntries();
    }

    @GetMapping("getmsentrybyid/{id}")
    MsDto getMsEntryById(@PathVariable int id){
        return msService.getMsEntryById(id);
    }

    @GetMapping("getmsentriesbyrepoid/{id}")
    Collection<MsDto> getMsEntryByRepoId(@PathVariable Integer id){
        return msService.findByRepoId(id);
    }

    @PostMapping("postmsentry")
    MsDto postMsEntry(@RequestBody @Valid MsDto msDto){
        return msService.saveMsEntry(msDto);
    }

    @PutMapping("putmsentry")
    MsDto putMsEntry(@RequestBody @Valid MsDto msDto){
        return msService.updateMsEntry(msDto);
    }

    @DeleteMapping("deletemsentry/{id}")
    void deleteMsEntry(@PathVariable int id){
        msService.deleteMsEntryById(id);
    }
}
