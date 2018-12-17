package org.san.home.accounts.controller.error;

import org.san.home.accounts.service.error.ErrorCode;

import java.lang.annotation.*;

/**
 * Используется для пометки методов, порождаемые которыми исключения которые необходимо
 * обернуть в RestException с указанным в аннотации параметром-кодом.
 * <p>
 * Улучшает читаем кода и убирает море копипасты.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WrapException {

    ErrorCode errorCode() default ErrorCode.UNDEFINED;
}
