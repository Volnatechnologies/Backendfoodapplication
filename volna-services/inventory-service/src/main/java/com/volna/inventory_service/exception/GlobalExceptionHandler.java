package com.volna.inventory_service.exception;
import org.springframework.http.*;import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.annotation.*;import java.time.LocalDateTime;import java.util.*;
@RestControllerAdvice public class GlobalExceptionHandler {
 private ResponseEntity<Map<String,Object>> r(HttpStatus s,String m){Map<String,Object>x=new LinkedHashMap<>();x.put("timestamp",LocalDateTime.now());x.put("status",s.value());x.put("error",s.getReasonPhrase());x.put("message",m);return ResponseEntity.status(s).body(x);}
 @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<?> nf(ResourceNotFoundException e){return r(HttpStatus.NOT_FOUND,e.getMessage());}
 @ExceptionHandler(BadRequestException.class) ResponseEntity<?> br(BadRequestException e){return r(HttpStatus.BAD_REQUEST,e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> val(MethodArgumentNotValidException e){Map<String,String> m=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->m.put(x.getField(),x.getDefaultMessage()));Map<String,Object>x=new LinkedHashMap<>();x.put("timestamp",LocalDateTime.now());x.put("status",400);x.put("error","Validation Failed");x.put("messages",m);return ResponseEntity.badRequest().body(x);}
 @ExceptionHandler(Exception.class) ResponseEntity<?> any(Exception e){return r(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred");}
}
