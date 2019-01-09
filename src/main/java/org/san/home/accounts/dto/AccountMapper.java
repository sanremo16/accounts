package org.san.home.accounts.dto;

import com.google.common.base.Enums;
import org.joda.money.Money;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.san.home.accounts.model.Account;
import org.san.home.accounts.model.CurrencyType;
import org.san.home.accounts.service.error.CommonException;
import org.san.home.accounts.service.error.ErrorArgument;
import org.san.home.accounts.service.error.ErrorCode;
import org.springframework.stereotype.Component;


/**
 * @author sanremo16
 */
@Component
public class AccountMapper extends ModelMapper {

    private final Converter<AccountDto, Account> DTO_TO_ACCOUNT = new Converter<AccountDto, Account>() {

        private MoneyMapper moneyMapper = new MoneyMapper();

        public Account convert(MappingContext<AccountDto, Account> context) {
            AccountDto dto = context.getSource();
            Account account = new Account();
            account.setId(dto.getId());
            account.setNum(dto.getNum());
            account.setCurrency(Enums.getIfPresent(CurrencyType.class,
                    dto.getCurrencyType()).toJavaUtil().orElseThrow(
                        () -> new CommonException(ErrorCode.UNSUPPORTED_CURRENCY, new ErrorArgument("CurrencyType", dto.getCurrencyType()))
            ));
            if (dto.getBalance() != null)
                account.setBalance(moneyMapper.map(dto.getBalance(), Money.class));
            return account;
        }
    };

    public AccountMapper() {
        super();
        addConverter(MoneyMapper.DTO_TO_MONEY);
        addConverter(MoneyMapper.MONEY_TO_DTO);
        addConverter(DTO_TO_ACCOUNT);
    }

}
