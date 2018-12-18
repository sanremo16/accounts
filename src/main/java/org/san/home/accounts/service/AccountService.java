package org.san.home.accounts.service;

import org.joda.money.Money;
import org.san.home.accounts.model.Account;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Base account service
 * @author sanremo16
 */
public interface AccountService {

    @NotNull Account getByAccountNumber(@NotNull String accountNumber);

    List<Account> findAll();

    @NotNull Account add(@NotNull Account account);

    @NotNull Account update(@NotNull Account account);

    void delete(@NotNull String accNumber);

    @NotNull Account withdraw(@NotNull String accNumber, @NotNull Money money);

    @NotNull Account topUp(@NotNull String accNumber, @NotNull Money money);

    @NotNull Account transfer(@NotNull String srcAccNumber, @NotNull String dstAccNumber, @NotNull Money money);
}
