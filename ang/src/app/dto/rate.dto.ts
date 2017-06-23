import {MarketDTO} from "./market.dto";
/**
 *  DTO descriptif d'un taux.
 */
export class RateDTO {
  /**
   *  Identifiant.
   */
  public id: string;

  /**
   *  Date.
   */
  public time: number;

  /**
   *  Demande.
   */
  public ask: number;

  /**
   *  Offre.
   */
  public bid: number;

  /**
   *  Marché.
   */
  public market: MarketDTO;
}
