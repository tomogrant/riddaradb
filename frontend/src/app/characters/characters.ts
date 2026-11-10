import { Component, computed, inject, OnInit, Signal, signal } from "@angular/core";
import { PageHeader } from "../page-header/page-header";
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
import { Collapse, Modal } from "bootstrap";
import { CharactersService } from "../characters/characters.service";
import { ICharacterName } from "../characters/ICharacterName";
import { debounce, form, FormField } from "@angular/forms/signals";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { LocationsService } from "../locations/locations.service";
import { ICharacter } from "./ICharacter";
import { ILocationName } from "../locations/ILocationName";
import { Species } from "./Species";
import { Sex } from "./Sex";

@Component({
  selector: "app-locations",
  imports: [PageHeader, ReactiveFormsModule, FormField, RouterLink],
  templateUrl: "./characters.html",
  styleUrl: "./characters.css",
})
export class Characters implements OnInit {
  private locationsService = inject(LocationsService);
  private sagaService = inject(SagaService);
  private authService = inject(AuthService);
  private charactersService = inject(CharactersService);
  private route = inject(ActivatedRoute);

  $loggedIn = computed(() => this.authService.$loggedIn());

  fragment: string | null = null;

  character: ICharacter = this.initialiseCharacter();
  characterToDelete: number = 0;
  charactersPerPage: number = 50;
  $currentPage = signal<number>(1);

  $pageText = computed(() => {
    const currentPage = this.$currentPage();
    const totalLength = this.$filteredCharacters().length;

    const bottomRange = (currentPage - 1) * this.charactersPerPage + 1;
    const topRange = Math.min(currentPage * totalLength, totalLength);

    return totalLength ? `Displaying ${bottomRange}-${topRange} of ${totalLength} characters.` : "";
  });

  filteredLocations: {
    form: FormGroup;
    index: number;
  }[] = [];

  filteredSagaVersionTitles: ISagaVersionTitleDto[] = [];

  locations: ILocationName[] = [];
  charTrackingId: number = 0;

  $sagaVersionTitles = signal<ISagaVersionTitleDto[]>([]);

  selectASpeciesText: string = "Select a species:";
  speciesUi: string[] = [];
  selectASexText: string = "Select a sex:";
  sexUi: string[] = [];

  readonly Mode = Mode;
  mode: Mode = Mode.NONE;

  initialising: boolean = false;
  loading: boolean = false;
  showValidationErrors: boolean = false;

  $characters = signal<ICharacter[]>([]);

  $filters = signal({
    speciesFilter: [] as string[],
    sexFilter: [] as string[],
    sagaVersionFilter: [] as number[],
  });

  //Filtered characters
  $filteredCharacters = computed(() => {
    let results = this.$characters();
    const filters = this.$filters();
    const searchTerm = this.$searchForm.searchTerm().value().toLowerCase().trim();

    results = results.filter(
      (character) =>
        //Species filter
        (!filters.speciesFilter.length || filters.speciesFilter.includes(this.mapSpeciesToUi(character.species))) &&
        //Species filter
        (!filters.sexFilter.length || filters.sexFilter.includes(this.mapSexToUi(character.sex))) &&
        //Saga version filter
        (!filters.sagaVersionFilter.length ||
          character.sagaVersionIds.some((id) => filters.sagaVersionFilter.includes(id))) &&
        //Search bar
        (character.name.trim().toLowerCase().includes(searchTerm) ||
          character.notes.trim().toLowerCase().includes(searchTerm)),
    );

    return results;
  });

  //Characters coupled with saga versions for display
  $charactersDisplay = computed(() => {
    return [...this.$filteredCharacters()].map((character) => ({
      ...character,
      sagaVersions: this.$sagaVersionTitles().filter((sagaVersion) =>
        character.sagaVersionIds.includes(sagaVersion.id),
      ),
    }));
  });

  //Characters and sagas paginated
  $charactersPaginated = computed(() => {
    const startIndex = (this.$currentPage() - 1) * this.charactersPerPage;
    const endIndex = startIndex + this.charactersPerPage;

    return this.$charactersDisplay().slice(startIndex, endIndex);
  });

  $totalPages: Signal<number> = computed(() => {
    const pages = Math.ceil(this.$charactersDisplay().length / this.charactersPerPage);

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

  $speciesFilterActive = computed(() => {
    return this.$filters().speciesFilter.length > 0;
  });

  $sexFilterActive = computed(() => {
    return this.$filters().sexFilter.length > 0;
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
    species: new FormControl<string>(this.selectASpeciesText, {
      nonNullable: true,
      validators: this.speciesNotSelected(),
    }),
    sex: new FormControl<string>(this.selectASexText, {
      nonNullable: true,
      validators: this.sexNotSelected(),
    }),
    notes: new FormControl<string>("", { nonNullable: true }),
    locationFormFilter: new FormControl<string>("", { nonNullable: true }),
    locationForms: new FormArray<FormGroup>([]),
    sagaVersionFormFilter: new FormControl<string>("", { nonNullable: true }),
    sagaVersionIds: new FormControl<number[]>([], { nonNullable: true }),
  });

  get id(): FormControl<number | null> {
    return this.editForm.controls.id;
  }

  get name(): FormControl<string> {
    return this.editForm.controls.name;
  }

  get species(): FormControl<string> {
    return this.editForm.controls.species;
  }

  get sex(): FormControl<string> {
    return this.editForm.controls.sex;
  }

  get notes(): FormControl<string> {
    return this.editForm.controls.notes;
  }

  get locationFormFilter(): FormControl<string> {
    return this.editForm.controls.locationFormFilter;
  }

  get locationForms(): FormArray<FormGroup> {
    return this.editForm.controls.locationForms;
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

    await this.getLocationNames();
    await this.getSagaVersionTitles();
    await this.getCharacters();

    this.initialising = false;

    Object.values(Species).forEach((species) => {
      this.speciesUi.push(this.mapSpeciesToUi(species));
    });

    Object.values(Sex).forEach((sex) => {
      this.sexUi.push(this.mapSexToUi(sex));
    });

    this.locationFormFilter.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe({
      next: (value) => this.updateLocationFormFilter(value ? String(value).trim().toLowerCase() : ""),
    });

    this.sagaVersionFormFilter.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe({
      next: (value) => this.updateSagaVersionFormFilter(value ? String(value).trim().toLowerCase() : ""),
    });

    //Page has been accessed from saga; highlight requested entry
    if (
      this.fragment &&
      /^\d+$/.test(this.fragment) &&
      Number.isSafeInteger(Number(this.fragment)) &&
      Number(this.fragment) > 0
    ) {
      const characterIndex = this.$characters().findIndex((loc) => loc.id === Number(this.fragment));

      if (characterIndex > -1) {
        this.$currentPage.set(Math.floor(characterIndex / this.charactersPerPage) + 1);

        this.scrollToCharacter();
      }
    }
  }

  addCharacter() {
    this.mode = Mode.ADD;
    this.showValidationErrors = false;
    this.updateSagaVersionFormFilter("");
    this.character = this.initialiseCharacter();
    this.editForm.reset();
    this.populateLocationForms();
    this.openAddEditModal();
  }

  editCharacter(id: number) {
    this.mode = Mode.EDIT;
    this.editForm.reset();
    this.updateSagaVersionFormFilter("");
    this.showValidationErrors = false;
    this.hideAccordion();
    const character = this.$characters().find((loc) => loc.id === id);

    if (character) {
      this.character = character;
      this.editForm.patchValue({
        id: this.character.id,
        name: this.character.name,
        species: this.mapSpeciesToUi(this.character.species),
        sex: this.mapSexToUi(this.character.sex),
        sagaVersionIds: this.character.sagaVersionIds,
      });

      this.populateLocationForms();
      this.openAddEditModal();
    } else {
      console.log("Character cannot be found!");
    }
  }

  mapSpeciesToUi(species: Species): string {
    return (String(species).charAt(0) + String(species.slice(1).toLowerCase())).replace("_", " ");
  }

  mapSpeciesFromUi(species: string): Species {
    return species.toUpperCase().replace(" ", "_") as Species;
  }

  mapSexToUi(sex: Sex): string {
    if (sex === "N_A") {
      return "N/A";
    } else {
      return String(sex).charAt(0) + String(sex.slice(1).toLowerCase());
    }
  }

  mapSexFromUi(sex: string): Sex {
    return sex.toUpperCase().replace("/", "_") as Sex;
  }

  scrollToCharacter() {
    setTimeout(() => {
      const characterRowId = `entry-${this.fragment}`;
      const characterRow = document.getElementById(characterRowId);

      if (characterRow) {
        characterRow.scrollIntoView({ behavior: "smooth", block: "center" });
        characterRow.classList.add("highlight-row");
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
        speciesFilter: [],
        sexFilter: [],
        sagaVersionFilter: [],
      };
    });

    this.$searchForm.searchTerm().value.set("");
    this.$currentPage.set(1);
  }

  toggleSpeciesFilter(species: string) {
    this.$filters.update((current) => {
      const speciesFilter = current.speciesFilter.includes(species)
        ? current.speciesFilter.filter((speciesInFilter) => speciesInFilter !== species)
        : [...current.speciesFilter, species];

      this.$currentPage.set(1);

      return { ...current, speciesFilter };
    });
  }

  toggleSexFilter(sex: string) {
    this.$filters.update((current) => {
      const sexFilter = current.sexFilter.includes(sex)
        ? current.sexFilter.filter((sexInFilter) => sexInFilter !== sex)
        : [...current.sexFilter, sex];

      this.$currentPage.set(1);

      return { ...current, sexFilter };
    });
  }

  speciesCheckboxChecked(species: string) {
    return this.$filters().speciesFilter.includes(species);
  }

  sexCheckboxChecked(sex: string) {
    return this.$filters().sexFilter.includes(sex);
  }

  clearSpeciesFilter() {
    this.$filters.update((current) => ({
      ...current,
      speciesFilter: [],
    }));

    this.$currentPage.set(1);
  }

  clearSexFilter() {
    this.$filters.update((current) => ({
      ...current,
      sexFilter: [],
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

  initialiseCharacter(): ICharacter {
    return {
      id: null,
      name: "",
      species: Species.MISC_HUMANOID,
      sex: Sex.N_A,
      notes: "",
      characterLocationDtos: [],
      sagaVersionIds: [],
    };
  }

  populateLocationForms() {
    this.locationForms.clear();
    this.locations.forEach((loc) => {
      const locationForm = this.createLocationForm(loc);
      this.configureLocationForm(locationForm);
      this.locationForms.push(locationForm);

      this.updateLocationFormFilter("");
    });
  }

  createLocationForm(location: ILocationName): FormGroup {
    const locAssignedToChar = this.character.characterLocationDtos.find((loc) => loc.locationId == location.id);
    const selected = !!locAssignedToChar;

    return new FormGroup({
      trackingId: new FormControl<number>({ value: this.charTrackingId++, disabled: true }),
      locationId: new FormControl<number | null>({ value: location.id, disabled: true }),
      name: new FormControl<string>({ value: location.name, disabled: true }),
      significantAssociation: new FormControl<boolean>(
        { value: locAssignedToChar ? locAssignedToChar.significantAssociation : false, disabled: !selected },
        Validators.required,
      ),
      selected: new FormControl<boolean>(selected),
    });
  }

  configureLocationForm(form: FormGroup) {
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

  updateLocationFormFilter(searchTerm: string) {
    this.filteredLocations = this.locationForms.controls
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
    var addEditModal = document.getElementById("addEditCharacter");
    if (addEditModal != null) {
      var modal = Modal.getOrCreateInstance(addEditModal);
      if (modal != null) {
        modal.show();
      }
    }
  }

  closeAddEditModal() {
    var addEditModal = document.getElementById("addEditCharacter");
    if (addEditModal != null) {
      var modal = Modal.getInstance(addEditModal);
      if (modal != null) {
        modal.hide();
      }
    }
  }

  openDeleteModal(id: number) {
    this.characterToDelete = id;
    var deleteModal = document.getElementById("deleteCharacter");
    if (deleteModal != null) {
      var modal = Modal.getOrCreateInstance(deleteModal);
      modal?.show();
    }
  }

  closeDeleteModal() {
    var deleteModal = document.getElementById("deleteCharacter");
    if (deleteModal != null) {
      var modal = Modal.getInstance(deleteModal);
      modal?.hide();
    }
  }

  hideAccordion() {
    const accordions = document.querySelectorAll("#addEditCharacter .accordion-collapse");

    accordions.forEach((element) => {
      const accordionInstance = Collapse.getOrCreateInstance(element, { toggle: false });
      if (accordionInstance) accordionInstance.hide();
    });
  }

  submitAddOrEdit() {
    this.name.updateValueAndValidity();
    this.species.updateValueAndValidity();
    this.sex.updateValueAndValidity();

    if (this.editForm.valid) {
      this.character.name = this.name.value;
      this.character.species = this.mapSpeciesFromUi(this.species.value);
      this.character.sex = this.mapSexFromUi(this.sex.value);
      this.character.notes = this.notes.value;
      this.character.sagaVersionIds = this.sagaVersionIds.value;

      const locationFormsRaw = this.locationForms.getRawValue();

      this.character.characterLocationDtos = locationFormsRaw
        .filter((form) => form["selected"])
        .map((form) => ({
          locationId: form["locationId"],
          significantAssociation: form["significantAssociation"],
        }));

      if (this.mode === Mode.ADD) {
        this.postCharacter();
      } else if (this.mode === Mode.EDIT) {
        this.updateCharacter();
      }
    } else {
      this.showValidationErrors = true;
    }
  }

  async getCharacters() {
    const characters = await firstValueFrom(this.charactersService.getCharacters()).catch((err) => {
      console.log("Error fetching characters: " + JSON.stringify(err));
    });

    if (characters) {
      this.$characters.set(characters.sort((a, b) => a.name.localeCompare(b.name)));
    }
  }

  async getLocationNames() {
    const locations = await firstValueFrom(this.locationsService.getLocationNames()).catch((err) => {
      console.log("Error fetching characters: " + JSON.stringify(err));
    });

    if (locations) {
      this.locations = locations.sort((a, b) => a.name.localeCompare(b.name));
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

  postCharacter() {
    this.loading = true;
    this.charactersService.postCharacter(this.character).subscribe({
      next: (character) => {
        this.loading = false;
        this.closeAddEditModal();
        console.log("Saved successfully: " + JSON.stringify(character));
        if (!character.id) return;

        this.$characters.update((current) => {
          const next = [...current];
          next.push(character);
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

  updateCharacter() {
    this.loading = true;
    this.charactersService.putCharacter(this.character).subscribe({
      next: (character) => {
        this.loading = false;
        this.closeAddEditModal();
        console.log("Updated successfully: " + JSON.stringify(character));
        if (!character.id) return;

        this.$characters.update((current) => {
          let next = [...current];
          const characterIndex = next.findIndex((loc) => loc.id === character.id);
          if (characterIndex !== -1) {
            next[characterIndex] = character;
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

  deleteCharacter() {
    this.closeDeleteModal();

    this.charactersService.deleteCharacter(this.characterToDelete).subscribe({
      next: () => {
        const deletedCharacterIndex = this.$characters().findIndex((loc) => loc.id === this.characterToDelete);
        if (deletedCharacterIndex >= 0) {
          this.$characters.update((current) => {
            const next = [...current];
            next.splice(deletedCharacterIndex, 1);
            return next;
          });
        }
      },
      error: (err) => console.log("problem with deleting character: " + JSON.stringify(err)),
    });
  }

  // characterUnique(): ValidatorFn {
  //   return (control: AbstractControl): ValidationErrors | null => {
  //     const name = String(control.value ?? "")
  //       .trim()
  //       .toLowerCase();

  //     if (!name) {
  //       return null;
  //     }

  //     const duplicate = this.$characters().find(
  //       (loc) => loc.name.trim().toLowerCase() === name && loc.id !== control.parent?.get("id")?.value,
  //     );

  //     return duplicate ? { characterNotUnique: true } : null;
  //   };
  // }

  speciesNotSelected(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return null;
      }

      if (value === this.selectASpeciesText) {
        return { speciesNotSelected: true };
      } else {
        return null;
      }
    };
  }

  sexNotSelected(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return null;
      }

      if (value === this.selectASexText) {
        return { sexNotSelected: true };
      } else {
        return null;
      }
    };
  }
}
