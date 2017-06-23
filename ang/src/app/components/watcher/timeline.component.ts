import {Component, Inject, Input, OnDestroy, OnInit, ViewEncapsulation} from "@angular/core";
import {TimelineDTO} from "../../dto/timeline.dto";
import {CellDTO} from "../../dto/cell.dto";
import {Observable} from "rxjs/Observable";
import {AccessService} from "../../services/access.service";
import {List} from "linqts";
import {Subscriber} from "rxjs/Subscriber";
import {Subscription} from "rxjs/Subscription";
import {Subject} from "rxjs";

/**
 *  Composant descriptif d'une ligne temporelle
 */
@Component({
  selector: "div[timeline]",
  templateUrl: "./timeline.component.html",
  styleUrls: ["./timeline.component.less"],
  encapsulation: ViewEncapsulation.Native
})
export class TimelineComponent implements OnInit, OnDestroy {
  /**
   *  Service d'accès aux données.
   */
  private access: AccessService;

  /**
   *  Cible.
   */
  @Input()
  private timeline: TimelineDTO;

  /**
   *  Dates de mise à jour.
   */
  private updates: number[];

  /**
   *  Données chargés.
   */
  private data: CellDTO[][];

  /**
   *  Liste des niveaux évalués.
   */
  private levels: number[];

  /**
   *  Constructeur.
   *  @param access Service d'accès aux données.
   */
  constructor(@Inject(AccessService) access: AccessService) {
    this.access = access;
    this.updates = [];
    this.data = [];
    this.levels = [];
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
    if (this.timeline.active) {
      this.access
        .getTimelineRates(this.timeline)
        .subscribe(rates => {
          /* Intégration à l'affichage */
          this.data.unshift(rates);
          this.updates.unshift(new Date().getTime());
          if (this.data.length > 40) {
            this.data.splice(this.data.length - 1, 1);
            this.updates.splice(this.updates.length - 1, 1);
          }

          /* Calcul des niveaux affichés */
          this.levels = new List(this.data)
            .SelectMany(column => {
              return new List(column)
                .Select(rate => rate.level)
                .Distinct();
            })
            .Distinct()
            .OrderBy(level => level)
            .ToArray();
        });
    }
  }

  /**
   *  Active une ligne temporelle.
   */
  private activateTimeline() {
    this.access
      .setTimelineActivity(this.timeline, true)
      .subscribe(timeline => this.timeline = timeline);
  }

  /**
   *  Désactive une ligne temporelle.
   */
  private disableTimeline() {
    this.access
      .setTimelineActivity(this.timeline, false)
      .subscribe(timeline => this.timeline = timeline);
  }
}
