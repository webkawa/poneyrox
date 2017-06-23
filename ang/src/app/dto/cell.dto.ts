/**
 *  DTO descriptif d'un taux calculé.
 */
export class CellDTO {
  /**
   *  Niveau de lissage.
   */
  public level: number;

  /**
   *  Données.
   */
  public data: CellDataDTO;
}

/**
 *  DTO descriptif des données d'un taux.
 */
export class CellDataDTO {
  /**
   *  Date des données.
   */
  public date: number;

  /**
   *  Niveau de la demande.
   */
  public ask: ClusterDTO;

  /**
   *  Niveau de l'offre.
   */
  public bid: ClusterDTO;
}

/**
 *  DTO descriptif d'une division de cellules.
 */
export class ClusterDTO {
  /**
   *  Taux minimum.
   */
  public minimum: number;

  /**
   *  Taux moyen.
   */
  public average: number;

  /**
   *  Taux maximum.
   */
  public maximum: number;

  /**
   *  Direction.
   */
  public direction: boolean;
}
