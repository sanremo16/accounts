package org.san.home.accounts.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.san.home.accounts.model.CurrencyType;

/**
 * @author sanremo16
 */
@Data
@Slf4j
public class AccountDto {
    private Long id;
    private String num;
    private CurrencyType currencyType;
    private MoneyDto balance;
}
