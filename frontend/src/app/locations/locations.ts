import { Component, computed, inject, OnInit } from "@angular/core";
import { PageHeader } from "../page-header/page-header";
import { ILocation } from "./ILocation";
import { LocationsService } from "./locations.service";
import { ISagaVersionTitleDto } from "../sagas/common/ISagaVersionTitleDto";
import { SagaService } from "../sagas/common/saga.service";
import { AuthService } from "../auth/auth.service";
import { firstValueFrom } from "rxjs";

@Component({
  selector: "app-locations",
  imports: [PageHeader],
  templateUrl: "./locations.html",
  styleUrl: "./locations.css",
})
export class Locations implements OnInit {
  private locationsService = inject(LocationsService);
  private sagaService = inject(SagaService);
  private authService = inject(AuthService);

  $loggedIn = computed(() => this.authService.$loggedIn());

  locations: ILocation[] = [];
  sagaVersionTitles: ISagaVersionTitleDto[] = [];
  sagaVersionTitleMap: Map<number, ISagaVersionTitleDto> = new Map<number, ISagaVersionTitleDto>();

  get locationsDisplay(){
    return this.locations.map(location => ({
        ...location,
        sagaVersions: this.sagaVersionTitles.filter(version => location.sagaVersionIds.includes(version.id))
      }));
  }

  ngOnInit(): void {
    this.initialise();
  }

  async initialise() {
    await this.getLocations();
    await this.getSagaVersionTitles();

    console.log(this.locationsDisplay);
  }

  async getLocations() {
    const locations = await firstValueFrom(this.locationsService.getLocations()).catch((err) => {
      console.log("Error fetching locations: " + JSON.stringify(err));
    });

    if (locations) {
      this.locations = locations;
    }
  }

  async getSagaVersionTitles() {
    const sagaVersionTitles = await firstValueFrom(this.sagaService.getSagaVersionTitles()).catch((err) => {
      console.log("Error fatching sagas: " + JSON.stringify(err));
    });

    if (sagaVersionTitles) {
      this.sagaVersionTitles = sagaVersionTitles;
    }
  }
}
