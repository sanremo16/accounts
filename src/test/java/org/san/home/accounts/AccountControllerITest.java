package org.san.home.accounts;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import javax.transaction.Transactional;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class AccountControllerITest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void getAll() throws Exception {
        this.mockMvc.perform(get("http://localhost:"+ port + "/accounts")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(100, 200)));
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void getByAccNum() throws Exception {
        this.mockMvc.perform(get("http://localhost:"+ port + "/accounts/11111111111111111111")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)));
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void topUp() throws Exception {
        this.mockMvc.perform(post("http://localhost:"+ port + "/accounts/topUp")
                .param("accountNumber", "11111111111111111111")
                .param("moneyMajor", "10")
                .param("moneyMinor", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance.major", is(20)))
                .andExpect(jsonPath("$.balance.minor", is(10)));

        this.mockMvc.perform(post("http://localhost:"+ port + "/accounts/topUp")
                .param("accountNumber", "22222222222222222222")
                .param("moneyMajor", "10")
                .param("moneyMinor", "110"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance.major", is(31)))
                .andExpect(jsonPath("$.balance.minor", is(10)));
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void withdraw() throws Exception {
        this.mockMvc.perform(post("http://localhost:"+ port + "/accounts/withdraw")
                .param("accountNumber", "11111111111111111111")
                .param("moneyMajor", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance.major", is(5)));
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void withdraw_noMoney() throws Exception {
        this.mockMvc.perform(post("http://localhost:"+ port + "/accounts/withdraw")
                .param("accountNumber", "11111111111111111111")
                .param("moneyMajor", "15"))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void transfer() throws Exception {
        this.mockMvc.perform(post("http://localhost:"+ port + "/accounts/transfer")
                .param("srcAccountNumber", "22222222222222222222")
                .param("dstAccountNumber", "11111111111111111111")
                .param("moneyMajor", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance.major", is(15)));
    }
}
