import { Injectable, inject, signal, computed } from "@angular/core";
import { IMotif } from "./IMotif";
import { firstValueFrom, lastValueFrom } from "rxjs";
import { MotifService } from "./motif.service";
import { SagaService } from "../../sagas/common/saga.service";
import { ISagaVersionTitleDto } from "../../sagas/common/ISagaVersionTitleDto";
import { IMotifSearchResult } from "./IMotifSearchResult";
import { HttpErrorResponse } from "@angular/common/http";
import { MotifModalService } from "./motif-modal.service";

@Injectable({
  providedIn: "root",
})
export class MotifStore {
  motifService = inject(MotifService);
  motifModalService = inject(MotifModalService);
  sagaService = inject(SagaService);

  $sagaTitles = signal<Array<ISagaVersionTitleDto>>(new Array<ISagaVersionTitleDto>());

  //Motifs without parents; entry-points into the trees
  $rootIds = signal<Set<number>>(new Set<number>());

  //Normalised store of motifs
  $motifNodes = signal<Map<number, IMotif>>(new Map<number, IMotif>());

  $pinnedNodes = signal<Map<number, IMotif>>(new Map<number, IMotif>());

  //Sets to control which nodes are displayed
  $expandedNodes = signal<Set<number>>(new Set<number>());
  $visibleNodes = signal<Set<number>>(new Set<number>());
  $resultNodes = signal<Set<number>>(new Set<number>());

  //UI state
  $searchActive = signal<boolean>(false);
  $searchTerm = signal<string>("");
  $searchMessage = signal<string>("");
  $showColourCoding = signal<boolean>(false);

  $initialising = signal<boolean>(false);
  $requestLoading = signal<boolean>(false);
  $searchLoading = signal<boolean>(false);
  $expandLoading = signal<boolean>(false);

  $storeInitialised = signal<boolean>(false);

  async initialise() {
    this.collapseAll();
    this.clearVisibleNodes();
    this.clearResultNodes();
    this.$searchActive.set(false);
    this.$searchTerm.set("");
    this.$searchMessage.set("");
    this.$showColourCoding.set(false);

    if (!this.$storeInitialised()) {
      this.$initialising.set(true);

      await this.getRootMotifs();
      await this.getSagaTitles();

      this.$pinnedNodes.set(await this.retrievePinnedNodesFromLocalStorage());

      this.$initialising.set(false);

      this.$storeInitialised.set(true);
    }
  }

  //Gets pinned node IDs from local storage and retrieves them from the database.
  //Nodes are placed in the $pinnedNodes map.
  async retrievePinnedNodesFromLocalStorage(): Promise<Map<number, IMotif>> {
    const raw = localStorage.getItem("pinnedNodes");
    const nodeIds: number[] = raw ? JSON.parse(raw) : [];

    const pinnedNodes = await firstValueFrom(this.motifService.getMotifsByIds(nodeIds));

    //1. Type predicate filters for IMotifs with numeric + non-null IDs
    //2. Sorts IMotifs according to IDs in local storage (we don't want the order changing)
    //3. Creates a map from the filtered, sorted IMotifs.
    return new Map(
      pinnedNodes
        .filter((pinnedNode): pinnedNode is IMotif & { id: number } => pinnedNode.id !== null)
        .sort((a, b) => nodeIds.indexOf(a.id!) - nodeIds.indexOf(b.id!))
        .map((pinnedNode) => [pinnedNode.id, pinnedNode]),
    );
  }

  savePinnedNodesToLocalStorage() {
    this.$pinnedNodes;

    localStorage.setItem("pinnedNodes", JSON.stringify([...this.$pinnedNodes().keys()]));
    console.log("Pinned nodes saved to local storage");
  }

  async getSagaTitles() {
    const sagas = await firstValueFrom(this.sagaService.getSagaVersionTitles());
    this.$sagaTitles.set(sagas.sort((a, b) => a.title.localeCompare(b.title)));
  }

  filterMap(map: Map<number, IMotif>): Map<number, IMotif> {
    return new Map([...map].sort((a, b) => a[1].motifCode.localeCompare(b[1].motifCode)));
  }

  clearSearch() {
    if (this.$searchActive()) {
      this.$searchActive.set(false);
      this.$searchTerm.set("");
      this.$searchMessage.set("");

      this.collapseAll();
      this.clearVisibleNodes();
      this.clearResultNodes();
    }
  }

  togglePinnedNode(nodeId: number) {
    this.$pinnedNodes.update((current) => {
      const next = new Map(current);
      const node = this.$motifNodes().get(nodeId);

      //Node is no longer in the motif store; remove from pinned nodes
      if (!node || !node.id) {
        next.delete(nodeId);
        return next;
      }

      //Node is in pinned nodes; remove
      if (next.has(node.id)) {
        next.delete(node.id);
      } else {
        //Node is not in pinned nodes; add
        next.set(node.id, node);
      }

      return next;
    });

    this.savePinnedNodesToLocalStorage();
  }

  //Moves pinned node up or down in map
  movePinnedNode(id: number, direction: number) {
    this.$pinnedNodes.update((current) => {
      const next = new Map(current);

      const pinnedNodesArray = [...next];
      const index = pinnedNodesArray.findIndex(([motifKey]) => motifKey === id);

      if (index < 0) {
        //Motif not found.
        return next;
      }

      const nodeToSwap = pinnedNodesArray[index];

      //Move motif up
      if (index < next.size && direction === 1) {
        pinnedNodesArray[index] = pinnedNodesArray[index + 1];
        pinnedNodesArray[index + 1] = nodeToSwap;
      }

      //Move motif down
      else if (index > 0 && direction === -1) {
        pinnedNodesArray[index] = pinnedNodesArray[index - 1];
        pinnedNodesArray[index - 1] = nodeToSwap;
      }

      return new Map(pinnedNodesArray);
    });

    this.savePinnedNodesToLocalStorage();
  }

  clearPinnedNodes() {
    this.$pinnedNodes.set(new Map());
    this.savePinnedNodesToLocalStorage();
  }

  editPinnedNode(node: IMotif) {
    this.$pinnedNodes.update((current) => {
      const next = new Map(current);

      if (next.get(node.id!)) {
        next.set(node.id!, node);
      }

      return next;
    });

    this.savePinnedNodesToLocalStorage();
  }

  removePinnedNode(id: number) {
    this.$pinnedNodes.update((current) => {
      const next = new Map(current);
      if (next.get(id)) {
        next.delete(id);
      }

      return next;
    });

    this.savePinnedNodesToLocalStorage();
  }

  toggleColourCoding() {
    this.$showColourCoding.set(!this.$showColourCoding());
  }

  async search(searchTerm: string, exactSearch: boolean) {
    this.$searchLoading.set(true);

    let results: IMotifSearchResult[] = [];

    try {
      //Search for paths to motif search results
      results = exactSearch
        ? await firstValueFrom(this.motifService.searchMotifsExactMatch(searchTerm))
        : await firstValueFrom(this.motifService.searchMotifs(searchTerm));
    } catch (err: unknown) {
      //If server returns a 400 error response, too many results returned
      if (err instanceof HttpErrorResponse && err.status === 400) {
        console.log(JSON.stringify(err));
        this.$searchLoading.set(false);
        this.$searchMessage.set("Too many results. Please refine your search.");
        return;
      }

      throw err;
    }

    if (!results.length) {
      this.$searchLoading.set(false);
      this.$searchMessage.set("No results found.");
      return;
    }

    this.$searchActive.set(true);
    this.$searchTerm.set(searchTerm);

    this.collapseAll();
    this.clearVisibleNodes();
    this.clearResultNodes();

    const motifsToFetch: Set<number> = new Set();
    results.forEach((result) => {
      if (!this.$motifNodes().has(result.searchResultId)) {
        motifsToFetch.add(result.searchResultId);
      }
      result.searchResultPath.forEach((id) => {
        if (!this.$motifNodes().has(id)) {
          motifsToFetch.add(id);
        }
      });
    });

    const searchResultMotifNodes = await firstValueFrom(this.motifService.getMotifsByIds(Array.from(motifsToFetch)));

    for (const result of results) {
      //Retrieves ancestors in
      this.insertAncestors(result.searchResultPath, searchResultMotifNodes);

      //Finally, makes the result node visible
      this.$visibleNodes.update((current) => {
        const next = new Set(current);
        next.add(result.searchResultId);
        return next;
      });

      this.$resultNodes.update((current) => {
        const next = new Set(current);
        next.add(result.searchResultId);
        return next;
      });
    }

    this.$searchMessage.set(`${results.length} results.`);
    this.$searchLoading.set(false);
  }

  insertAncestors(ancestorIds: number[], searchResultMotifNodes: IMotif[]) {
    for (const ancestorId of ancestorIds) {
      //Starting with the root note (oldest ancestor), load
      const node = this.getMotifNode(ancestorId);
      if (!node) {
        console.log("Node " + ancestorId + " not present in motif store!");
        return;
      }

      this.$expandedNodes.update((current) => {
        const next = new Set(current);
        next.add(ancestorId);
        return next;
      });

      this.$visibleNodes.update((current) => {
        const next = new Set(current);
        next.add(ancestorId);
        return next;
      });

      //If node has children,
      //1. retrieve these from the searchResultMotifNodes.
      //2. add them to $motifNodes if they have not been added already.
      //3. merge them into the parent's child nodes.
      if (node.childCount) {
        this.insertMotifChildren(ancestorId, searchResultMotifNodes);
      }
    }
  }

  insertMotifChildren(id: number, searchResultMotifNodes: IMotif[]) {
    //Loads, sorts and adds children to $motifNodes

    const children: IMotif[] = searchResultMotifNodes.filter((motif) => motif.parentId === id);

    this.$motifNodes.update((current) => {
      const next = new Map(current);

      if (!children.length) return next;

      const sortedChildren = children.sort((a, b) => a.motifCode.localeCompare(b.motifCode));

      //Add child node to $motifNodes if not present already
      for (var child of sortedChildren) {
        if (child.id == null) return next;
        if (!next.get(child.id)) {
          next.set(child.id, child);
        }
      }

      //Merge child node into parent's child nodes
      const parentMotif = next.get(id);

      if (parentMotif) {
        const existingChildIds = parentMotif.childIds ?? [];
        const fetchedChildIds = sortedChildren.map((child) => child.id).filter((childId) => childId != null);

        next.set(id, {
          ...parentMotif,
          childIds: [...new Set([...existingChildIds, ...fetchedChildIds])],
        });
      }

      return next;
    });
  }

  clearVisibleNodes() {
    this.$visibleNodes.update((current) => {
      const next = new Set(current);
      next.clear();
      return next;
    });
  }

  clearResultNodes() {
    this.$resultNodes.update((current) => {
      const next = new Set(current);
      next.clear();
      return next;
    });
  }

  async getRootMotifs(): Promise<void> {
    const roots = await firstValueFrom(this.motifService.getRootMotifs());

    this.$motifNodes.update((current) => {
      const next = new Map(current);

      for (const root of roots) {
        if (root.id) next.set(root.id, root);
      }

      return next;
    });

    this.$rootIds.update((current) => {
      const next = new Set(current);
      roots.forEach((root) => {
        if (root.id) next.add(root.id);
      });

      return next;
    });

    this.sortRootIds();
  }

  addNewRootMotif(newRootMotif: IMotif) {
    this.$motifNodes.update((current) => {
      const next = new Map(current);

      if (newRootMotif.id) next.set(newRootMotif.id, newRootMotif);

      return next;
    });

    this.$rootIds.update((current) => {
      const next = new Set(current);

      if (newRootMotif.id) next.add(newRootMotif.id);

      return next;
    });

    this.sortRootIds();
  }

  //Gets motif node in store corresponding to ID.
  getMotifNode(id: number): IMotif | undefined {
    return this.$motifNodes().get(id);
  }

  setMotifNodes(motifNodes: IMotif[]) {
    //update method takes current state (current) and returns updated state (next).
    this.$motifNodes.update((current) => {
      const next = new Map(current);

      for (const motifNode of motifNodes) {
        if (motifNode.id) next.set(motifNode.id, motifNode);
      }

      return next;
    });
  }

  updateMotifNode(updatedMotifNode: IMotif) {
    this.$motifNodes.update((current) => {
      const next = new Map(current);

      if (updatedMotifNode.id) next.set(updatedMotifNode.id, updatedMotifNode);

      return next;
    });
  }

  assignChild(childMotifNode: IMotif) {
    this.$motifNodes.update((current) => {
      const next = new Map(current);

      if (childMotifNode.parentId == null) return next;
      const parentMotifNode = next.get(childMotifNode.parentId);
      const updatedChildIds = parentMotifNode?.childIds ?? [];

      if (childMotifNode.id != null) updatedChildIds?.push(childMotifNode.id);

      if (parentMotifNode && parentMotifNode.id != null) {
        next.set(parentMotifNode.id, {
          ...parentMotifNode,
          childIds: updatedChildIds,
          childCount: parentMotifNode.childCount,
        });
      }

      return next;
    });
  }

  sortChildIds(parentNode: IMotif) {
    this.$motifNodes.update((current) => {
      const next = new Map(current);
      const childIdsToSort = parentNode?.childIds ?? [];

      //Create array of child motifs
      const children: IMotif[] = [];
      childIdsToSort.forEach((id) => {
        if (id == null) return;
        const child = next.get(id);
        if (!child) {
          return;
        }
        children.push(child);
      });

      //Sort array by motif code and map IDs
      children.sort((a, b) => a.motifCode.localeCompare(b.motifCode, undefined, { numeric: true }));
      const sortedChildIds = children.map((child) => child.id).filter((id) => id != null);

      //Update parent node
      if (parentNode.id)
        next.set(parentNode.id, {
          ...parentNode,
          childIds: sortedChildIds,
        });

      return next;
    });
  }

  sortRootIds() {
    this.$rootIds.update((current) => {
      const rootMotifs: IMotif[] = [];

      current.forEach((id) => {
        const rootMotif = this.getMotifNode(id);
        if (!rootMotif) return;
        rootMotifs.push(rootMotif);
      });

      rootMotifs.sort((a, b) => a.motifCode.localeCompare(b.motifCode, undefined, { numeric: true }));
      const sortedRootMotifs = rootMotifs.map((rootMotif) => rootMotif.id).filter((id) => id != null);

      return new Set(sortedRootMotifs);
    });
  }

  postMotifNode(newMotifNode: IMotif) {
    this.$requestLoading.set(true);
    this.motifService.postMotif(newMotifNode).subscribe({
      next: (postedMotif) => {
        this.$requestLoading.set(false);
        this.motifModalService.closeModal();
        console.log("motif posted: " + postedMotif);
        this.updateMotifNode(postedMotif);

        if (postedMotif.parentId != null) {
          this.assignChild(postedMotif);
          const parentNode = this.getMotifNode(postedMotif.parentId);
          if (!parentNode) return;
          this.sortChildIds(parentNode);
          this.expand(postedMotif.parentId);
        } else {
          this.addNewRootMotif(postedMotif);
        }
      },
      error: (err) => {
        this.$requestLoading.set(false);
        console.log("Error posting motif: " + JSON.stringify(err));
      },
    });
  }

  putMotifNode(updatedMotifNode: IMotif) {
    this.$requestLoading.set(true);
    this.motifService.updateMotif(updatedMotifNode).subscribe({
      next: (updatedMotif) => {
        this.$requestLoading.set(false);
        this.motifModalService.closeModal();
        //Retain children after edit. Children are not returned by backend API
        //and field 'hasChildren' is not set until after a following get request.
        //This is a shortcut.
        const motif = updatedMotif;
        motif.childCount = updatedMotifNode.childCount;
        motif.childIds = updatedMotifNode.childIds;
        this.updateMotifNode(motif);

        if (motif.parentId != null) {
          const parentNode = this.getMotifNode(motif.parentId);
          if (!parentNode) return;
          this.sortChildIds(parentNode);
        } else {
          this.sortRootIds();
        }

        this.editPinnedNode(updatedMotif);
      },
      error: (err) => {
        this.$requestLoading.set(false);
        console.log("Problem updating motif: " + JSON.stringify(err));
      },
    });
  }

  //Can I shorten this method?
  deleteMotifNode(id: number) {
    this.$motifNodes.update((current) => {
      const next = new Map(current);

      //Recursively remove children of children, and then child itself
      function removeNode(id: number) {
        const node = next.get(id);
        if (!node || node.id == null) return;

        if (node.childIds) {
          for (const child of node.childIds) {
            if (child) removeNode(child);
          }
        }

        next.delete(node.id);
      }

      //Remove node from parent
      const node = next.get(id);
      if (!node) return next;

      removeNode(id);

      const parentId = node.parentId;
      if (parentId == null) return next;

      const nodeParent = next.get(parentId);

      if (nodeParent) {
        const updatedParent = {
          ...nodeParent,
          childIds: nodeParent.childIds?.filter((childId) => childId !== id) ?? [],
        };

        if (updatedParent.id == null) return next;

        if (!updatedParent.childIds?.length) {
          this.collapse(updatedParent.id);
          updatedParent.childCount = 0;
        }

        next.set(updatedParent.id, updatedParent);
      }

      return next;
    });

    //Remove root IDs which no longer exist in the $motifNodes map
    this.$rootIds().forEach((id) => {
      if (!this.$motifNodes().has(id)) {
        this.$rootIds().delete(id);
      }
    });

    //Remove expanded node IDs which no longer exist in the $motifNodes map
    this.$expandedNodes().forEach((id) => {
      if (!this.$motifNodes().has(id)) {
        this.$expandedNodes().delete(id);
      }
    });

    console.log("deleting motif " + id);
    this.motifService.deleteMotif(id).subscribe({
      next: () => this.removePinnedNode(id),
      error: (err) => console.log("Error deleting motif node: " + JSON.stringify(err.error)),
    });
  }

  async getMotifChildren(id: number): Promise<void> {
    this.$expandLoading.set(true);
    const children = await firstValueFrom(this.motifService.getChildren(id)).catch((error) => {
      console.log(JSON.stringify(error));
      this.$expandLoading.set(false);
    });

    this.$expandLoading.set(false);

    this.$motifNodes.update((current) => {
      const next = new Map(current);

      if (!children) return next;

      const sortedChildren = children.sort((a, b) => a.motifCode.localeCompare(b.motifCode));

      for (var child of sortedChildren) {
        if (child.id == null) return next;
        //Only add new children to avoid overwriting existing nodes and potential grandchildren
        if (!next.has(child.id)) {
          next.set(child.id, child);
        }
      }

      //Assigns child id(s) to parent motif
      const parentMotif = next.get(id);

      if (parentMotif) {
        next.set(id, {
          ...parentMotif,
          childIds: [...new Set(sortedChildren.map((child) => child.id).filter((id) => id != null))],
        });
      }

      return next;
    });
  }

  expand(id: number) {
    this.$expandedNodes.update((current) => {
      const next = new Set(current);
      next.add(id);

      return next;
    });
  }

  collapse(id: number) {
    this.$expandedNodes.update((current) => {
      const next = new Set(current);
      next.delete(id);

      return next;
    });
  }

  collapseAll() {
    this.$expandedNodes.update((current) => {
      const next = new Set(current);
      next.clear();

      return next;
    });
  }
}
