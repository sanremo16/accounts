package org.san.home.accounts.model;

import org.joda.money.Money;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author sanremo16
 */
public class ModelUtils {
    public static @NotNull BigDecimal moneyToBigDecimal(Money money) {
        if (money != null && money.getCurrencyUnit() != null) {
            if (CurrencyType.valueOf(money.getCurrencyUnit().getCode()) == null) {
                throw new IllegalArgumentException("Currency is not supported: " + money.getCurrencyUnit().getCode());
            }
            return money.getAmount();
        } else {
            return BigDecimal.ZERO.setScale(CurrencyType.RUR.getCurrencyUnit().getDecimalPlaces());
        }
    }

    public static @NotNull Money bigDecimalToMoney(@NotNull BigDecimal bg, @NotNull CurrencyType currency) {
        return Money.of(currency.getCurrencyUnit(), bg, RoundingMode.HALF_DOWN);
    }
}
