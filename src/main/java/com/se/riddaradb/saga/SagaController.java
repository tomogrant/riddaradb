package com.se.riddaradb.saga;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/sagas/")
public class SagaController {

    final SagaService sagaService;
    public SagaController(SagaService sagaService) {
        this.sagaService = sagaService;
    }

    @GetMapping("getsagas")
    Set<SagaResponseDto> getSagas(){
        return sagaService.getSagas();
    }

    @GetMapping("getsagabyid/{id}")
    SagaResponseDto getSagaById(@PathVariable int id){
        return sagaService.getSagaById(id);
    }

    @GetMapping("getsagatitles")
    Set<SagaTitleDto> getSagaTitles() { return sagaService.getSagaTitles();}

    @PostMapping("postsaga")
    ResponseEntity<SagaResponseDto> postSaga(@RequestBody @Valid SagaRequestDto sagaRequestDto){
        return ResponseEntity.ok(sagaService.saveSaga(sagaRequestDto));
    }

    @PutMapping("putsaga")
    SagaResponseDto putSaga(@RequestBody @Valid SagaRequestDto sagaRequestDto){
        return sagaService.updateSaga(sagaRequestDto);
    }

    @DeleteMapping("deletesaga/{id}")
    void deleteSaga(@PathVariable int id){
        sagaService.deleteSagaById(id);
    }
}
