import { ICharacterLocation } from "./ICharacterLocation";
import { Sex } from "./Sex";
import { Species } from "./Species";

export interface ICharacter {
  id: number | null;
  name: string;
  species: Species;
  sex: Sex;
  notes: string;
  characterLocationDtos: ICharacterLocation[];
  sagaVersionIds: number[]
}