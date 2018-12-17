package org.san.home.accounts.service.impl;

import com.google.common.collect.Iterables;
import org.joda.money.Money;
import org.san.home.accounts.jpa.AccountRepository;
import org.san.home.accounts.model.Account;
import org.san.home.accounts.service.AccountService;
import org.san.home.accounts.service.error.BusinessException;
import org.san.home.accounts.service.error.ErrorArgument;
import org.san.home.accounts.service.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author sanremo16
 */
@Service
public class AccountServiceImpl implements AccountService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Account withdraw(@NotNull String accNumber, @NotNull Money money) {
        Account account = accountRepository.findOneByNumForUpdate(Objects.requireNonNull(accNumber))
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find account by account number"));
        validateCurrency(account, money);
        Money currBalance = account.getBalance();
        if (money.compareTo(currBalance) > 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_MONEY,
                    new ErrorArgument("balance", currBalance),
                    new ErrorArgument("money", money));
        }
        account.setBalance(currBalance.minus(money));
        return accountRepository.save(account);
    }

    @Transactional
    public Account topUp(@NotNull String accNumber, @NotNull Money money) {
        Account account = accountRepository.findOneByNumForUpdate(Objects.requireNonNull(accNumber))
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find account by account number"));
        validateCurrency(account, money);
        account.setBalance(account.getBalance().plus(money));
        return accountRepository.save(account);
    }

    @Transactional
    public Account transfer(@NotNull String srcAccNumber, @NotNull String dstAccNumber, @NotNull Money money) {
        withdraw(srcAccNumber, money);
        return topUp(dstAccNumber, money);
    }

    @Override
    public @NotNull Optional<Account> getByAccountNumber(@NotNull String accNum) {
        return Optional.ofNullable(Iterables.get(accountRepository.findByNum(accNum), 0));
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    private static void validateCurrency(@NotNull Account account, @NotNull Money money) {
        if (!Objects.requireNonNull(money.getCurrencyUnit()).equals(Objects.requireNonNull(account.getCurrency()).getCurrencyUnit())) {
            throw new BusinessException(ErrorCode.INCOMPATIBLE_CURRENCY,
                    new ErrorArgument("currency", money.getCurrencyUnit()),
                    new ErrorArgument("account", account));
        }
    }
}
