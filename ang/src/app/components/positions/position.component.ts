import {Component, Input, OnInit} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {PositionDTO} from "../../dto/position.dto";

/**
 *  Position unitaire.
 */
@Component({
  selector: "div[position]",
  styleUrls: ["./position.component.less"],
  templateUrl: "./position.component.html"
})
export class PositionComponent implements OnInit {
  /**
   *  Dernière position recue.
   */
  @Input()
  private position: PositionDTO;

  /**
   *  Constructeur.
   */
  constructor() {
  }

  /**
   *  Initialisation.
   */
  public ngOnInit(): void {
  }
}
