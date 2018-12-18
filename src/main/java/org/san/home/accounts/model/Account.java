package org.san.home.accounts.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.joda.money.Money;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * @author sanremo16
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "account", uniqueConstraints = @UniqueConstraint(columnNames = {"num"}))
public class Account {

    @Id
    @Getter
    @Setter
    @GeneratedValue
    private Long id;

    @NotNull
    @Getter
    @Setter
    @Length(min = 3, max = 20)
    @Column(name = "num")
    private String num;

    @PositiveOrZero
    @Column(name = "balance", scale = 2)
    private BigDecimal balance = BigDecimal.ZERO.setScale(CurrencyType.RUR.getCurrencyUnit().getDecimalPlaces());

    @NotNull
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    protected CurrencyType currency;

    public Account(Long l, String s, Money money) {
        id = l;
        num = s;
        balance = ModelUtils.moneyToBigDecimal(money);
        currency = CurrencyType.valueOf(money.getCurrencyUnit().getCode());
    }


    public Money getBalance() {
        return ModelUtils.bigDecimalToMoney(balance, currency);
    }

    public void setBalance(Money money) {
        balance = ModelUtils.moneyToBigDecimal(money);
    }

}
