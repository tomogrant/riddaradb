import { Component, computed, inject } from "@angular/core";
import { PageHeader } from "../../page-header/page-header";
import { AuthService } from "../../auth/auth.service";
import { LandingService } from "../common/landing.service";
import { FormControl, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { IText } from "../common/IText";
import { Modal } from "bootstrap";
import { QuillModule } from "ngx-quill";

@Component({
  selector: "app-home",
  imports: [PageHeader, QuillModule, ReactiveFormsModule],
  templateUrl: "./home.html",
  styleUrl: "./home.css",
})
export class Home {
  private landingService = inject(LandingService);
  private authService = inject(AuthService);

  editForm = new FormGroup({
    text: new FormControl<string>("", { nonNullable: true }),
  });

  homeText: IText = {
    text: "",
  };

  $isAdmin = computed(() => this.authService.$isAdmin());

  get text(): FormControl<string> {
    return this.editForm.controls.text;
  }

  ngOnInit() {
    this.landingService.getHomeText().subscribe({
      next: (receivedText) => {
        this.homeText = { ...receivedText };
      },
      error: (err) => {
        console.log("Error getting text: " + JSON.stringify(err));
      },
    });
  }

  openEditModal() {
    this.text.setValue(this.homeText.text);

    var editModal = document.getElementById("editText");
    if (editModal != null) {
      var modal = Modal.getOrCreateInstance(editModal);
      if (modal != null) {
        modal.show();
      }
    }
  }

  closeEditModal() {
    var editModal = document.getElementById("editText");
    if (editModal != null) {
      var modal = Modal.getOrCreateInstance(editModal);
      if (modal != null) {
        modal.hide();
      }
    }
  }

  saveText() {
    const textToSave: IText = {
      text: this.text.value.replaceAll(/((?:&nbsp;)*)&nbsp;/g, "$1 ")
    };

    this.landingService.saveHomeText(textToSave).subscribe({
      next: (data) => {
        this.homeText = { ...data };
        this.text.setValue(this.homeText.text);
        this.closeEditModal();
      },
      error: (err) => {
        console.log("Problem saving text: " + JSON.stringify(err));
      },
    });
  }
}
