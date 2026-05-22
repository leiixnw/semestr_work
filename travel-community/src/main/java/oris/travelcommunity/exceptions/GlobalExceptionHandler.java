package oris.travelcommunity.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import oris.travelcommunity.dto.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
