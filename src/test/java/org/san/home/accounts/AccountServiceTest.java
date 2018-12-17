package org.san.home.accounts;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.san.home.accounts.jpa.AccountRepository;
import org.san.home.accounts.model.Account;
import org.san.home.accounts.model.CurrencyType;
import org.san.home.accounts.model.ModelUtils;
import org.san.home.accounts.service.AccountService;
import org.san.home.accounts.service.error.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void testTopUp() {
        BigDecimal plus = new BigDecimal(10);
        Money m = ModelUtils.bigDecimalToMoney(plus, CurrencyType.RUR);
        Account ac = accountRepository.findById(new Long(100)).get();
        accountService.topUp(ac.getNum(), m);
        assertEquals(m.plus(plus), ac.getBalance());
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void testWithdraw() {
        BigDecimal minus = new BigDecimal(5);
        Money m = ModelUtils.bigDecimalToMoney(minus, CurrencyType.RUR);
        Account ac = accountRepository.findById(new Long(100)).get();
        accountService.withdraw(ac.getNum(), m);
        assertEquals(m, ac.getBalance());
    }

    @Test(expected = CommonException.class)
    @DatabaseSetup({"/dataset/account.xml"})
    public void testWithdrawTooMuchMoney() {
        BigDecimal minus = new BigDecimal(15);
        Money m = ModelUtils.bigDecimalToMoney(minus, CurrencyType.RUR);
        Account ac = accountRepository.findById(new Long(100)).get();
        accountService.withdraw(ac.getNum(), m);
        assertEquals(m.minus(minus), ac.getBalance());
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void testTransfer() {
        BigDecimal bd = new BigDecimal(10);
        Money m = ModelUtils.bigDecimalToMoney(bd, CurrencyType.RUR);
        Account ac1 = accountRepository.findById(new Long(100)).get();
        Account ac2 = accountRepository.findById(new Long(200)).get();
        accountService.transfer(ac2.getNum(), ac1.getNum(), m);
        assertEquals(m.plus(bd), ac1.getBalance());
        assertEquals(m, ac2.getBalance());
    }


}
