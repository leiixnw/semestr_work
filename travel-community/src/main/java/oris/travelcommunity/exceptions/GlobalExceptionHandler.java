package oris.travelcommunity.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import oris.travelcommunity.dto.ErrorResponse;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public Object handleNotFoundException(NotFoundException ex, Model model, HttpServletRequest request) {
        log.warn("Ресурс не найден: {}", ex.getMessage());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now())
            );
        }

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("status", 404);
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, Model model, HttpServletRequest request) {
        log.error("Произошла внутренняя ошибка сервера", ex);

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now())
            );
        }

        model.addAttribute("errorMessage", "Что-то пошло не так. Попробуйте позже или обратитесь к администратору.");
        model.addAttribute("status", 500);
        return "error/500";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");
        return uri.startsWith("/api/") ||
               "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) ||
               (acceptHeader != null && acceptHeader.contains("application/json") && !acceptHeader.contains("text/html"));
    }
}
