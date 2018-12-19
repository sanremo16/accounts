package org.san.home.accounts.dto;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.san.home.accounts.model.CurrencyType;
import org.springframework.stereotype.Component;

/**
 * @author sanremo16
 */
@Component
public class MoneyMapper extends ModelMapper {

    static final Converter<MoneyDto, Money> DTO_TO_MONEY = new Converter<MoneyDto, Money>() {
        public Money convert(MappingContext<MoneyDto, Money> context) {
            MoneyDto dto = context.getSource();
            Money money = Money.ofMajor(CurrencyUnit.of(CurrencyType.RUR.getIso()), dto.getMajor());
            return money.plusMinor(dto.getMinor());
        }
    };

    static final Converter<Money, MoneyDto> MONEY_TO_DTO = new Converter<Money, MoneyDto>() {
        public MoneyDto convert(MappingContext<Money, MoneyDto> context) {
            Money money = context.getSource();
            MoneyDto dto = new MoneyDto();
            dto.setMajor(money.getAmountMajorInt());
            dto.setMinor(money.getMinorPart());
            return dto;
        }
    };

    public MoneyMapper() {
        super();
        addConverter(DTO_TO_MONEY);
        addConverter(MONEY_TO_DTO);
    }
}
