package org.san.home.accounts.service.error;

/**
 * @author sanremo16
 */
public enum ErrorCode {
    UNDEFINED(0, "undefined"),
    INCOMPATIBLE_CURRENCY(1, "incompatible_currency"),
    INSUFFICIENT_MONEY(2, "insufficient_money");

    private Integer code;
    private String name;

    ErrorCode(Integer errCode, String errName) {
        this.code = errCode;
        this.name = errName;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
