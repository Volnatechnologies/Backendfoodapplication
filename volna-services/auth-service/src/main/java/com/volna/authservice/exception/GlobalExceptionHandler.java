package com.volna.authservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
 public Map<String,Object> bad(IllegalArgumentException e){
  return body(400,e.getMessage());
 }
 @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
 public Map<String,Object> validation(MethodArgumentNotValidException e){
  Map<String,String> fields=new LinkedHashMap<>();
  e.getBindingResult().getFieldErrors().forEach(x->fields.put(x.getField(),x.getDefaultMessage()));
  Map<String,Object> r=body(400,"Validation failed");
  r.put("fields",fields);
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