package org.san.home.accounts.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.Money;
import org.san.home.accounts.dto.AccountDto;
import org.san.home.accounts.dto.DtoMapper;
import org.san.home.accounts.dto.MoneyDto;
import org.san.home.accounts.model.Account;
import org.san.home.accounts.service.AccountService;
import org.san.home.accounts.controller.error.WrapException;
import org.san.home.accounts.service.error.CommonException;
import org.san.home.accounts.service.error.ErrorArgument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@Api(description="Simple API for account's operations")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private DtoMapper mapper;

    @ApiOperation(value = "View a list of accounts", response = Iterable.class)
    @WrapException(errorCode = GET_ALL_FAILED)
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<AccountDto> findAll() {
        return accountService.findAll().stream()
                .map(account -> mapper.map(account, AccountDto.class))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "Get account by account number", response = AccountDto.class)
    @WrapException(errorCode = GET_ACCOUNT_FAILED)
    @GetMapping(value = "/show/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AccountDto get(@ApiParam(value = "Account number") @PathVariable("num") String num) {
        return mapper.map(
                accountService.getByAccountNumber(num),
                AccountDto.class);
    }

    @ApiOperation(value = "Add account", response = AccountDto.class)
    @WrapException(errorCode = ADD_ACCOUNT_FAILED)
    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AccountDto add(@RequestBody AccountDto accountDto){
        accountDto.setId(null);
        return mapper.map(
                accountService.add(mapper.map(accountDto, Account.class)),
                AccountDto.class);
    }

    @ApiOperation(value = "Update account", response = AccountDto.class)
    @WrapException(errorCode = UPDATE_ACCOUNT_FAILED)
    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AccountDto updateAccount(@RequestBody AccountDto accountDto){
        return mapper.map(
                accountService.update(mapper.map(accountDto, Account.class)),
                AccountDto.class);
    }

    @ApiOperation(value = "Delete account by account number")
    @WrapException(errorCode = DELETE_ACCOUNT_FAILED)
    @RequestMapping(value="/delete/{num}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@ApiParam(value = "Account number") @PathVariable("num") String num){
        accountService.delete(num);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "TopUp account", response = AccountDto.class)
    @WrapException(errorCode = TOPUP_FAILED)
    @PostMapping(value = "/topUp", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto topUp(@ApiParam(value = "Account number") @RequestParam String accountNumber,
                            @ApiParam(value = "Major money value") @RequestParam(required = false, defaultValue = "0") Integer moneyMajor,
                            @ApiParam(value = "Minor money value") @RequestParam(required = false, defaultValue = "0") Integer moneyMinor) {
        return mapper.map(
                accountService.topUp(accountNumber,
                        mapper.map(new MoneyDto(moneyMajor, moneyMinor), Money.class)),
                AccountDto.class);
    }

    @ApiOperation(value = "Withdraw account", response = AccountDto.class)
    @WrapException(errorCode = WITHDRAW_FAILED)
    @PostMapping(value = "/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto withdraw(@ApiParam(value = "Account number") @RequestParam String accountNumber,
                               @ApiParam(value = "Major money value") @RequestParam(required = false, defaultValue = "0") Integer moneyMajor,
                               @ApiParam(value = "Minor money value") @RequestParam(required = false, defaultValue = "0") Integer moneyMinor) {
        return mapper.map(
                accountService.withdraw(accountNumber,
                        mapper.map(new MoneyDto(moneyMajor, moneyMinor), Money.class)),
                AccountDto.class);
    }

    @ApiOperation(value = "Transfer money between accounts, return destination account", response = AccountDto.class)
    @WrapException(errorCode = TRANSFER_FAILED)
    @PostMapping(value = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto transfer(@ApiParam(value = "Source account number") @RequestParam String srcAccountNumber,
                               @ApiParam(value = "Destination account number") @RequestParam String dstAccountNumber,
                               @RequestParam(required = false, defaultValue = "0") Integer moneyMajor,
                               @RequestParam(required = false, defaultValue = "0") Integer moneyMinor) {
        return mapper.map(
                accountService.transfer(srcAccountNumber, dstAccountNumber,
                        mapper.map(new MoneyDto(moneyMajor, moneyMinor), Money.class)),
                AccountDto.class);
    }
}
