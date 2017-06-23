import {Component, Input} from "@angular/core";
import {CellDTO} from "../../dto/cell.dto";

/**
 *  Composant descriptif d'un taux affiché dans une ligne
 *  temporelle.
 */
@Component({
  selector: "div[cell]",
  templateUrl: "./cell.component.html",
  styleUrls: ["./cell.component.less"]
})
export class CellComponent {
  /**
   *  Cellule affichée.
   */
  @Input()
  private cell: CellDTO;
}
