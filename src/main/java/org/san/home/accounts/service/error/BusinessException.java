package org.san.home.accounts.service.error;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author sanremo16
 */
@Data
@Slf4j
public class BusinessException extends CommonException {

    public BusinessException(@NotNull ErrorCode errCode, @NotNull ErrorArgument... args) {
        super(errCode, null, args != null ? Arrays.asList(args) : Collections.EMPTY_LIST);
    }
}
