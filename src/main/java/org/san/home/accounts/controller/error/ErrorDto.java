package org.san.home.accounts.controller.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Описание ошибки для клиентов REST-сервисов.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {

    @JsonProperty
    public final int errorCode;

    @JsonProperty
    public final String exceptionText;

    @JsonProperty
    public final String stacktraceText;

    @JsonProperty
    private Map<String, Object> arguments;

    /**
     * Необходимо передавать системное время.
     */
    @JsonProperty
    private final String date;


    static final DateTimeFormatter DT_FORMATTER
        = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());


    @JsonCreator
    public ErrorDto(
        @JsonProperty("errorCode") Integer errorCode,
        @JsonProperty("exceptionText") String exceptionText,
        @JsonProperty("arguments") Map<String, Object> arguments,
        @JsonProperty("stacktraceText") String stacktraceText) {
        this.errorCode = errorCode;
        this.exceptionText = exceptionText;
        this.stacktraceText = stacktraceText;
        this.date = DT_FORMATTER.format(Instant.now());
        this.arguments = arguments;
    }

    public String getDate() {
        return date;
    }
}
