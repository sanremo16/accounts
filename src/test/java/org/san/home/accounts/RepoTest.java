package org.san.home.accounts;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.san.home.accounts.jpa.AccountRepository;
import org.san.home.accounts.jpa.ClientRepository;
import org.san.home.accounts.model.Account;
import org.san.home.accounts.model.Client;
import org.san.home.accounts.model.CurrencyType;
import org.san.home.accounts.model.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.transaction.Transactional;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class RepoTest {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Test
    @DatabaseSetup({"/dataset/client.xml"})
    public void testClientRepo() {
        Client c1 = new Client(new Long(100), "123", "456", "789");
        assertEquals(c1, clientRepository.findById(new Long(100)).get());

        Client c2 = new Client(null, "2", "3", "4");
        c2 = clientRepository.saveAndFlush(c2);
        assertEquals(c2, clientRepository.findById(c2.getId()).get());

        clientRepository.deleteById(c2.getId());
        clientRepository.flush();
        assertFalse(clientRepository.findById(c2.getId()).isPresent());
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void testAccountRepo() {
        Account a1 = new Account(new Long(100), "11111111111111111111", ModelUtils.bigDecimalToMoney(new BigDecimal(10), CurrencyType.RUR));
        assertEquals(a1, accountRepository.findById(a1.getId()).get());

        Account a2 = new Account(null, "22222222222222222222", ModelUtils.bigDecimalToMoney(new BigDecimal(20), CurrencyType.USD));
        a2 = accountRepository.saveAndFlush(a2);
        assertEquals(a2, accountRepository.findById(a2.getId()).get());

        accountRepository.deleteById(a2.getId());
        accountRepository.flush();
        assertFalse(accountRepository.findById(a2.getId()).isPresent());
    }
}
