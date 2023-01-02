package ru.nsu.fit.pak.budle.controller;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import ru.nsu.fit.pak.budle.BaseResponse;
import ru.nsu.fit.pak.budle.exceptions.BaseException;
import ru.nsu.fit.pak.budle.exceptions.IncorrectDataException;
import ru.nsu.fit.pak.budle.exceptions.UserAlreadyExistsException;

import java.util.LinkedHashMap;

@ControllerAdvice
public class ArticleController implements ResponseBodyAdvice<Object> {
    @ExceptionHandler({UserAlreadyExistsException.class, IncorrectDataException.class})
    public <T extends BaseException> ResponseEntity<BaseResponse<Object>> handleException(T e) {
        BaseResponse<Object> response = new BaseResponse<>(e.getMessage(), e.getType());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof BaseResponse<?> || body instanceof LinkedHashMap<?, ?>) {
            return body;
        }
        return new BaseResponse<>(body);
    }
}