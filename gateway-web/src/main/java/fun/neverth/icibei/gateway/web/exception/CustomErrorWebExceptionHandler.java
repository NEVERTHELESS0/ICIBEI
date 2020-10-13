package fun.neverth.icibei.gateway.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;

/**
 * SpringCloud Finchley Gateway 统一异常处理
 *
 * 写一个自己的CustomErrorWebExceptionHandler类，
 * 继承 DefaultErrorWebExceptionHandler 的 renderErrorResponse()方法和getRoutingFunction() 。
 * (原本DefaultErrorWebExceptionHandler中这两个方法都是返回错误页面的，我们这里只需要重新塞入自定义的handler就行)
 *
 * https://blog.csdn.net/weixin_42437633/article/details/107595805
 *
 * @author NeverTh
 * @date 15:02 2020/10/6
 */
public class CustomErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    /**
     * 自定义异常处理器
     */
    @Resource
    private GatewayExceptionHandlerAdvice gateWayExceptionHandlerAdvice;

    public CustomErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 指定响应处理方法为JSON处理的方法
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> error = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus errorStatus = HttpStatus.valueOf(getHttpStatus(error));
        Throwable throwable = getError(request);
        return ServerResponse.status(errorStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(gateWayExceptionHandlerAdvice.handle(throwable)));
    }
}
