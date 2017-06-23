import {Directive, ElementRef, Inject, Input, OnInit} from "@angular/core";

/**
 *  Directive permettant l'affichage d'une icone en arrière-plan d'un
 *  élément.
 */
@Directive({
  selector: "[icon]"
})
export class IconDirective implements OnInit {
  /**
   *  Référence à l'élément.
   */
  public ref: ElementRef;

  /**
   *  Clef d'accès à l'icone.
   */
  @Input("icon")
  public icon: string;

  /**
   *  Constructeur.
   */
  constructor(@Inject(ElementRef) ref: ElementRef) {
    this.ref = ref;
  }

  /**
   *  Initialisation.
   */
  public ngOnInit(): void {
    let split: string[] = this.icon.split("/");
    let path = "url('../services/media/";
    path += split[0];
    path += "/";
    path += split[1] ? split[1] : "96";
    if (split[2]) {
      path += "/";
      path += split[2];
    }

    this.ref.nativeElement.style["background-image"] = path;
    this.ref.nativeElement.style["background-position"] = "center center";
    this.ref.nativeElement.style["background-repeat"] = "no-repeat";
  }
}
