package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.dto.ErrorDTO;
import com.akasoft.poneyrox.exceptions.AbstractException;
import com.akasoft.poneyrox.views.ErrorViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *  Controleur des erreurs.
 */
@RestController
@ControllerAdvice
public class TreatErrorController {
    /**
     *  Prise en charge d'une exception levée par un controleur.
     *  @param ex Exception levée.
     *  @return Exception propre à la sérialisation.
     */
    @ExceptionHandler(AbstractException.class)
    @JsonView(ErrorViews.Public.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handle(AbstractException ex) {
        ex.printStackTrace();
        return new ErrorDTO(ex);
    }
}
