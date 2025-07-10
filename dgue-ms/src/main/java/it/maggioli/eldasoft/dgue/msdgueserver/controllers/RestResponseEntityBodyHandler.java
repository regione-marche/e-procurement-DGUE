package it.maggioli.eldasoft.dgue.msdgueserver.controllers;

import it.maggioli.eldasoft.dgue.msdgueserver.core.APIConstants;
import it.maggioli.eldasoft.dgue.msdgueserver.core.ResponseBean;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 05, 2019
 */
@ControllerAdvice(basePackages = {"it.maggioli.eldasoft.dgue.msdgueserver.controllers"})
public class RestResponseEntityBodyHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof ResponseBean || body instanceof Resource ||
                request.getURI().toString().contains("swagger") || request.getURI().toString().endsWith("/v2/api-docs")) {
            return body;
        } else {
            final ResponseBean responseBean = new ResponseBean();
            responseBean.setDone(APIConstants.RESPONSE_DONE_Y);
            if (body != null)
                responseBean.setResponse(body);
            return responseBean;
        }
    }
}
