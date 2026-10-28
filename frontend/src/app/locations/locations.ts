import { Component, inject, OnInit } from "@angular/core";
import { PageHeader } from "../page-header/page-header";
import { ILocation } from "./ILocation";
import { LocationsService } from "./locations.service";
import { ISagaVersionTitleDto } from "../sagas/common/ISagaVersionTitleDto";
import { SagaService } from "../sagas/common/saga.service";

@Component({
  selector: "app-locations",
  imports: [PageHeader],
  templateUrl: "./locations.html",
  styleUrl: "./locations.css",
})
export class Locations implements OnInit {

  private locationsService = inject(LocationsService);
  private sagaService = inject(SagaService);

  locations: ILocation[] = [];
  sagaVersionTitles: ISagaVersionTitleDto[] = [];
  sagaVersionTitleMap: Map<number, ISagaVersionTitleDto> = new Map<number, ISagaVersionTitleDto>();

  ngOnInit(): void {
    this.getLocations();
    this.getSagaVersionTitles();
  }

  getLocations(){
    this.locationsService.getLocations().subscribe({
      next: locations => {
        this.locations = locations;
      }, 
      error: (err) => {
        console.log("Problem retrieving locations: " + JSON.stringify(err.error));
      }
    });
  }

  getSagaVersionTitles(){
    this.sagaService.getSagaVersionTitles().subscribe({
      next: sagaVersionTitles => {
        sagaVersionTitles.forEach(title => {
          this.sagaVersionTitleMap.set(title.id, title);
        });      
      },
      error: err => {
        console.log("Error fetching saga version titles: " + JSON.stringify(err));
      }
    });
  }
}
