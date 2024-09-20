package com.fiuni.distri.project.fiuni.controllers;

import com.fiuni.distri.project.fiuni.exceptions.ApiException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class GlobalErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (status != null) {
            try {
                httpStatus = HttpStatus.valueOf(status);
            } catch (Exception e) {
                logger.error("Error occurred: Invalid status code = {}", status);
            }
        }

        logger.error("Error occurred: Status code = {}", httpStatus.value());

        // Agregar atributos para Thymeleaf
        model.addAttribute("status", httpStatus.value());
        model.addAttribute("statusText", httpStatus.getReasonPhrase());

        return "error/error";  // Página genérica para otros errores
    }

    // Manejar la excepción personalizada ApiException
    @ExceptionHandler(ApiException.class)
    public String handleApiException(ApiException ex, Model model) {
        logger.error("API error: {}", ex.getReason());

        // Agregar atributos para Thymeleaf
        model.addAttribute("status", ex.getStatus().value());
        model.addAttribute("statusText", ex.getStatus().getReasonPhrase());
        model.addAttribute("errorMessage", ex.getReason());

        return "error/Error404";  // Página personalizada para 404
    }
}
