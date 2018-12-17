package org.san.home.accounts.controller;

import lombok.extern.slf4j.Slf4j;
import org.joda.money.Money;
import org.san.home.accounts.dto.AccountDto;
import org.san.home.accounts.dto.DtoMapper;
import org.san.home.accounts.dto.MoneyDto;
import org.san.home.accounts.model.CurrencyType;
import org.san.home.accounts.service.AccountService;
import org.san.home.accounts.controller.error.WrapException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.san.home.accounts.service.error.ErrorCode.*;

/**
 * @author sanremo16
 */
@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private DtoMapper mapper;

    @WrapException
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<AccountDto> findAll() {
        return accountService.findAll().stream()
                .map(account -> mapper.map(account, AccountDto.class))
                .collect(Collectors.toList());
    }

    @WrapException
    @GetMapping(value = "/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AccountDto get(@PathVariable("num") String num) {
        return mapper.map(
                accountService.getByAccountNumber(num)
                        .orElseThrow(() -> new RuntimeException("Couldn't find entity by account number ")),
                AccountDto.class);
    }

    @WrapException
    @PostMapping(value = "/topUp", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto topUp(@RequestParam String accountNumber,
                            @RequestParam(required = false, defaultValue = "0") Integer moneyMajor,
                            @RequestParam(required = false, defaultValue = "0") Integer moneyMinor,
                            @RequestParam(required = false, defaultValue = "RUR") String currency) {
        return mapper.map(
                accountService.topUp(accountNumber,
                        mapper.map(new MoneyDto(moneyMajor, moneyMinor, CurrencyType.valueOf(currency)), Money.class)),
                AccountDto.class);
    }

    @WrapException
    @PostMapping(value = "/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto withdraw(@RequestParam String accountNumber,
                            @RequestParam(required = false, defaultValue = "0") Integer moneyMajor,
                            @RequestParam(required = false, defaultValue = "0") Integer moneyMinor,
                            @RequestParam(required = false, defaultValue = "RUR") String currency) {
        return mapper.map(
                accountService.withdraw(accountNumber,
                        mapper.map(new MoneyDto(moneyMajor, moneyMinor, CurrencyType.valueOf(currency)), Money.class)),
                AccountDto.class);
    }

    @WrapException
    @PostMapping(value = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto transfer(@RequestParam String srcAccountNumber,
                               @RequestParam String dstAccountNumber,
                               @RequestParam(required = false, defaultValue = "0") Integer moneyMajor,
                               @RequestParam(required = false, defaultValue = "0") Integer moneyMinor,
                               @RequestParam(required = false, defaultValue = "RUR") String currency) {
        return mapper.map(
                accountService.transfer(srcAccountNumber,dstAccountNumber,
                        mapper.map(new MoneyDto(moneyMajor, moneyMinor, CurrencyType.valueOf(currency)), Money.class)),
                AccountDto.class);
    }



}
