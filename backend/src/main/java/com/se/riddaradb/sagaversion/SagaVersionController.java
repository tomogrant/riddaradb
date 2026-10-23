package com.se.riddaradb.sagaversion;

import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/sagaversions/")
public class SagaVersionController {

    final SagaVersionService sagaVersionService;
    public SagaVersionController(SagaVersionService sagaVersionService) {
        this.sagaVersionService = sagaVersionService;
    }

    @GetMapping("getsagaversions")
    Set<SagaVersionResponseDto> getSagas(){
        return sagaVersionService.getSagaVersions();
    }

    @GetMapping("getsagaversionbyid/{id}")
    SagaVersionResponseDto getSagaById(@PathVariable int id){
        return sagaVersionService.getSagaVersionById(id);
    }

    @GetMapping("getsagaversiontitles")
    Set<SagaVersionTitleDto> getSagaVersionTitles(){
        return sagaVersionService.getSagaVersionTitles();
    }

//    @PostMapping("/sagas/postsagaversion")
//    SagaVersionResponseDto postSagas(@RequestBody SagaVersionRequestDto sagaVersionRequestDto){
//        return sagaVersionService.saveSagaVersion(sagaVersionRequestDto);
//    }
//
//    @PutMapping("/sagas/putsagaversion")
//    SagaVersionResponseDto putSaga(@RequestBody SagaVersionRequestDto sagaVersionRequestDto){
//        return sagaVersionService.saveSagaVersion(sagaVersionRequestDto);
//    }

    @DeleteMapping("deletesagaversion/{id}")
    void deleteSaga(@PathVariable int id){
        sagaVersionService.deleteSagaVersionById(id);
    }
}
