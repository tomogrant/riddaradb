package com.se.riddaradb.ms;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/msrepository/")
public class MsRepositoryController {

    final MsRepositoryService msRepositoryService;

    public MsRepositoryController(MsRepositoryService msRepositoryService) {
        this.msRepositoryService = msRepositoryService;
    }

    @GetMapping("getmsrepositories")
    Collection<MsRepositoryDto> getMsEntries(){
        return msRepositoryService.getMsRepositories();
    }

    @GetMapping("getmsrepositorybyid/{id}")
    MsRepositoryDto getMsEntryById(@PathVariable int id){
        return msRepositoryService.getMsRepositoryById(id);
    }

    @PostMapping("postmsrepository")
    MsRepositoryDto postMsEntry(@RequestBody @Valid MsRepositoryDto msRepositoryDto){
        return msRepositoryService.saveMsRepository(msRepositoryDto);
    }

    @PutMapping("putmsrepository")
    MsRepositoryDto putMsEntry(@RequestBody @Valid MsRepositoryDto msRepositoryDto){
        return msRepositoryService.updateMsRepository(msRepositoryDto);
    }

    @DeleteMapping("deletemsrepository/{id}")
    void deleteMsEntry(@PathVariable int id){
        msRepositoryService.deleteMsRepositoryById(id);
    }
}