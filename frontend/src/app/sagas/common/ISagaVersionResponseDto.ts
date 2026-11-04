import { ICharacterName } from '../../characters/ICharacterName';
import { ILocationName } from '../../locations/ILocationName';
import { ISagaMotif } from './ISagaMotif';
import { SagaDate } from './SagaDate';

export interface ISagaVersionResponseDto {
  id: number;
  title: string;
  abbreviation: string;
  description: string;
  date: SagaDate;
  sagaId: number;  
  sagaMotifs: ISagaMotif[];
  characterNames: ICharacterName[];
  locationNames: ILocationName[];
} 