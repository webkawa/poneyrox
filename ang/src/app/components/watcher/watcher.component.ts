import {Component, Inject, OnInit} from "@angular/core";
import {AccessService} from "../../services/access.service";
import {MarketDTO} from "../../dto/market.dto";
import {TimelineDTO} from "../../dto/timeline.dto";
import {RateDTO} from "../../dto/rate.dto";
import {Observable} from "rxjs/Observable";

@Component({
  selector: "div[id='watcher']",
  templateUrl: "./watcher.component.html",
  styleUrls: ["./watcher.component.less"]
})
export class WatcherComponent implements OnInit {
  /**
   *  Service d'accès aux données.
   */
  private access: AccessService;

  /**
   *  Ligne ajoutée.
   */
  private add: TimelineDTO;

  /**
   *  Liste des marchés.
   */
  private markets: MarketDTO[];

  /**
   *  Liste des taux.
   */
  private rates: RateDTO[];

  /**
   *  Liste des lignes temporelles.
   */
  private timelines: TimelineDTO[];

  /**
   *  Constructeur.
   *  @param access Service d'accès.
   */
  constructor(@Inject(AccessService) access: AccessService) {
    this.access = access;
    this.markets = [];
    this.timelines = [];

    this.add = new TimelineDTO();
    this.add.label = "Observateur";
    this.add.size = 30;
    this.add.active = true;
    this.add.market = this.markets[0];
  }

  /**
   *  Initialisation.
   */
  public ngOnInit(): void {
    /* Chargement des données. */
    this.access
      .getMarkets()
      .subscribe(markets => {
        this.markets = markets;
        this.access
          .getTimelines()
          .subscribe(timelines => this.timelines = timelines);
      });

    /* Souscription à la liste des taux */
    Observable.interval(1000).subscribe(item => {
      this.access
        .getRates()
        .subscribe(rates => {
          this.rates = rates;
        });
    });
  }

  /**
   *  Réalise l'ajout d'une ligne de temps.
   */
  private addTimeline(): void {
    this.access
      .addTimeline(this.add)
      .subscribe(result => {
        this.timelines.push(result);
      });
  }
}
