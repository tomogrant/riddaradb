import { ICharacter } from "../../characters/ICharacter";
import { ICharacterName } from "../../characters/ICharacterName";
import { ILocationName } from "../../locations/ILocationName";
import { ISagaMotif } from "./ISagaMotif";
import { SagaDate } from "./SagaDate";

export interface ISagaVersionVm {
  id: number | null;
  title: string;
  abbreviation: string;
  description: string;
  date: SagaDate;
  sagaId: number;
  sagaMotifs: ISagaMotif[];
  characterNames: ICharacterName[];
  locationNames: ILocationName[];
}
