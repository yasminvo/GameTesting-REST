package br.ufscar.dc.dsw.GameTesting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ModelAndView handleAppException(AppException ex, Model model) {
        ModelAndView mav = new ModelAndView("/errors/custom-error");
        mav.setStatus(ex.getStatus());
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, Model model) {
        ex.printStackTrace();
        ModelAndView mav = new ModelAndView("/errors/custom-error");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("message", "Erro interno no servidor.");
        return mav;
    }
}
