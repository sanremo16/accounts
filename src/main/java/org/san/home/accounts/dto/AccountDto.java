package org.san.home.accounts.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sanremo16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AccountDto {
    @ApiModelProperty(notes = "The database generated account ID")
    private Long id;
    @ApiModelProperty(notes = "Account number")
    private String num;
    @ApiModelProperty(notes = "Currency type by ISO code")
    private String currencyType;
    @ApiModelProperty(notes = "Account balance")
    private MoneyDto balance;
}
