import { ComponentFixture, TestBed } from "@angular/core/testing";
import { of } from "rxjs";
import { provideRouter } from "@angular/router";
import { MsAll } from "./ms-all";
import { MsService } from "../common/ms.service";
import { IMsRepositoryDto } from "../common/IMsRepositoryDto";
import { IMs } from "../common/IMs";
import { signal } from "@angular/core";
import { AuthService } from "../../auth/auth.service";

describe("MsAll", () => {
  let component: MsAll;
  let fixture: ComponentFixture<MsAll>;

  let authServiceMock = {
    $loggedIn: signal(true),
    $isAdmin: signal(true),
  };

  let repo: IMsRepositoryDto = {
    id: 1,
    name: "Repository",
    city: "Cambridge",
    country: "United Kingdom",
    msIds: [1],
  };

  let emptyRepo: IMsRepositoryDto = {
    id: 2,
    name: "Empty repository",
    city: "Cambridge",
    country: "United Kingdom",
    msIds: [],
  };

  let repos: IMsRepositoryDto[] = [repo, emptyRepo];

  let ms: IMs = {
    id: 1,
    name: null,
    shelfmark: "Shelfmark",
    date: "1500",
    description: "",
    msSagaDtos: [],
    msRepositoryId: 1,
  };

  let mss: IMs[] = [ms];

  beforeEach(async () => {
    const msServiceSpy = jasmine.createSpyObj<MsService>([
      "getMsRepositories",
      "postMsRepository",
      "putMsRepository",
      "deleteMsRepository",
      "getMsEntries",
    ]);
    msServiceSpy.getMsRepositories.and.returnValue(of(repos));
    msServiceSpy.postMsRepository.and.returnValue(of(repo));
    msServiceSpy.putMsRepository.and.returnValue(of(repo));
    msServiceSpy.deleteMsRepository.and.returnValue(of(null));
    msServiceSpy.getMsEntries.and.returnValue(of(mss));

    authServiceMock.$isAdmin.set(true);
    authServiceMock.$loggedIn.set(true);

    await TestBed.configureTestingModule({
      imports: [MsAll],
      providers: [
        {
          provide: MsService,
          useValue: msServiceSpy,
        },
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MsAll);
    component = fixture.componentInstance;
    //The first time this is run it will execute the constructor and run ngOnInit().
    fixture.detectChanges();
  });

  it("Component should be defined", () => {
    expect(component).toBeDefined();
  });

  it("Init fetches MS repositories", () => {
    const msService = TestBed.inject(MsService) as jasmine.SpyObj<MsService>;

    expect(msService.getMsRepositories).toHaveBeenCalledTimes(1);
    expect(msService.getMsEntries).toHaveBeenCalledTimes(1);
    expect(component.repositoriesVm.length).toBeTruthy();
    expect(component.mss.length).toBeTruthy();
  });

  it("Buttons hidden to unauthorised users", () => {
    authServiceMock.$isAdmin.set(false);
    authServiceMock.$loggedIn.set(false);

    fixture.detectChanges();

    const addRepoButton = fixture.nativeElement.querySelector('[data-testid="add-repo-button"]');
    const editRepoButton = fixture.nativeElement.querySelector('[data-testid="edit-repo-button-0"]');
    const deleteRepoButton = fixture.nativeElement.querySelector('[data-testid="delete-repo-button-0"]');
    const addMsButton = fixture.nativeElement.querySelector('[data-testid="add-ms-button-0"]');

    expect(addRepoButton).toBeNull();
    expect(editRepoButton).toBeNull();
    expect(deleteRepoButton).toBeNull();
    expect(addMsButton).toBeNull();
  });

  it("Buttons shown to authorised users", () => {
    authServiceMock.$isAdmin.set(true);
    authServiceMock.$loggedIn.set(true);

    fixture.detectChanges();

    const addRepoButton = fixture.nativeElement.querySelector('[data-testid="add-repo-button"]');
    const editRepoButton = fixture.nativeElement.querySelector('[data-testid="edit-repo-button-0"]');
    const deleteEmptyRepoButton = fixture.nativeElement.querySelector('[data-testid="delete-repo-button-0"]');
    const deleteFilledRepoButton = fixture.nativeElement.querySelector('[data-testid="delete-repo-button-1"]');
    const addMsButton = fixture.nativeElement.querySelector('[data-testid="add-ms-button-0"]');

    expect(addRepoButton).not.toBeNull();
    expect(editRepoButton).not.toBeNull();
    expect(deleteEmptyRepoButton).not.toBeNull();
    expect(deleteFilledRepoButton).not.toBeNull();
    expect(addMsButton).not.toBeNull();
  });

  it("Contributors cannot delete filled repository", () => {
    authServiceMock.$isAdmin.set(false);
    authServiceMock.$loggedIn.set(true);

    fixture.detectChanges();

    //Repo at index 1 has a MS, and so shouldn't be deletable for non-admins
    const deleteRepoButton = fixture.nativeElement.querySelector('[data-testid="delete-repo-button-1"]');

    expect(deleteRepoButton).toBeNull();
  });

  it("Contributors/admin can post MS repository", () => {
    spyOn(component, "openAddEditModal");
    spyOn(component, "closeAddEditModal");
    spyOn(component, "submitAddOrEdit").and.callThrough();

    const msService = TestBed.inject(MsService) as jasmine.SpyObj<MsService>;

    const addRepoButton = fixture.nativeElement.querySelector('[data-testid="add-repo-button"]');
    addRepoButton.click();
    fixture.detectChanges();

    expect(component.openAddEditModal).toHaveBeenCalled();

    component.editForm.controls.name.setValue("New repo");
    component.editForm.controls.city.setValue("City");
    component.editForm.controls.country.setValue("Country");

    const saveButton = fixture.nativeElement.querySelector('[data-testid="save-button"]');
    saveButton.click();
    fixture.detectChanges();

    expect(component.editForm.valid).toBeTrue();
    expect(component.submitAddOrEdit).toHaveBeenCalledTimes(1);
    expect(component.closeAddEditModal).toHaveBeenCalledTimes(1);
    expect(msService.postMsRepository).toHaveBeenCalledOnceWith(
      jasmine.objectContaining({
        name: "New repo",
        city: "City",
        country: "Country",
      }),
    );
  });

  it("Contributors/admin can edit MS repository", () => {
    spyOn(component, "openAddEditModal");
    spyOn(component, "closeAddEditModal");
    spyOn(component, "submitAddOrEdit").and.callThrough();

    const msService = TestBed.inject(MsService) as jasmine.SpyObj<MsService>;

    const editRepoButton = fixture.nativeElement.querySelector('[data-testid="edit-repo-button-0"]');
    editRepoButton.click();
    fixture.detectChanges();

    expect(component.openAddEditModal).toHaveBeenCalled();

    component.editForm.controls.name.setValue("Edited repo name");

    const saveButton = fixture.nativeElement.querySelector('[data-testid="save-button"]');
    saveButton.click();
    fixture.detectChanges();

    expect(component.editForm.valid).toBeTrue();
    expect(component.submitAddOrEdit).toHaveBeenCalledTimes(1);
    expect(component.closeAddEditModal).toHaveBeenCalledTimes(1);
    expect(msService.putMsRepository).toHaveBeenCalledOnceWith(
      jasmine.objectContaining({
        name: "Edited repo name",
        city: "Cambridge",
        country: "United Kingdom",
      }),
    );
  });

  it("Contributors/admin can delete MS repository", () => {
    spyOn(component, "openDeleteModal");
    spyOn(component, "closeDeleteModal");
    spyOn(component, "deleteRepository").and.callThrough();
    spyOn(component, "deleteMsRepository").and.callThrough();

    const msService = TestBed.inject(MsService) as jasmine.SpyObj<MsService>;

    const deleteRepoButton = fixture.nativeElement.querySelector('[data-testid="delete-repo-button-0"]');
    deleteRepoButton.click();
    fixture.detectChanges();

    expect(component.openDeleteModal).toHaveBeenCalled();

    const deleteConfirmButton = fixture.nativeElement.querySelector('[data-testid="delete-confirm-button"]');
    deleteConfirmButton.click();
    fixture.detectChanges();

    expect(component.closeDeleteModal).toHaveBeenCalledTimes(1);
    expect(component.deleteRepository).toHaveBeenCalledTimes(1);
    expect(component.deleteMsRepository).toHaveBeenCalledTimes(1);
    expect(msService.deleteMsRepository).toHaveBeenCalledOnceWith(0);
  });

  it("Contributors can add manuscripts to repositories", () => {

    spyOn(component, "addManuscript");

    const addMsButton = fixture.nativeElement.querySelector('[data-testid="add-ms-button-0"]');
    addMsButton.click();
    fixture.detectChanges();

    expect(component.addManuscript).toHaveBeenCalledTimes(1);
  });
});
