import { Continent } from "./Continent";
import { ILocationCharacter } from "./ILocationCharacter";

export interface ILocation {
  id: number | null;
  name: string;
  translation: string;
  continent: Continent;
  notes: string;
  locationCharacterDtos: ILocationCharacter[];
  sagaVersionIds: number[];
}