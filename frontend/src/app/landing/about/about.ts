import { Component, computed, inject } from "@angular/core";
import { PageHeader } from "../../page-header/page-header";
import { IText } from "../common/IText";
import { LandingService } from "../common/landing.service";
import { FormControl, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { QuillModule } from "ngx-quill";
import { Modal } from "bootstrap";
import { AuthService } from "../../auth/auth.service";

@Component({
  selector: "app-about",
  imports: [PageHeader, QuillModule, ReactiveFormsModule],
  templateUrl: "./about.html",
  styleUrl: "./about.css",
})
export class About {
  private landingService = inject(LandingService);
  private authService = inject(AuthService);

  editForm = new FormGroup({
    text: new FormControl<string>("", { nonNullable: true }),
  });

  aboutText: IText = {
    text: "",
  };

  $isAdmin = computed(() => this.authService.$isAdmin());

  get text(): FormControl<string> {
    return this.editForm.controls.text;
  }

  ngOnInit() {
    this.landingService.getAboutText().subscribe({
      next: (receivedText) => {
        this.aboutText = { ...receivedText };
      },
      error: (err) => {
        console.log("Error getting text: " + JSON.stringify(err));
      },
    });
  }

  openEditModal() {
    this.text.setValue(this.aboutText.text);

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

    this.landingService.saveAboutText(textToSave).subscribe({
      next: (data) => {
        this.aboutText = { ...data };
        this.text.setValue(this.aboutText.text);
        this.closeEditModal();
      },
      error: (err) => {
        console.log("Problem saving text: " + JSON.stringify(err));
      },
    });
  }
}
