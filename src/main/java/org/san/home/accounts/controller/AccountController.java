package org.san.home.accounts.controller;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.Money;
import org.san.home.accounts.controller.error.WrapException;
import org.san.home.accounts.dto.AccountDto;
import org.san.home.accounts.dto.AccountMapper;
import org.san.home.accounts.dto.MoneyDto;
import org.san.home.accounts.dto.MoneyMapper;
import org.san.home.accounts.model.Account;
import org.san.home.accounts.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.san.home.accounts.service.error.ErrorCode.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author sanremo16
 */
@RestController
@RequestMapping("/accounts")
@Slf4j
@Tag(name = "Simple API for account's operations")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private MoneyMapper moneyMapper;

    //@Timed(value = "findAll", description = "findAll method time")
    @Operation(summary = "View a list of accounts")
    @WrapException(errorCode = GET_ALL_FAILED)
    @GetMapping(value = "/list", produces = { "application/hal+json" })
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<AccountDto> findAll() {
        Collection<AccountDto> accounts =
            accountService.findAll().stream()
                .map(account -> accountMapper.map(account, AccountDto.class))
                .map(accountDto -> accountDto.add(linkTo(methodOn(AccountController.class).get(accountDto.getNum())).withSelfRel()))
                .collect(Collectors.toList());
        return CollectionModel.of(accounts, linkTo(methodOn(AccountController.class).findAll()).withSelfRel());
    }

    @Operation(summary = "Get account by account number")
    @WrapException(errorCode = GET_ACCOUNT_FAILED)
    @GetMapping(value = "/show/{num}", produces = { "application/hal+json" })
    @ResponseStatus(HttpStatus.OK)
    public AccountDto get(@Parameter(description = "Account number") @PathVariable("num") String num) {
        AccountDto accRes = accountMapper.map(accountService.getByAccountNumber(num), AccountDto.class);
        accRes.add(linkTo(methodOn(AccountController.class).findAll()).withRel("list"));
        return accRes;
    }

    @Operation(summary = "Add account")
    @WrapException(errorCode = ADD_ACCOUNT_FAILED)
    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto add(@Valid @RequestBody AccountDto accountDto){
        accountDto.setId(null);
        return accountMapper.map(
                accountService.add(accountMapper.map(accountDto, Account.class)),
                AccountDto.class);
    }

    @Operation(summary = "Update account")
    @WrapException(errorCode = UPDATE_ACCOUNT_FAILED)
    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public AccountDto updateAccount(@Valid @RequestBody AccountDto accountDto){
        return accountMapper.map(
                accountService.update(accountMapper.map(accountDto, Account.class)),
                AccountDto.class);
    }

    @Operation(summary = "Delete account by account number")
    @WrapException(errorCode = DELETE_ACCOUNT_FAILED)
    @RequestMapping(value="/delete/{num}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@Parameter(description = "Account number")  @PathVariable("num") String num){
        accountService.delete(num);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "TopUp account")
    @WrapException(errorCode = TOPUP_FAILED)
    @PostMapping(value = "/topUp", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto topUp(@Parameter(description = "Account number") @RequestParam String accountNumber,
                            @Parameter(description = "Major money value") @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer moneyMajor,
                            @Parameter(description = "Minor money value") @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer moneyMinor) {
        return accountMapper.map(
                accountService.topUp(accountNumber,
                        moneyMapper.map(new MoneyDto(moneyMajor, moneyMinor), Money.class)),
                AccountDto.class);
    }

    @Operation(summary = "Withdraw account")
    @WrapException(errorCode = WITHDRAW_FAILED)
    @PostMapping(value = "/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto withdraw(@Parameter(description = "Account number") @RequestParam String accountNumber,
                               @Parameter(description = "Major money value") @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer moneyMajor,
                               @Parameter(description = "Minor money value") @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer moneyMinor) {
        return accountMapper.map(
                accountService.withdraw(accountNumber,
                        moneyMapper.map(new MoneyDto(moneyMajor, moneyMinor), Money.class)),
                AccountDto.class);
    }

    @Operation(summary = "Transfer money between accounts, return destination account")
    @WrapException(errorCode = TRANSFER_FAILED)
    @PostMapping(value = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto transfer(@Parameter(description = "Source account number") @RequestParam String srcAccountNumber,
                               @Parameter(description = "Destination account number") @RequestParam String dstAccountNumber,
                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer moneyMajor,
                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer moneyMinor) {
        return accountMapper.map(
                accountService.transfer(srcAccountNumber, dstAccountNumber,
                        moneyMapper.map(new MoneyDto(moneyMajor, moneyMinor), Money.class)),
                AccountDto.class);
    }
}
