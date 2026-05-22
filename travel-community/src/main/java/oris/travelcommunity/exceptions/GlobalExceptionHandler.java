package oris.travelcommunity.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException ex, Model model) {
        log.warn("Ресурс не найден: {}", ex.getMessage());

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("status", 404);

        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        log.error("Произошла внутренняя ошибка сервера", ex);

        model.addAttribute("errorMessage", "Что-то пошло не так. Попробуйте позже или обратитесь к администратору.");
        model.addAttribute("status", 500);

        return "error/500";
    }
}
