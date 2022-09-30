package uz.isystem.Bank.Service.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handler(BadRequest badRequest){
        return ResponseEntity.badRequest().body(badRequest.getMessage());
    }
    @ExceptionHandler
    public ResponseEntity<?> handler(IllegalArgumentException exception){
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
    @ExceptionHandler
    public ResponseEntity<?> handler(ServiceUnavailable serviceUnavailable){
        return new ResponseEntity<>(serviceUnavailable.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
