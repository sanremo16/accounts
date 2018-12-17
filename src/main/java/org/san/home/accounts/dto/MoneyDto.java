package org.san.home.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.san.home.accounts.model.CurrencyType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MoneyDto {
    private Integer major;
    private Integer minor;
    private CurrencyType currencyType;
}
