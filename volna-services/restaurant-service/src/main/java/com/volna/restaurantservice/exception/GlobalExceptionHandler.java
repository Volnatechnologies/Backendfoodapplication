package com.volna.restaurantservice.exception;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.time.OffsetDateTime; import java.util.*;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(BadRequestException.class)
 @ResponseStatus(HttpStatus.BAD_REQUEST)
 Map<String,Object> bad(BadRequestException e){
  return body(400,e.getMessage());
 }
 @ExceptionHandler(ResourceNotFoundException.class)
 @ResponseStatus(HttpStatus.NOT_FOUND)
 Map<String,Object> notFound(ResourceNotFoundException e){
  return body(404,e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
 Map<String,Object> validation(MethodArgumentNotValidException e){
  Map<String,String> f=new LinkedHashMap<>();
  e.getBindingResult()
          .getFieldErrors()
          .forEach(x->f.put(x.getField(),
                  x.getDefaultMessage()));
  Map<String,Object> r=body(400,"Validation failed");
  r.put("fields",f);
  return r;
 }
 private Map<String,Object> body(int s,String m){
  Map<String,Object> r=new LinkedHashMap<>();
  r.put("timestamp",OffsetDateTime.now());
  r.put("status",s);
  r.put("message",m);
  return r;
 }
}