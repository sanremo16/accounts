package org.san.home.accounts.controller.error;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.san.home.accounts.monitoring.MonitoringUtilsService;
import org.san.home.accounts.service.error.ErrorArgument;
import org.san.home.accounts.service.error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Класс, реализиющий общим механизм формирования исключений RestException
 * при возникновении любых исключений в методах бинов, аннотированных WrapException.
 * В формируемое исключения автоматически передаётся список параметров и значений вызываемого метода.
 */
@Aspect
@Component
public class RestExceptionWrapper {
    @Autowired
    private MonitoringUtilsService monitoringUtilsService;

    @Around("execution(* org.san.home.accounts..*.*(..)) && @annotation(org.san.home.accounts.controller.error.WrapException)")
    public Object defaultException(ProceedingJoinPoint point) throws Throwable {
        Object result;
        try {
            //monitoringUtilsService.incrementRequestsActiveCounter();
            result = point.proceed();
        } catch (Exception e) {
            //monitoringUtilsService.processException(e);

            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            WrapException a = methodSignature.getMethod().getAnnotation(WrapException.class);
            if (a != null) {
                List<ErrorArgument> params = newArrayList();
                String[] names = methodSignature.getParameterNames();
                List<?> values = Arrays.asList(point.getArgs());
                if (names != null) {
                    boolean varargs = names.length != values.size();
                    for (int i = 0; i < names.length; i++) {
                        Object value = (varargs && i == names.length - 1)
                            ? values.subList(i, values.size())
                            : values.get(i);
                        params.add(new ErrorArgument(names[i], toString(value)));
                    }
                }
                if (e instanceof RestException) {
                    ((RestException) e).addArgs(params);
                    throw e;
                }
                throw new RestException(a.errorCode() != null ? a.errorCode() : ErrorCode.UNDEFINED, e, params);
            }
            throw e;
        } finally {
            //monitoringUtilsService.decrementRequestsActiveCounter();
        }
        //monitoringUtilsService.getSuccessRequestsCounter().increment();
        return result;
    }




    private static String toString(Object o) {
        return (o instanceof RepresentationModel)
            ? "RepresentationModel{" + o.getClass().getSimpleName() + '}'
            : String.valueOf(o);
    }

}
