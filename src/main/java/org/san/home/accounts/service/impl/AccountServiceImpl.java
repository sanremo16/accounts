package org.san.home.accounts.service.impl;

import com.google.common.collect.Iterables;
import org.joda.money.Money;
import org.san.home.accounts.jpa.AccountRepository;
import org.san.home.accounts.model.Account;
import org.san.home.accounts.service.AccountService;
import org.san.home.accounts.service.error.BusinessException;
import org.san.home.accounts.service.error.CommonException;
import org.san.home.accounts.service.error.ErrorArgument;
import org.san.home.accounts.service.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.san.home.accounts.service.error.ErrorCode.GET_ACCOUNT_FAILED;
import static org.san.home.accounts.service.error.ErrorCode.TOO_MANY_ACCOUNTS;

/**
 *
 * @author sanremo16
 */
@Service
public class AccountServiceImpl implements AccountService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public Account withdraw(@NotNull String accNumber, @NotNull Money money) {
        Account account = getAccountForUpdate(accNumber);
        money = fixMoneyCurrency(Objects.requireNonNull(account), Objects.requireNonNull(money));
        Money currBalance = account.getBalance();
        if (money.compareTo(currBalance) > 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_MONEY,
                    new ErrorArgument("balance", currBalance),
                    new ErrorArgument("money", money));
        }
        account.setBalance(currBalance.minus(money));
        return accountRepository.save(account);
    }

    private @NotNull Account getAccountForUpdate(@NotNull String accNumber) {
        return accountRepository.findOneByNumForUpdate(Objects.requireNonNull(accNumber))
                    .orElseThrow(() -> new CommonException(GET_ACCOUNT_FAILED, new ErrorArgument("accNumber", accNumber)));
    }

    @Transactional
    public Account topUp(@NotNull String accNumber, @NotNull Money money) {
        Account account = getAccountForUpdate(accNumber);
        money = fixMoneyCurrency(Objects.requireNonNull(account), Objects.requireNonNull(money));
        account.setBalance(account.getBalance().plus(money));
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account transfer(@NotNull String srcAccNumber, @NotNull String dstAccNumber, @NotNull Money money) {
        if (Objects.requireNonNull(srcAccNumber).equals(Objects.requireNonNull(dstAccNumber))) {
            throw new BusinessException(ErrorCode.SAME_ACCOUNT_TRANSFER,
                    new ErrorArgument("srcAccNumber", srcAccNumber),
                    new ErrorArgument("dstAccNumber", dstAccNumber));
        }
        validateCurrency(getAccountForUpdate(srcAccNumber), getAccountForUpdate(dstAccNumber));
        withdraw(srcAccNumber, money);
        return topUp(dstAccNumber, money);
    }

    @Override
    @Transactional
    public @NotNull Account getByAccountNumber(@NotNull String accNum) {
        Collection<Account> accounts = accountRepository.findByNum(accNum);
        if (Iterables.isEmpty(accounts)) {
            throw new CommonException(GET_ACCOUNT_FAILED, new ErrorArgument("accNum", accNum));
        } else if (accounts.size() > 1) {
            throw new CommonException(TOO_MANY_ACCOUNTS, new ErrorArgument("accNum", accNum));
        } else {
            return Iterables.get(accountRepository.findByNum(accNum), 0);
        }
    }

    @Override
    @Transactional
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public @NotNull Account add(@NotNull Account account) {
        return accountRepository.save(Objects.requireNonNull(account));
    }

    @Override
    @Transactional
    public @NotNull Account update(@NotNull Account account) {
        account.setId(getByAccountNumber(Objects.requireNonNull(account).getNum()).getId());
        return accountRepository.save(Objects.requireNonNull(account));
    }

    @Override
    @Transactional
    public @NotNull void delete(@NotNull String accNum) {
        accountRepository.deleteByNum(accNum);
    }

    private static Money fixMoneyCurrency(@NotNull Account account, @NotNull Money money) {
        return Money.of(Objects.requireNonNull(account.getCurrency()).getCurrencyUnit(), money.getAmount());
    }

    private static void validateCurrency(@NotNull Account account1, @NotNull Account account2) {
        if (!Objects.requireNonNull(account1.getCurrency()).equals(account2.getCurrency()))
            throw new BusinessException(ErrorCode.INCOMPATIBLE_CURRENCY,
                    new ErrorArgument("account1", account1),
                    new ErrorArgument("account2", account2));
    }
}
