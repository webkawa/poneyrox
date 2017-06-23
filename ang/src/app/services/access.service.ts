import {Inject, OnInit} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {Http, Response} from "@angular/http";
import {ErrorDTO} from "../dto/error.dto";
import {MarketDTO} from "../dto/market.dto";
import {TimelineDTO} from "../dto/timeline.dto";
import {RateDTO} from "../dto/rate.dto";
import {CellDTO} from "../dto/cell.dto";
import {PositionDTO} from "../dto/position.dto";

/**
 *  Service d'accès au serveur.
 */
export class AccessService implements OnInit {
  /**
   *  Service HTTP.
   */
  private http: Http;

  constructor(@Inject(Http) http: Http) {
    this.http = http;
    this.ngOnInit();
  }

  /**
   *  Initialisation.
   */
  public ngOnInit(): void {
  }

  /**
   *  Retourne la liste des marchés.
   *  @returns {Observable<MarketDTO>} Liste des marchés.
   */
  public getMarkets(): Observable<MarketDTO[]> {
    return this.get<MarketDTO[]>("markets/get");
  }

  /**
   *  Retourne une liste des positions les plus performantes.
   *  @param type Type de position.
   *  @param mode Mode.
   *  @param limit Nombre d'entrées recherchées.
   *  @returns {Observable<PositionDTO[]>} Liste de positions.
   */
  public getTopPositions(type: string, mode: boolean, limit: number) : Observable<PositionDTO[]> {
    return this.get<PositionDTO[]>("positions/top/" + type + "/" + mode + "/" + limit);
  }

  /**
   *  Retourne la liste des lignes de temps référencées.
   *  @returns {Observable<TimelineDTO[]>} Liste des lignes de temps.
   */
  public getTimelines(): Observable<TimelineDTO[]> {
    return this.get<TimelineDTO[]>("timelines/get");
  }

  /**
   *  Retourne la liste des taux rattachés à une ligne temporelle.
   *  @param timeline Ligne temporelle.
   * @returns {Observable<CellDTO[]>} Taux correspondants.
   */
  public getTimelineRates(timeline: TimelineDTO): Observable<CellDTO[]> {
    return this.post<CellDTO[], TimelineDTO>("timelines/rate", timeline);
  }

  /**
   *  Retourne la liste des derniers taux levés.
   *  @returns {Observable<RateDTO[]>} Liste des taux.
   */
  public getRates(): Observable<RateDTO[]> {
    return this.get<RateDTO[]>("rates/get");
  }

  /**
   *  Définit le statut d'activité d'une ligne de temps.
   *  @param timeline Ligne affectée.
   *  @param activity Statut défini.
   *  @returns {Observable<TimelineDTO>} Observable correspondant.
   */
  public setTimelineActivity(timeline: TimelineDTO, activity: boolean): Observable<TimelineDTO> {
    return this.post<TimelineDTO, TimelineDTO>("timelines/set/activity/" + activity, timeline);
  }

  /**
   *  Ajoute une ligne temporelle.
   *  @param add Ligne ajoutée.
   *  @returns {Observable<TimelineDTO>} Ligne de temps ajoutée.
   */
  public addTimeline(add: TimelineDTO): Observable<TimelineDTO> {
    return this.post<TimelineDTO, TimelineDTO>("timelines/add", add);
  }

  /**
   *  Service de requetage HTTP en mode GET.
   *  @param url URL requetée.
   *  @returns {Observable<R|T>} Observable correspondant.
   */
  private get<T>(url: string): Observable<T> {
    return this.treat<T>(this.http.get("../services/" + url));
  }

  /**
   *  Service de requetage HTTP en mode POST.
   *  @param url URL requetée.
   *  @param data Données passées.
   *  @returns {Observable<T>} Observable correspondant.
   */
  private post<T, U>(url: string, data: U): Observable<T> {
    return this.treat<T>(this.http.post("../services/" + url, data));
  }

  /**
   *  Traite une requete HTTP standard.
   *  @param response Réponse reçue.
   *  @returns {Observable<R|T>} Observable traité.
   */
  private treat<T>(response: Observable<Response>): Observable<T> {
    return response.map(response => {
      let result: T = response.json();
      return result;
    })
      .catch(error => {
        let result: ErrorDTO = new ErrorDTO();
        if (error instanceof Response) {
          let response: Response = error as Response;
          result = response.json();
          result.response = response;
        } else {
          result.notification = error.toString();
        }
        return Observable.throw(result);
      });
  }
}
