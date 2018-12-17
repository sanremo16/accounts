package org.san.home.accounts.service.error;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * @author sanremo16
 */
@Data
public class CommonException extends RuntimeException {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static String MESSAGE_NOT_FOUND = "Message not found";

    private static MessageSource messageSource = new ErrorMessageSource();

    private ErrorCode errCode;
    private String localizedErrName;
    private Throwable cause;
    private Collection<ErrorArgument> args;

    public CommonException(@NotNull ErrorCode errCode, @NotNull ErrorArgument... args) {
        this(errCode, null, args);
    }

    public CommonException(@NotNull ErrorCode errCode, Throwable cause, @NotNull ErrorArgument... args) {
        this(errCode, cause, args != null ? Arrays.asList(args) : Collections.EMPTY_LIST);
    }

    public CommonException(@NotNull ErrorCode errCode, Throwable cause, @NotNull Collection<ErrorArgument> args) {
        this.errCode = errCode;
        this.args = ImmutableList.copyOf(args);
        this.cause = cause;
        localizedErrName = getLocalizedMessageFromResource(errCode.getName());
    }

    @Override
    public String getMessage() {
        return errCode + ": " + localizedErrName + ", " + args + ", " + cause;
    }

    public void writeToLog() {
        logger.error(getMessage(), this);
    }

    public static String getLocalizedMessageFromResource(String name) {
        return messageSource.getMessage(name, null, MESSAGE_NOT_FOUND + ": " + name, Locale.getDefault());
    }

    public void addArgs(Collection<ErrorArgument> args) {
        if (args != null && !args.isEmpty()) {
            this.args.addAll(args);
        }
    }

    public Collection<ErrorArgument> getArgs() {
        return args;
    }
}
