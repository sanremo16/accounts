package org.san.home.accounts.model;

import lombok.Getter;
import org.joda.money.CurrencyUnit;

/**
 * @author sanremo16
 */
@Getter
public enum CurrencyType {
    RUR("RUR"),
    USD("USD"),
    EUR("EUR");

    private String iso;
    private CurrencyUnit currencyUnit;

    CurrencyType(String iso) {
        this.iso = iso;
        currencyUnit = CurrencyUnit.of(iso);
    }

    @Override
    public String toString() {
        return iso;
    }


}
