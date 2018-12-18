package org.san.home.accounts.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.san.home.accounts.model.CurrencyType;

/**
 * @author sanremo16
 */
@Data
@Slf4j
public class AccountDto {
    @ApiModelProperty(notes = "The database generated account ID")
    private Long id;
    @ApiModelProperty(notes = "Account number")
    private String num;
    @ApiModelProperty(notes = "Currency type by ISO code")
    private CurrencyType currencyType;
    @ApiModelProperty(notes = "Account balance")
    private MoneyDto balance;
}
