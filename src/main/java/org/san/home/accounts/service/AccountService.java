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

    @NotNull Optional<Account> getByAccountNumber(@NotNull String accountNumber);

    List<Account> findAll();

    Account withdraw(@NotNull String accNumber, @NotNull Money money);

    Account topUp(@NotNull String accNumber, @NotNull Money money);

    Account transfer(@NotNull String srcAccNumber, @NotNull String dstAccNumber, @NotNull Money money);
}
