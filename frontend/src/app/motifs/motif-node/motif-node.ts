import { Component, ChangeDetectionStrategy, inject, input, computed } from "@angular/core";
import { MotifStore } from "../common/motif.store";
import { MotifModalService } from "../common/motif-modal.service";
import { RouterLink } from "@angular/router";
import { AuthService } from "../../auth/auth.service";

@Component({
  selector: "app-motif-node",
  imports: [RouterLink],
  templateUrl: "./motif-node.html",
  styleUrl: "./motif-node.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MotifNode {
  motifStore = inject(MotifStore);
  motifModalService = inject(MotifModalService);
  authService = inject(AuthService);

  //Inputs
  $nodeId = input.required<number>();
  $depth = input.required<number>();

  //Main node signal
  $node = computed(() => this.motifStore.$motifNodes().get(this.$nodeId()));

  //Booleans
  $expanded = computed(() => this.motifStore.$expandedNodes().has(this.$nodeId()));
  $visible = computed(() => this.motifStore.$visibleNodes().has(this.$nodeId()));
  $result = computed(() => this.motifStore.$resultNodes().has(this.$nodeId()));

  $pinnedNode = computed(() => this.motifStore.$pinnedNodes().has(this.$nodeId()));

  //UI signals
  $searchActive = computed(() => this.motifStore.$searchActive());
  $searchTerm = computed(() => this.motifStore.$searchTerm());
  $showColourCoding = computed(() => this.motifStore.$showColourCoding());
  $expandLoading = computed(() => this.motifStore.$expandLoading());

  //Auth signals
  $loggedIn = computed(() => this.authService.$loggedIn());
  $isAdmin = computed(() => this.authService.$isAdmin());

  //Sagas
  $sagas = computed(() => this.motifStore.$sagaTitles());
  $assignedSagas = computed(() => {
    const node = this.$node();
    if (!node) return;

    const sagaVersions = [];

    for (const sagaMotif of node.motifSagas) {
      const saga = this.$sagas().find((saga) => saga.id === sagaMotif.sagaVersionId);
      if (saga && (this.$loggedIn() || saga.published)) {
        sagaVersions.push({
          sagaVersionId: sagaMotif.sagaVersionId,
          sagaTitle: saga.title,
          sagaAbbreviation: saga.abbreviation,
          published: saga.published,
          sagaId: saga.sagaId,
          pageChapterNumber: sagaMotif.pageChapterNumber,
          inBoberg: sagaMotif.inBoberg,
        });
      }
    }

    return sagaVersions.sort((a, b) => a.sagaAbbreviation.localeCompare(b.sagaAbbreviation));
  });

  nodeColour: string = "";

  //Functions
  ngOnInit() {
    this.setBackgroundColour();
  }

  toggleExpand() {
    if (this.$expanded()) {
      this.motifStore.collapse(this.$nodeId());
      console.log("COLLAPSING");
      return;
    }

    this.motifStore.expand(this.$nodeId());
    console.log("EXPANDING");

    const node = this.$node();

    //If node has at least one child AND no children have been assigned OR the amount of 
    //assigned children is less than the total amount, then assign all children.
    if (node?.childCount && (!node?.childIds || node?.childIds.length < node?.childCount)){
      this.motifStore.getMotifChildren(this.$nodeId());
    }
  }

  togglePinnedNode() {
    this.motifStore.togglePinnedNode(this.$nodeId());
  }

  openAddModal() {
    this.motifModalService.openAddModal(this.$nodeId());
  }

  openEditModal() {
    this.motifModalService.openEditModal(this.$nodeId());
  }

  openDeleteModal() {
    this.motifModalService.openDeleteModal(this.$nodeId());
  }

  resetModal() {
    this.motifModalService.closeModal();
  }

  setBackgroundColour() {
    switch (this.$depth()) {
      case 1:
        this.nodeColour = "rgb(255, 213, 213)";
        break;
      case 2:
        this.nodeColour = "rgb(255, 235, 213)";
        break;
      case 3:
        this.nodeColour = "rgb(251, 255, 213)";
        break;
      case 4:
        this.nodeColour = "rgb(221, 255, 213)";
        break;
      case 5:
        this.nodeColour = "rgb(213, 248, 255)";
        break;
      case 6:
        this.nodeColour = "rgb(219, 213, 255)";
        break;
      case 7:
        this.nodeColour = "rgb(255, 213, 252)";
        break;
      case 8:
        this.nodeColour = "rgb(255, 213, 213)";
        break;
      case 9:
        this.nodeColour = "rgb(255, 235, 213)";
        break;
      default:
        return;
    }
  }

  highlightText(textToHighlight: string | undefined) {
    if (textToHighlight && textToHighlight.length > 0) {
      const escapedTerm = this.$searchTerm().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
      var re = new RegExp(escapedTerm, "gi");
      return textToHighlight.replace(re, (match) => `<mark>${match}</mark>`);
    } else {
      return "";
    }
  }

  highlightAbbreviation(textToHighlight: string | undefined) {

    if (textToHighlight && textToHighlight.length > 0) {
      const escapedTerm = this.$searchTerm().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
      var re = new RegExp(escapedTerm, "gi");

      //First look for a match with the abbreviation itself
      if (textToHighlight.match(re)) {
        return textToHighlight.replace(re, (match) => `<mark>${match}</mark>`);
      }
     else {
      //Then look for a match with the saga title
      const match = this.$assignedSagas()?.some((saga) => 
        saga.sagaTitle.toLowerCase().includes(this.$searchTerm().toLowerCase()));
      if (match) {
        return `<mark>${textToHighlight}</mark>`;
      } 
    }
  }
    return textToHighlight;
  }
}
