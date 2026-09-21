package com.volna.menuservice.exception;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(java.util.NoSuchElementException.class)
 ResponseEntity<Map<String,String>> notFound(Exception e){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",e.getMessage()));}
 @ExceptionHandler(Exception.class)
 ResponseEntity<Map<String,String>> bad(Exception e){return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",e.getMessage()==null?"Request failed":e.getMessage()));}
}
