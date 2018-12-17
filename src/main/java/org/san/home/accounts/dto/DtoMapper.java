package org.san.home.accounts.dto;

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
public class DtoMapper extends ModelMapper {
    private static final Converter<MoneyDto, Money> DTO_TO_MONEY = new Converter<MoneyDto, Money>() {
        public Money convert(MappingContext<MoneyDto, Money> context) {
            MoneyDto dto = context.getSource();
            Money money = Money.ofMajor(dto.getCurrencyType().getCurrencyUnit(), dto.getMajor());
            return money.plusMinor(dto.getMinor());
        }
    };

    private static final Converter<Money, MoneyDto> MONEY_TO_DTO = new Converter<Money, MoneyDto>() {
        public MoneyDto convert(MappingContext<Money, MoneyDto> context) {
            Money money = context.getSource();
            MoneyDto dto = new MoneyDto();
            dto.setCurrencyType(CurrencyType.valueOf(money.getCurrencyUnit().getCode()));
            dto.setMajor(money.getAmountMajorInt());
            dto.setMinor(money.getMinorPart());
            return dto;
        }
    };

    public DtoMapper() {
        super();
        addConverter(DTO_TO_MONEY);
        addConverter(MONEY_TO_DTO);
    }

}
