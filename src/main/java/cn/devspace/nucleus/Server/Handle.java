package cn.devspace.nucleus.Server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

@RestControllerAdvice
public class Handle extends SimpleMappingExceptionResolver {
    // 全局异常

    // 自定义token异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleOpdRuntimeException(NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.OK.value()).body("hello");
    }
}
