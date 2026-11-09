import { Component, computed, inject, OnInit, Signal, signal } from "@angular/core";
import { PageHeader } from "../page-header/page-header";
import { ILocation } from "./ILocation";
import { LocationsService } from "./locations.service";
import { ISagaVersionTitleDto } from "../sagas/common/ISagaVersionTitleDto";
import { SagaService } from "../sagas/common/saga.service";
import { AuthService } from "../auth/auth.service";
import { debounceTime, distinctUntilChanged, firstValueFrom } from "rxjs";
import { Mode } from "../shared/Enums";
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from "@angular/forms";
import { Continent } from "./Continent";
import { Collapse, Modal } from "bootstrap";
import { CharactersService } from "../characters/characters.service";
import { ICharacterName } from "../characters/ICharacterName";
import { debounce, form, FormField } from "@angular/forms/signals";
import { ActivatedRoute, RouterLink } from "@angular/router";

@Component({
  selector: "app-locations",
  imports: [PageHeader, ReactiveFormsModule, FormField, RouterLink],
  templateUrl: "./locations.html",
  styleUrl: "./locations.css",
})
export class Locations implements OnInit {
  private locationsService = inject(LocationsService);
  private sagaService = inject(SagaService);
  private authService = inject(AuthService);
  private charactersService = inject(CharactersService);
  private route = inject(ActivatedRoute);

  $loggedIn = computed(() => this.authService.$loggedIn());

  fragment: string | null = null;

  location: ILocation = this.initialiseLocation();
  locationToDelete: number = 0;
  locationsPerPage: number = 50;
  $currentPage = signal<number>(1);

  $pageText = computed(() => {
    const currentPage = this.$currentPage();
    const totalLength = this.$filteredLocations().length;

    const bottomRange = (currentPage - 1) * this.locationsPerPage + 1;
    const topRange = Math.min(currentPage * totalLength, totalLength);

    return totalLength ? `Displaying ${bottomRange}-${topRange} of ${totalLength} locations.` : "";
  });

  filteredCharacters: {
    form: FormGroup;
    index: number;
  }[] = [];

  filteredSagaVersionTitles: ISagaVersionTitleDto[] = [];

  characters: ICharacterName[] = [];
  charTrackingId: number = 0;

  $sagaVersionTitles = signal<ISagaVersionTitleDto[]>([]);

  selectAContinentText: string = "Select a continent:";
  continentsUi: string[] = [];

  readonly Mode = Mode;
  mode: Mode = Mode.NONE;

  initialising: boolean = false;
  loading: boolean = false;
  showValidationErrors: boolean = false;

  $locations = signal<ILocation[]>([]);

  $filters = signal({
    continentsFilter: [] as string[],
    sagaVersionFilter: [] as number[],
  });

  //Filtered locations
  $filteredLocations = computed(() => {
    let results = this.$locations();
    const filters = this.$filters();
    const searchTerm = this.$searchForm.searchTerm().value().toLowerCase().trim();

    results = results.filter(
      (location) =>
        //Continents filter
        (!filters.continentsFilter.length ||
          filters.continentsFilter.includes(this.mapContinentToUi(location.continent))) &&
        //Saga version filter
        (!filters.sagaVersionFilter.length ||
          location.sagaVersionIds.some((id) => filters.sagaVersionFilter.includes(id))) &&
        //Search bar
        (location.name.trim().toLowerCase().includes(searchTerm) ||
          location.translation.trim().toLowerCase().includes(searchTerm) ||
          location.notes.trim().toLowerCase().includes(searchTerm)),
    );

    return results;
  });

  //Locations coupled with saga versions for display
  $locationsDisplay = computed(() => {
    return [...this.$filteredLocations()].map((location) => ({
      ...location,
      sagaVersions: this.$sagaVersionTitles().filter((sagaVersion) => location.sagaVersionIds.includes(sagaVersion.id)),
    }));
  });

  //Locations and sagas paginated
  $locationsPaginated = computed(() => {
    const startIndex = (this.$currentPage() - 1) * this.locationsPerPage;
    const endIndex = startIndex + this.locationsPerPage;

    return this.$locationsDisplay().slice(startIndex, endIndex);
  });

  $totalPages: Signal<number> = computed(() => {
    const pages = Math.ceil(this.$locationsDisplay().length / this.locationsPerPage);

    return pages ? pages : 1;
  });

  $pagesIterable = computed(() => {
    return Array.from(
      {
        length: this.$totalPages(),
      },
      (val, i) => i + 1,
    );
  });

  $continentFilterActive = computed(() => {
    return this.$filters().continentsFilter.length > 0;
  });

  $sagaVersionFilterActive = computed(() => {
    return this.$filters().sagaVersionFilter.length > 0;
  });

  //Forms
  $searchModel = signal({
    searchTerm: "",
  });

  $searchForm = form(this.$searchModel, (schemaPath) => {
    debounce(schemaPath.searchTerm, 250);
  });

  editForm = new FormGroup({
    id: new FormControl<number | null>({ value: null, disabled: true }),
    name: new FormControl<string>("", {
      nonNullable: true,
      validators: [Validators.required],
    }),
    translation: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
    continent: new FormControl<string>(this.selectAContinentText, {
      nonNullable: true,
      validators: this.continentNotSelected(),
    }),
    notes: new FormControl<string>("", { nonNullable: true }),
    characterFormFilter: new FormControl<string>("", { nonNullable: true }),
    characterForms: new FormArray<FormGroup>([]),
    sagaVersionFormFilter: new FormControl<string>("", { nonNullable: true }),
    sagaVersionIds: new FormControl<number[]>([], { nonNullable: true }),
  });

  get id(): FormControl<number | null> {
    return this.editForm.controls.id;
  }

  get name(): FormControl<string> {
    return this.editForm.controls.name;
  }

  get translation(): FormControl<string> {
    return this.editForm.controls.translation;
  }

  get continent(): FormControl<string> {
    return this.editForm.controls.continent;
  }

  get notes(): FormControl<string> {
    return this.editForm.controls.notes;
  }

  get characterFormFilter(): FormControl<string> {
    return this.editForm.controls.characterFormFilter;
  }

  get characterForms(): FormArray<FormGroup> {
    return this.editForm.controls.characterForms;
  }

  get sagaVersionFormFilter(): FormControl<string> {
    return this.editForm.controls.sagaVersionFormFilter;
  }

  get sagaVersionIds(): FormControl<number[]> {
    return this.editForm.controls.sagaVersionIds;
  }

  ngOnInit(): void {
    this.initialise();
  }

  async initialise() {
    this.route.fragment.subscribe((frag) => {
      this.fragment = frag;
    });

    this.initialising = true;

    await this.getCharacterNames();
    await this.getSagaVersionTitles();
    await this.getLocations();

    this.initialising = false;

    Object.values(Continent).forEach((continent) => {
      this.continentsUi.push(this.mapContinentToUi(continent));
    });

    this.characterFormFilter.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe({
      next: (value) => this.updateCharacterFormFilter(value ? String(value).trim().toLowerCase() : ""),
    });

    this.sagaVersionFormFilter.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe({
      next: (value) => this.updateSagaVersionFormFilter(value ? String(value).trim().toLowerCase() : ""),
    });

    //Page has been accessed from saga; highlight requested entry
    if (this.fragment && /^\d+$/.test(this.fragment) && Number.isSafeInteger(Number(this.fragment)) && Number(this.fragment) > 0) {
      const locationIndex = this.$locations().findIndex((loc) => loc.id === Number(this.fragment));

      if (locationIndex > -1) {
        this.$currentPage.set(Math.floor(locationIndex / this.locationsPerPage) + 1);

        this.scrollToLocation();
      }
    }
  }

  addLocation() {
    this.mode = Mode.ADD;
    this.showValidationErrors = false;
    this.updateSagaVersionFormFilter("");
    this.location = this.initialiseLocation();
    this.editForm.reset();
    this.populateCharacterForms();
    this.openAddEditModal();
  }

  editLocation(id: number) {
    this.mode = Mode.EDIT;
    this.editForm.reset();
    this.updateSagaVersionFormFilter("");
    this.showValidationErrors = false;
    this.hideAccordion();
    const location = this.$locations().find((loc) => loc.id === id);

    if (location) {
      this.location = location;
      this.editForm.patchValue({
        id: this.location.id,
        name: this.location.name,
        translation: this.location.translation,
        continent: this.mapContinentToUi(this.location.continent),
        sagaVersionIds: this.location.sagaVersionIds,
      });

      this.populateCharacterForms();
      this.openAddEditModal();
    } else {
      console.log("Location cannot be found!");
    }
  }

  mapContinentToUi(continent: Continent): string {
    switch (continent) {
      case Continent.EUROPE:
        return "Europe";
      case Continent.ASIA:
        return "Asia";
      case Continent.AFRICA:
        return "Africa";
      case Continent.UNKNOWN:
        return "Unknown";
      default:
        console.log("Continent cannot be mapped to UI!");
        return "";
    }
  }

  mapContinentFromUi(continent: string): Continent {
    switch (continent) {
      case "Europe":
        return Continent.EUROPE;
      case "Asia":
        return Continent.ASIA;
      case "Africa":
        return Continent.AFRICA;
      case "Unknown":
        return Continent.UNKNOWN;
      default:
        console.log("Continent cannot be mapped from UI!");
        return Continent.UNKNOWN;
    }
  }

  scrollToLocation() {
    setTimeout(() => {
      const locationRowId = `entry-${this.fragment}`;
      const locationRow = document.getElementById(locationRowId);

      if (locationRow) {
        locationRow.scrollIntoView({ behavior: "smooth", block: "center" });
        locationRow.classList.add("highlight-row");
      }
    }, 100);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.$totalPages()) {
      this.$currentPage.update(() => page);
    }
  }

  clearAllFilters() {
    this.$filters.update((current) => {
      return {
        ...current,
        continentsFilter: [],
        sagaVersionFilter: [],
      };
    });

    this.$searchForm.searchTerm().value.set("");
    this.$currentPage.set(1);
  }

  toggleContinentFilter(continent: string) {
    this.$filters.update((current) => {
      const continentsFilter = current.continentsFilter.includes(continent)
        ? current.continentsFilter.filter((continentInFilter) => continentInFilter !== continent)
        : [...current.continentsFilter, continent];

      this.$currentPage.set(1);

      return { ...current, continentsFilter };
    });
  }

  continentCheckboxChecked(continent: string) {
    return this.$filters().continentsFilter.includes(continent);
  }

  clearContinentFilter() {
    this.$filters.update((current) => ({
      ...current,
      continentsFilter: [],
    }));

    this.$currentPage.set(1);
  }

  toggleSagaVersionFilter(sagaVersionId: number) {
    this.$filters.update((current) => {
      const sagaVersionFilter = current.sagaVersionFilter.includes(sagaVersionId)
        ? current.sagaVersionFilter.filter((idInFilter) => idInFilter !== sagaVersionId)
        : [...current.sagaVersionFilter, sagaVersionId];

      this.$currentPage.set(1);

      return { ...current, sagaVersionFilter };
    });
  }

  sagaVersionCheckboxChecked(sagaVersionId: number) {
    return this.$filters().sagaVersionFilter.includes(sagaVersionId);
  }

  clearSagaVersionFilter() {
    this.$filters.update((current) => ({
      ...current,
      sagaVersionFilter: [],
    }));

    this.$currentPage.set(1);
  }

  initialiseLocation(): ILocation {
    return {
      id: null,
      name: "",
      translation: "",
      continent: Continent.UNKNOWN,
      notes: "",
      locationCharacterDtos: [],
      sagaVersionIds: [],
    };
  }

  populateCharacterForms() {
    this.characterForms.clear();
    this.characters.forEach((char) => {
      const characterForm = this.createCharacterForm(char);
      this.configureCharacterForm(characterForm);
      this.characterForms.push(characterForm);

      this.updateCharacterFormFilter("");
    });
  }

  createCharacterForm(char: ICharacterName): FormGroup {
    const charAssignedToLocation = this.location.locationCharacterDtos.find(
      (character) => character.characterId == char.id,
    );
    const selected = !!charAssignedToLocation;

    return new FormGroup({
      trackingId: new FormControl<number>({ value: this.charTrackingId++, disabled: true }),
      charId: new FormControl<number | null>({ value: char.id, disabled: true }),
      name: new FormControl<string>({ value: char.name, disabled: true }),
      significantAssociation: new FormControl<boolean>(
        { value: charAssignedToLocation ? charAssignedToLocation.significantAssociation : false, disabled: !selected },
        Validators.required,
      ),
      selected: new FormControl<boolean>(selected),
    });
  }

  configureCharacterForm(form: FormGroup) {
    form.get("selected")?.valueChanges.subscribe((selected) => {
      const significantAssociation = form.get("significantAssociation");

      if (selected) {
        significantAssociation?.enable();
      } else {
        significantAssociation?.setValue(false);
        significantAssociation?.disable();
      }
    });
  }

  updateCharacterFormFilter(searchTerm: string) {
    this.filteredCharacters = this.characterForms.controls
      .map((form, index) => ({ form: form, index: index }))
      .filter((form) => String(form.form.get("name")?.value).toLowerCase().includes(searchTerm));
  }

  updateSagaVersionFormFilter(searchTerm: string) {
    this.filteredSagaVersionTitles = this.$sagaVersionTitles().filter((saga) =>
      String(saga.title).toLowerCase().includes(searchTerm),
    );
  }

  sagaVersionChecked(id: number): boolean {
    return this.sagaVersionIds.value.includes(id);
  }

  toggleSagaVersion(id: number) {
    const ids: number[] = this.sagaVersionIds.value;

    //If sagaIds form includes id, filter it out. If not, add it.
    this.sagaVersionIds.setValue(ids.includes(id) ? ids.filter((idsInForm) => idsInForm !== id) : [...ids, id]);
  }

  openAddEditModal() {
    var addEditModal = document.getElementById("addEditLocation");
    if (addEditModal != null) {
      var modal = Modal.getOrCreateInstance(addEditModal);
      if (modal != null) {
        modal.show();
      }
    }
  }

  closeAddEditModal() {
    var addEditModal = document.getElementById("addEditLocation");
    if (addEditModal != null) {
      var modal = Modal.getInstance(addEditModal);
      if (modal != null) {
        modal.hide();
      }
    }
  }

  openDeleteModal(id: number) {
    this.locationToDelete = id;
    var deleteModal = document.getElementById("deleteLocation");
    if (deleteModal != null) {
      var modal = Modal.getOrCreateInstance(deleteModal);
      modal?.show();
    }
  }

  closeDeleteModal() {
    var deleteModal = document.getElementById("deleteLocation");
    if (deleteModal != null) {
      var modal = Modal.getInstance(deleteModal);
      modal?.hide();
    }
  }

  hideAccordion() {
    const accordions = document.querySelectorAll("#addEditLocation .accordion-collapse");

    accordions.forEach((element) => {
      const accordionInstance = Collapse.getOrCreateInstance(element, { toggle: false });
      if (accordionInstance) accordionInstance.hide();
    });
  }

  submitAddOrEdit() {
    this.name.updateValueAndValidity();
    this.translation.updateValueAndValidity();
    this.continent.updateValueAndValidity();

    if (this.editForm.valid) {
      this.location.name = this.name.value;
      this.location.translation = this.translation.value;
      this.location.continent = this.mapContinentFromUi(this.continent.value);
      this.location.notes = this.notes.value;
      this.location.sagaVersionIds = this.sagaVersionIds.value;

      const characterFormsRaw = this.characterForms.getRawValue();

      this.location.locationCharacterDtos = characterFormsRaw
        .filter((form) => form["selected"])
        .map((form) => ({
          characterId: form["charId"],
          significantAssociation: form["significantAssociation"],
        }));

      if (this.mode === Mode.ADD) {
        this.postLocation();
      } else if (this.mode === Mode.EDIT) {
        this.updateLocation();
      }
    } else {
      this.showValidationErrors = true;
    }
  }

  async getLocations() {
    const locations = await firstValueFrom(this.locationsService.getLocations()).catch((err) => {
      console.log("Error fetching locations: " + JSON.stringify(err));
    });

    if (locations) {
      this.$locations.set(locations.sort((a, b) => a.name.localeCompare(b.name)));
    }
  }

  async getCharacterNames() {
    const characters = await firstValueFrom(this.charactersService.getCharacterNames()).catch((err) => {
      console.log("Error fetching characters: " + JSON.stringify(err));
    });

    if (characters) {
      this.characters = characters.sort((a, b) => a.name.localeCompare(b.name));
    }
  }

  async getSagaVersionTitles() {
    const sagaVersionTitles = await firstValueFrom(this.sagaService.getSagaVersionTitles()).catch((err) => {
      console.log("Error fatching sagas: " + JSON.stringify(err));
    });

    if (sagaVersionTitles) {
      this.$sagaVersionTitles.set(
        sagaVersionTitles
          .filter((saga) => this.$loggedIn() || saga.published)
          .sort((a, b) => a.abbreviation.localeCompare(b.abbreviation)),
      );
      this.updateSagaVersionFormFilter("");
    }
  }

  postLocation() {
    this.loading = true;
    this.locationsService.postLocation(this.location).subscribe({
      next: (location) => {
        this.loading = false;
        this.closeAddEditModal();
        console.log("Saved successfully: " + JSON.stringify(location));
        if (!location.id) return;

        this.$locations.update((current) => {
          const next = [...current];
          next.push(location);
          next.sort((a, b) => a.name.localeCompare(b.name));
          return next;
        });
      },
      error: (err) => {
        this.loading = false;
        this.closeAddEditModal();
        console.log("Problem with saving: " + JSON.stringify(err));
      },
    });
  }

  updateLocation() {
    this.loading = true;
    this.locationsService.putLocation(this.location).subscribe({
      next: (location) => {
        this.loading = false;
        this.closeAddEditModal();
        console.log("Updated successfully: " + JSON.stringify(location));
        if (!location.id) return;

        this.$locations.update((current) => {
          let next = [...current];
          const locationIndex = next.findIndex((loc) => loc.id === location.id);
          if (locationIndex !== -1) {
            next[locationIndex] = location;
            next.sort((a, b) => a.name.localeCompare(b.name));
          }

          return next;
        });
      },
      error: (err) => {
        this.loading = false;
        this.closeAddEditModal();
        console.log("Problem with updating: " + JSON.stringify(err));
      },
    });
  }

  deleteLocation() {
    this.closeDeleteModal();

    this.locationsService.deleteLocation(this.locationToDelete).subscribe({
      next: () => {
        const deletedLocationIndex = this.$locations().findIndex((loc) => loc.id === this.locationToDelete);
        if (deletedLocationIndex >= 0) {
          this.$locations.update((current) => {
            const next = [...current];
            next.splice(deletedLocationIndex, 1);
            return next;
          });
        }
      },
      error: (err) => console.log("problem with deleting location: " + JSON.stringify(err)),
    });
  }

  // locationUnique(): ValidatorFn {
  //   return (control: AbstractControl): ValidationErrors | null => {
  //     const name = String(control.value ?? "")
  //       .trim()
  //       .toLowerCase();

  //     if (!name) {
  //       return null;
  //     }

  //     const duplicate = this.$locations().find(
  //       (loc) => loc.name.trim().toLowerCase() === name && loc.id !== control.parent?.get("id")?.value,
  //     );

  //     return duplicate ? { locationNotUnique: true } : null;
  //   };
  // }

  continentNotSelected(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return null;
      }

      if (value === this.selectAContinentText) {
        return { continentNotSelected: true };
      } else {
        return null;
      }
    };
  }
}
