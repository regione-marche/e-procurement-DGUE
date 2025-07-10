package it.maggioli.eldasoft.dgue.msdgueserver.controllers;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;

import it.maggioli.eldasoft.dgue.msdgueserver.core.APIConstants;
import it.maggioli.eldasoft.dgue.msdgueserver.core.ResponseBean;
import it.maggioli.eldasoft.dgue.msdgueserver.core.message.DGUEMessage;
import it.maggioli.eldasoft.dgue.msdgueserver.core.message.DGUEMessageBuilder;
import it.maggioli.eldasoft.dgue.msdgueserver.core.message.DGUEMessageCode;
import it.maggioli.eldasoft.dgue.msdgueserver.core.message.DGUEMessageSeverity;
import it.maggioli.eldasoft.dgue.msdgueserver.exceptions.BadRequestException;
import it.maggioli.eldasoft.dgue.msdgueserver.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 05, 2019
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);
    private static final String FIELDS_PREFIX = "services.validations.fields.";
    private static final String MESSAGES_PREFIX = "services.validations.messages.";

    @Autowired
    private DGUEMessageBuilder messageBuilder = null;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.warn("Exception handle in RestResponseEntityExceptionHandler. See details", ex);
        final ResponseBean responseBean = new ResponseBean();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            final DGUEMessage message = messageBuilder.buildMessage(DGUEMessageCode.INPUT_VALIDATION_ERROR,
                    DGUEMessageSeverity.WARN,
                    FIELDS_PREFIX + fieldError.getField(), MESSAGES_PREFIX + fieldError.getDefaultMessage());
            responseBean.getMessages().add(message);
        }
        responseBean.setDone(APIConstants.RESPONSE_DONE_N);
        return handleExceptionInternal(ex, responseBean, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    public final ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatus status,
                                                                     WebRequest request) {
        LOG.warn("Exception handle in RestResponseEntityExceptionHandler. See details", ex);
        final ResponseBean responseBean = new ResponseBean();
        final DGUEMessage dgueMessage = messageBuilder.buildMessage(DGUEMessageCode.INVALID_BODY, ex.getMessage());
        responseBean.getMessages().add(dgueMessage);
        responseBean.setDone(APIConstants.RESPONSE_DONE_N);
        return handleExceptionInternal(ex, responseBean, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> onConstraintValidationException(ConstraintViolationException e, WebRequest request) {
        LOG.warn("Exception handle in RestResponseEntityExceptionHandler. See details", e);
        final ResponseBean responseBean = new ResponseBean();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            final DGUEMessage message = messageBuilder.buildMessage(DGUEMessageCode.INPUT_VALIDATION_ERROR,
                    DGUEMessageSeverity.WARN,
                    FIELDS_PREFIX + violation.getPropertyPath().toString(), MESSAGES_PREFIX + violation.getMessage() );

            responseBean.getMessages().add(message);
        }
        responseBean.setDone(APIConstants.RESPONSE_DONE_N);
        return handleExceptionInternal(e, responseBean, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * HTTP STATUS 400
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = { BadRequestException.class })
    protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        LOG.error("Exception handle in RestResponseEntityExceptionHandler. See details", ex);
        final ResponseBean responseBean = new ResponseBean();
        responseBean.setDone(APIConstants.RESPONSE_DONE_N);
        responseBean.getMessages().addAll(ex.getMessages());
        return handleExceptionInternal(ex, responseBean, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * HTTP STATUS 403
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(final Exception ex, final WebRequest request) {
        LOG.error("Exception handle in RestResponseEntityExceptionHandler. See details", ex);
        final DGUEMessage message = messageBuilder.buildMessage(DGUEMessageCode.RESPONSE_FORBIDDEN);
        final ResponseBean responseBean = new ResponseBean();
        responseBean.setDone(APIConstants.RESPONSE_DONE_N);
        responseBean.getMessages().add(message);
        return handleExceptionInternal(ex, responseBean, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /**
     * HTTP STATUS 404
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = { ResourceNotFoundException.class })
    protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        LOG.error("Exception handle in RestResponseEntityExceptionHandler. See details", ex);
        final ResponseBean responseBean = new ResponseBean();
        responseBean.setDone(APIConstants.RESPONSE_DONE_N);
        responseBean.getMessages().add(ex.getDGUEMessage());
        return handleExceptionInternal(ex, responseBean, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = { IllegalArgumentException.class, Exception.class, NullPointerException.class, RuntimeException.class })
    protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        LOG.error("Exception handle in RestResponseEntityExceptionHandler. See details", ex);
        final DGUEMessage message = messageBuilder.buildMessage(DGUEMessageCode.RESPONSE_GENERIC_ERROR, ex.getMessage());
        final ResponseBean responseBean = new ResponseBean();
        responseBean.getMessages().add(message);
        responseBean.setDone(APIConstants.RESPONSE_DONE_N);
        return handleExceptionInternal(ex, responseBean, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
