package com.netcracker.exception;

import com.netcracker.pojoServices.BadRequestDTO;
import com.netcracker.pojoServices.StatusDTO;
import org.hibernate.HibernateException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.sql.SQLException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<StatusDTO> handleSQLException(SQLException e) {
        StatusDTO statusDTO = StatusDTO.create("ERROR");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(statusDTO);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<BadRequestDTO> handleResourceNotFoundException(ResourceNotFoundException e) {
        BadRequestDTO badRequestDTO = BadRequestDTO.create(e.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(badRequestDTO);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BadRequestDTO badRequestDTO = BadRequestDTO.create("Correct JSON!");
        return ResponseEntity.status(BAD_REQUEST).body(badRequestDTO);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<BadRequestDTO> handleHibernateException(DataIntegrityViolationException ex) { ;
        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
        BadRequestDTO tableDTO = BadRequestDTO.create(message);
        return ResponseEntity.status(BAD_REQUEST).body(tableDTO);
    }

    @ExceptionHandler(value = ResourceAccessException.class)
    public ResponseEntity<BadRequestDTO> handleResourceAccessException(ResourceAccessException ex) { ;
        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
        BadRequestDTO tableDTO = BadRequestDTO.create(message);
        return ResponseEntity.status(BAD_REQUEST).body(tableDTO);
    }

}
