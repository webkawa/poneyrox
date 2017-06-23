import {MarketDTO} from "./market.dto";
/**
 *  DTO descriptif d'une ligne de temps.
 */
export class TimelineDTO {
  /**
   *  Identifiant.
   */
  public id: string;

  /**
   *  Libellé.
   */
  public label: string;

  /**
   *  Taille des cellules.
   */
  public size: number;

  /**
   *  Statut d'activité.
   */
  public active: boolean;

  /**
   *  Marché.
   */
  public market: MarketDTO;
}
