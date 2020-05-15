package com.otw.api.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otw.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExchangeController.class)
public class ListExchangeControllerIntegrationTest {

    @MockBean
    private ExchangeService exchangeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    @Test
    void should_fail_if_request_has_no_exchange_name_parameter() throws Exception {
        mockMvc.perform(get("/exchange/list/")
                .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_fail_if_exchange_not_found() throws Exception {
        when(exchangeService.listStocks("exchange")).thenThrow(ExchangeNotFoundException.class);
        getExchangeList(status().isNotFound(), "");
    }

    @Test
    void should_list_empty_exchange_successfully() throws Exception {
        when(exchangeService.listStocks("exchange")).thenReturn(Arrays.asList());
        getExchangeList(status().isOk(), "[]");
    }

    @Test
    void should_list_exchange_successfully() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2020, 5, 10, 12, 00);
        Stock stock1 = new Stock("stock1", "description", localDateTime, BigDecimal.ZERO);
        Stock stock2 = new Stock("stock2", "description", localDateTime, BigDecimal.ZERO);
        when(exchangeService.listStocks("exchange")).thenReturn(Arrays.asList(stock1, stock2));
        getExchangeList(status().isOk(),
                "[{\"id\":0,\"name\":\"stock1\",\"description\":\"description\",\"lastUpdated\":\"2020-05-10 12:00\",\"currentPrice\":0}," +
                        "{\"id\":0,\"name\":\"stock2\",\"description\":\"description\",\"lastUpdated\":\"2020-05-10 12:00\",\"currentPrice\":0}]");
    }

    private void getExchangeList(ResultMatcher expectedStatus, String result) throws Exception {
        mockMvc.perform(get("/exchange/list/exchange")
                .with(user("user").roles("USER"))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(expectedStatus)
                .andExpect(content().string(result));
    }
}
