package org.san.home.accounts.controller.error;

import org.san.home.accounts.service.error.CommonException;
import org.san.home.accounts.service.error.ErrorArgument;
import org.san.home.accounts.service.error.ErrorCode;

import java.util.Arrays;
import java.util.Collection;

/**
 * Исключение REST-сервисов
 * Аргументы не испошьзуются, т.к. все параметры получаются из HttpRequest-а.
 */
public class RestException extends CommonException {

    private static final ErrorArgument[] EMPTY = new ErrorArgument[0];

    public RestException(ErrorCode errorCode) {
        this(errorCode, null, EMPTY);
    }

    public RestException(ErrorCode errorCode, ErrorArgument... args) {
        this(errorCode, null, args);
    }

    public RestException(ErrorCode errorCode, Collection<ErrorArgument> args) {
        this(errorCode, null, args);
    }

    public RestException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, cause, EMPTY);
    }

    public RestException(ErrorCode errorCode, Throwable cause, ErrorArgument... args) {
        this(errorCode, cause, Arrays.asList(args));
    }

    public RestException(ErrorCode errorCode, Throwable cause, Collection<ErrorArgument> args) {
        super(errorCode, cause, args);
    }
}
