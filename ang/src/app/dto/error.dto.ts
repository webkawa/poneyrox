import {Response} from '@angular/http';

/**
 *  DTO représentatif d'une erreur interne.
 */
export class ErrorDTO {
  /**
   *  Message de notification.
   */
  public notification: string;

  /**
   *  Réponse envoyée par le serveur.
   */
  public response: Response;
}
