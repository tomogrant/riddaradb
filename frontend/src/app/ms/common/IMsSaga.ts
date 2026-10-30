export interface IMsSaga {
  trackingId?: number;
  sagaId: number;
  sagaTitle?: string;
  published?: boolean;
  folioNumber: string;
  note?: string | null;
  selected?: boolean;
}
