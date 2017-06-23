import {Component, Inject, OnInit} from '@angular/core';
import {TabDTO} from "../../dto/tab.dto";

/**
 *  Composant racine.
 */
@Component({
  selector: 'div[id="root"]',
  templateUrl: './root.component.html',
  styleUrls: ['./root.component.less']
})
export class RootComponent implements OnInit {
  /**
   *  Liste des onglets.
   */
  private tabs: TabDTO[];

  /**
   *  Onglet affiché.
   */
  private display: TabDTO;

  /**
   *  Constructeur.
   */
  constructor() {
    /* Création des onglets */
    this.tabs = [];
    this.tabs.push(new TabDTO("Observateurs", "watcher", "watcher"));
    this.tabs.push(new TabDTO("Stratégies", "strategy", "strategy"));
    this.tabs.push(new TabDTO("Simulations", "simulations", "profit"))

    /* Onglet affiché */
    this.display = this.tabs[0];
  }

  /**
   *  Initialisation.
   */
  public ngOnInit(): void {
  }

  /**
   *  Modifie l'onglet affiché.
   *  @param tab Onglet affiché.
   */
  public goto(tab: TabDTO) {
    this.display = tab;
  }
}
