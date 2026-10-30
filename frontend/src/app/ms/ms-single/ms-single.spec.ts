import { TestBed } from "@angular/core/testing";
import { ActivatedRoute, convertToParamMap } from "@angular/router";
import { of } from "rxjs";
import { SagaService } from "../../sagas/common/saga.service";
import { ISagaTitleDto } from "../../sagas/common/ISagaTitleDto";
import { AuthService } from "../../auth/auth.service";
import { signal } from "@angular/core";
import { MsService } from "../common/ms.service";
import { IMs } from "../common/IMs";
import { MsSingle } from "./ms-single";
import { IMsRepositoryDto } from "../common/IMsRepositoryDto";

fdescribe("MsSingle", () => {
  let activatedRouteMock: any;

  let authServiceMock = {
    $isAdmin: signal(true),
    $loggedIn: signal(true),
  };

  let repo: IMsRepositoryDto = {
    id: 1,
    name: "Repo name",
    city: "City",
    country: "Country",
    msIds: []
  }

  let ms: IMs = {
    id: 1,
    name: null,
    shelfmark: "Shelfmark",
    date: "1600",
    description: "",
    msSagaDtos: [],
    msRepositoryId: 1
  }

  let sagaTitle: ISagaTitleDto = {
    id: 1,
    title: "Saga title",
    published: true
  }

  let sagaTitles: ISagaTitleDto[] = [sagaTitle];

  //Executed before each it() test
  beforeEach(async () => {
    //Sets up the activated route to be provided to the TestBed
    activatedRouteMock = {
      snapshot: {
        paramMap: convertToParamMap({}),
      },
    };

    //Initialisations
    activatedRouteMock.snapshot.paramMap = convertToParamMap({ id: "1" });
    authServiceMock.$loggedIn.set(true);
    authServiceMock.$isAdmin.set(true);

    //Sets up the mock BibService instance to be passed to the TestBed
    const msServiceSpy = jasmine.createSpyObj<MsService>(["getMsEntryById", "postMs", "putMs", "deleteMs", "getMsRepositoryById"]);
    //When the bib service's getBibEntryById function is called, return the bib entry defined above
    msServiceSpy.getMsEntryById.and.returnValue(of(ms));
    msServiceSpy.postMs.and.returnValue(of(ms));
    msServiceSpy.putMs.and.returnValue(of(ms));
    msServiceSpy.deleteMs.and.returnValue(of(null));
    msServiceSpy.getMsRepositoryById.and.returnValue(of(repo));

    //Sets up the mock SagaService instance to be passed to the TestBed
    const sagaServiceSpy = jasmine.createSpyObj<SagaService>(["getSagaTitles"]);
    //When the saga service's getSagaTitles function is called, return the list of sagas defined above
    sagaServiceSpy.getSagaTitles.and.returnValue(of(sagaTitles));

    await TestBed.configureTestingModule({
      imports: [MsSingle],
      //When MsSingle accesses these classes, spies are provided instead
      providers: [
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        {
          provide: MsService,
          useValue: msServiceSpy,
        },
        {
          provide: SagaService,
          useValue: sagaServiceSpy,
        },
        {
          provide: ActivatedRoute,
          useValue: activatedRouteMock,
        },
      ],
    }).compileComponents();
  });

  it("ngOnInit should return bibliography entries when not in add mode", () => {
    const fixture = TestBed.createComponent(MsSingle);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.activeMs).toEqual(ms);
    expect(component.sagas.length).toBeTruthy();
  });

  ["1.5", "", "1 ", "0", "abc", undefined].forEach((invalidParam) => {
    it(`Invalid parameter ${invalidParam} should not fetch saga`, () => {
      activatedRouteMock.snapshot.paramMap = convertToParamMap({ id: invalidParam });
      const fixture = TestBed.createComponent(MsSingle);
      const component = fixture.componentInstance;

      const bibService = TestBed.inject(MsService) as jasmine.SpyObj<MsService>;
      spyOn(component, "navigateToMsAllPage");

      fixture.detectChanges();

      expect(component.navigateToMsAllPage).toHaveBeenCalled();
      expect(bibService.getMsEntryById).not.toHaveBeenCalled();
    });
  });

  it("Buttons are unavailable to unauthenticated users", () => {
    authServiceMock.$loggedIn.set(false);

    const fixture = TestBed.createComponent(MsSingle);
    fixture.detectChanges();

    const editButton = fixture.nativeElement.querySelector('[data-testid="edit-button"]');
    const deleteButton = fixture.nativeElement.querySelector('[data-testid="delete-button"]');

    expect(editButton).toBeNull();
    expect(deleteButton).toBeNull();
  });

  it("Buttons are available to all authenticated users", () => {
    const fixture = TestBed.createComponent(MsSingle);
    fixture.detectChanges();

    const editButton = fixture.nativeElement.querySelector('[data-testid="edit-button"]');
    const deleteButton = fixture.nativeElement.querySelector('[data-testid="delete-button"]');

    expect(editButton).not.toBeNull();
    expect(deleteButton).not.toBeNull();
  });

  it("Add modal posts ms entry", () => {
    activatedRouteMock.snapshot.paramMap = convertToParamMap({ mode: "add" });
    const fixture = TestBed.createComponent(MsSingle);
    const component = fixture.componentInstance;
    const msService = TestBed.inject(MsService) as jasmine.SpyObj<MsService>;
    //We don't want the add modal to actually open
    spyOn(component, "openAddEditModal");
    spyOn(component, "closeAddEditModal");

    fixture.detectChanges();

    component.shelfmark.setValue("New shelfmark");
    component.date.setValue("1500");

    const saveButton = fixture.nativeElement.querySelector('[data-testid="save-button"]');
    saveButton.click();
    fixture.detectChanges();

    expect(component.activeMs).toBeTruthy();
    expect(component.editForm.valid).toBeTrue();
    expect(component.openAddEditModal).toHaveBeenCalledTimes(1);
    expect(component.closeAddEditModal).toHaveBeenCalledTimes(1);
    expect(msService.postMs).toHaveBeenCalledTimes(1);
    expect(msService.postMs).toHaveBeenCalledWith(
      jasmine.objectContaining({
        id: null,
        shelfmark: "New shelfmark",
        date: "1500"
      }),
    );
  });

  it("Edit modal populates and updates bib entry", () => {
    const fixture = TestBed.createComponent(MsSingle);
    const component = fixture.componentInstance;
    const msService = TestBed.inject(MsService) as jasmine.SpyObj<MsService>;
    spyOn(component, "openAddEditModal");
    spyOn(component, "closeAddEditModal");
    //Runs ngOnInit
    fixture.detectChanges();

    //Gets button by data-testid, clicks button, and triggers change detection which runs
    //this.type.valueChanges.pipe().
    const editButton = fixture.nativeElement.querySelector('[data-testid="edit-button"]');
    editButton.click();
    fixture.detectChanges();

    component.shelfmark.setValue("Edited shelfmark");

    //Clicks the save button
    const saveButton = fixture.nativeElement.querySelector('[data-testid="save-button"]');
    saveButton.click();
    fixture.detectChanges();

    //Expects edit form to be valid, bib service to have updated entry and to have
    //returned the updated bib object
    expect(component.editForm.valid).toBeTrue();
    expect(component.openAddEditModal).toHaveBeenCalled();
    expect(component.closeAddEditModal).toHaveBeenCalled();
    expect(msService.putMs).toHaveBeenCalledTimes(1);
    expect(msService.putMs).toHaveBeenCalledWith(
      jasmine.objectContaining({
        shelfmark: "Edited shelfmark"
      }),
    );
  });

  it("Delete modal deletes bib entry", () => {
    const fixture = TestBed.createComponent(MsSingle);
    const component = fixture.componentInstance;
    const msService = TestBed.inject(MsService) as jasmine.SpyObj<MsService>;
    spyOn(component, "openDeleteModal");
    spyOn(component, "closeDeleteModal");

    fixture.detectChanges();

    const deleteButton = fixture.nativeElement.querySelector('[data-testid="delete-button"]');
    const deleteConfirmButton = fixture.nativeElement.querySelector('[data-testid="delete-confirm-button"]');

    deleteButton.click();
    fixture.detectChanges();

    deleteConfirmButton.click();
    fixture.detectChanges();

    expect(component.openDeleteModal).toHaveBeenCalled();
    expect(component.closeDeleteModal).toHaveBeenCalled();
    expect(msService.deleteMs).toHaveBeenCalledTimes(1);
    expect(msService.deleteMs).toHaveBeenCalledWith(1);
  });
});
