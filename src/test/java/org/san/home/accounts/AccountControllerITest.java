package org.san.home.accounts;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.san.home.accounts.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private AccountService accountService;


    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void getAll() throws Exception {
        this.mockMvc.perform(get("http://localhost:"+ port + "/accounts/list")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(100, 200)))
                .andExpect(jsonPath("$[*].links[*].rel", containsInAnyOrder("self", "self")));
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void getByAccNum() throws Exception {
        this.mockMvc.perform(get("http://localhost:"+ port + "/accounts/show/11111111111111111111")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)));
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void delete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:"+ port + "/accounts/delete/11111111111111111111")).andDo(print())
                .andExpect(status().isOk());
        assertEquals(1, accountService.findAll().size());
    }

    @Test
    public void add() throws Exception {
        String s = "{\"num\":\"555\",\"currencyType\":\"USD\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:"+ port + "/accounts/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(s))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void update() throws Exception {
        String s = "{\"num\":\"11111111111111111111\",\"currencyType\":\"USD\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("http://localhost:"+ port + "/accounts/update")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(s))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyType", is("USD")));
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


    @Test
    @DatabaseSetup({"/dataset/account.xml"})
    public void validationTest() throws Exception {
        String s = "{\"num\":\"-11111111111111111111\",\"currencyType\":\"USD\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("http://localhost:"+ port + "/accounts/update")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(s))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is(14)));
    }

}
