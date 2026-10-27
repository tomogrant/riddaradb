package com.se.riddaradb.location;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/locations/")
public class LocationController {

    final LocationService locationService;
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("getlocations")
    Collection<LocationDto> getLocationEntry(){
        return locationService.getLocationEntries();
    }

    @GetMapping("getlocationbyid/{id}")
    LocationDto getLocationEntryById(@PathVariable int id){
        return locationService.getLocationEntryById(id);
    }

    @PostMapping("postlocation")
    LocationDto postLocationEntry(@RequestBody @Valid LocationDto locationDto){
        return locationService.saveLocationEntry(locationDto);
    }

    @PutMapping("putlocation")
    LocationDto putLocationEntry(@RequestBody @Valid LocationDto locationDto){
        return locationService.updateLocationEntry(locationDto);
    }

    @DeleteMapping("deletelocation/{id}")
    void deleteLocationEntry(@PathVariable int id){
        locationService.deleteLocationEntryById(id);
    }
}
