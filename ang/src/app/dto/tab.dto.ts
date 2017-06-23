/**
 *  DTO descriptif d'un onglet.
 */
export class TabDTO {
  /**
   *  Libellé de l'onglet.
   */
  public label: string;

  /**
   *  Clef d'accès.
   */
  public key: string;

  /**
   *  Icone rattachée.
   */
  public icon: string;

  /**
   *  Constructeur.
   *  @param label Libellé de l'onglet.
   *  @param key Clef d'accès.
   *  @param icon Icone rattachée.
   */
  constructor(label: string, key: string, icon: string) {
    this.label = label;
    this.key = key;
    this.icon = icon;
  }
}
