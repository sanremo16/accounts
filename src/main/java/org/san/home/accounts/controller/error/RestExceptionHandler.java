package org.san.home.accounts.controller.error;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.san.home.accounts.service.error.BusinessException;
import org.san.home.accounts.service.error.CommonException;
import org.san.home.accounts.service.error.ErrorArgument;
import org.san.home.accounts.service.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.google.common.collect.Maps.newLinkedHashMap;

/**
 * Базовый обработчик исключений в REST-контроллерах
 */
@ControllerAdvice("org.san.home.accounts.controller")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String USER_MESSAGE_SEPARATOR = " → ";

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        //return new ResponseEntity<Object>(createDtoException(ex), headers, status);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createDtoException(ex));
    }

    @ExceptionHandler(RestException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDto handle(RestException e) {
        return createDtoException(e);
    }

    @ExceptionHandler(CommonException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDto handle(CommonException e) {
        return createDtoException(e);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDto handle(BusinessException e) {
        return createDtoException(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDto handle(Exception e, HttpServletRequest request) {
        CommonException commonException = new CommonException(
            ErrorCode.UNDEFINED,
            e,
            getHttpParameters(request));
        return createDtoException(commonException);
    }

    private static List<ErrorArgument> getHttpParameters(HttpServletRequest request) {
        List<ErrorArgument> arguments = new ArrayList<>();
        arguments.add(new ErrorArgument("url", request.getRequestURI()));
        if (request.getParameterMap() != null)
            for (Map.Entry<String, String[]> ent : request.getParameterMap().entrySet()) {
                arguments.add(new ErrorArgument(ent.getKey(), StringUtils.join(ent.getValue(), ", ")));
            }

        return arguments;
    }

    private ErrorDto createDtoException(@NotNull MethodArgumentNotValidException e) {
        logger.error(e.getMessage(), e);

        //бежим по стеку и составляем тексты для пользователя и ищем самый глубокий код - первопричину всех бед
        StringBuilder exceptionText = new StringBuilder(
                CommonException.getLocalizedMessageFromResource(ErrorCode.VALIDATION_ERROR.getName()));
        Map<String, Object> arguments = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            arguments.put(fieldName, errorMessage);
        });

        return new ErrorDto(ErrorCode.VALIDATION_ERROR.getCode(), exceptionText.toString(), arguments, ExceptionUtils.getStackTrace(e));
    }

    private ErrorDto createDtoException(@NotNull CommonException e) {
        logger.error(e.getMessage(), e);

        //бежим по стеку и составляем тексты для пользователя и ищем самый глубокий код - первопричину всех бед
        StringBuilder exceptionText = new StringBuilder(e.getLocalizedErrName());
        Map<String, Object> arguments = prepareArguments(e.getArgs());
        Throwable curr = e;
        while ((curr = curr.getCause()) != null) {
            if (curr instanceof CommonException) {
                exceptionText.append(USER_MESSAGE_SEPARATOR).append(((CommonException) curr).getLocalizedErrName());
                if (((CommonException) curr).getArgs() != null) {
                    arguments.putAll(prepareArguments(((CommonException) curr).getArgs()));
                }
            }
        }

        return new ErrorDto(e.getErrCode().getCode(), exceptionText.toString(), arguments, ExceptionUtils.getStackTrace(e));
    }

    /**
     * Извлечь в аргументы детальную информацию по ошибкам.
     * Информация может сериализоваться в ресурсы.
     *
     * @param arguments коллекция аргументов.
     * @return карта аргументов.
     */
    private Map<String, Object> prepareArguments(Collection<ErrorArgument> arguments) {
        Map<String, Object> ret = newLinkedHashMap();
        for (ErrorArgument argument : arguments) {
            Object argValue = argument.getArgValue();
            String argValueStr = "";
            if (argValue != null) {
                try {
                    argValueStr = argValue.toString();
                } catch (Exception e) {
                    // exception of any kind during #toString
                    // e.g. LazyInitializationException
                    try {
                        argValueStr = argValue.getClass().getSimpleName();
                    } catch (Exception ex) {
                        argValueStr = argValue.getClass().getName() + '@'
                            + Integer.toHexString(System.identityHashCode(argValue));
                    }
                }
            }
            ret.put(argument.getArgName(), argValueStr);
        }
        return ret;
    }
}
