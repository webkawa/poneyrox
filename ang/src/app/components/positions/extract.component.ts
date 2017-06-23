import {Component, Inject, Input, OnDestroy, OnInit} from "@angular/core";
import {AccessService} from "../../services/access.service";
import {Observable} from "rxjs/Observable";
import {PositionDTO} from "app/dto/position.dto";
import {Subscription} from "rxjs/Subscription";
import {Subject} from "rxjs";

/**
 *  Extraction d'une liste de positions.
 */
@Component({
  selector: "div[extract]",
  styleUrls: ["./extract.component.less"],
  templateUrl: "./extract.component.html"
})
export class ExtractComponent implements OnInit, OnDestroy {
  /**
   *  Point d'accès aux données.
   */
  private access: AccessService;

  /**
   *  Type d'extraction.
   */
  @Input()
  private extract: string;

  /**
   *  Titre.
   */
  @Input()
  private title: string;

  /**
   *  Type de position extraite.
   */
  @Input()
  private type: string;

  /**
   *  Mode.
   */
  @Input()
  private mode: boolean;

  /**
   *  Nombre de lignes affichées.
   */
  @Input()
  private limit: number;

  /**
   *  Taux de rafraichissement.
   *  Exprimé en millisecondes.
   */
  @Input()
  private tick: number;

  /**
   *  Liste des rafraichissements.
   */
  private positions: PositionDTO[];

  /**
   *  Constructeur.
   *  @param access Point d'accès aux données.
   */
  constructor(@Inject(AccessService) access: AccessService) {
    this.access = access;
    this.positions = [];
  }

  /**
   *  Initialisation.
   */
  public ngOnInit(): void {
    this.refresh();
  }

  /**
   *  Destruction.
   */
  public ngOnDestroy(): void {
  }

  /**
   *  Rafraichissement.
   */
  public refresh(): void {
    if (this.extract == "top") {
      this.access.getTopPositions(this.type, this.mode, this.limit).subscribe(positions => this.positions = positions);
    } else {
      throw new Error("Invalid extract type");
    }
  }
}
