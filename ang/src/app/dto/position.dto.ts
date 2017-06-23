import {TimelineDTO} from "./timeline.dto";
/**
 *  DTO descriptif d'une position.
 */
export class PositionDTO {
  /**
   *  Identifiant.
   */
  public id: string;

  /**
   *  Mode.
   */
  public mode: boolean;

  /**
   *  Ouverture.
   */
  public open: boolean;

  /**
   *  Date de début.
   */
  public start: number;

  /**
   *  Date de fin.
   */
  public end: number;

  /**
   *  Cout à l'entrée.
   */
  public entry: number;

  /**
   *  Score à l'entrée.
   */
  public entryScore: number;

  /**
   *  Cout à la sortie.
   */
  public exit: number;

  /**
   *  Score à la sortie.
   */
  public exitScore: number;

  /**
   *  Profit généré.
   */
  public profit: number;

  /**
   *  Expiration.
   */
  public timeout: boolean;

  /**
   *  Ligne temporelle.
   */
  public timeline: TimelineDTO;

  /**
   *  Niveau de lissage.
   */
  public smooth: number;

  /**
   *  Type.
   */
  public type: string;
}
