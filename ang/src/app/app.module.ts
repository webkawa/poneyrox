import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {RootComponent} from "./components/root/root.component";
import {WatcherComponent} from "./components/watcher/watcher.component";
import {AccessService} from "./services/access.service";
import {IconDirective} from "./directives/icon.directive";
import {TimelineComponent} from "./components/watcher/timeline.component";
import {CellComponent} from "./components/watcher/cell.component";
import {SimulationComponent} from "./components/simulations/simulation.component";
import {PositionComponent} from "./components/positions/position.component";
import {ExtractComponent} from "./components/positions/extract.component";

/**
 *  Module.
 */
@NgModule({
  declarations: [
    CellComponent,
    ExtractComponent,
    PositionComponent,
    RootComponent,
    SimulationComponent,
    TimelineComponent,
    WatcherComponent,
    IconDirective
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  providers: [
    AccessService
  ],
  bootstrap: [RootComponent]
})
export class AppModule { }
