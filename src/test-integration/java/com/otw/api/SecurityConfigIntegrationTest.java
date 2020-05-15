package com.otw.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otw.api.exchange.AddStockToExchangeRequest;
import com.otw.api.exchange.ExchangeController;
import com.otw.api.exchange.ExchangeService;
import com.otw.api.stock.CreateStockRequest;
import com.otw.api.stock.StockController;
import com.otw.api.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ExchangeController.class, StockController.class})
public class SecurityConfigIntegrationTest {

    @MockBean
    private StockService stockService;

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
    void should_fail_stock_controller_request_if_user_not_authenticated() throws Exception {
        mockMvc.perform(post("/stock/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_fail_stock_controller_request_if_user_has_incorrect_role() throws Exception {
        mockMvc.perform(post("/stock/create")
                .with(user("guest").roles("ANONYMOUS"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void should_allow_stock_controller_request_if_user_authenticated() throws Exception {
        String request = objectMapper.writeValueAsString(new CreateStockRequest("name", "description"));
        mockMvc.perform(post("/stock/create")
                .with(user("user").roles("USER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void should_fail_exchange_controller_request_if_user_not_authenticated() throws Exception {
        mockMvc.perform(post("/exchange/addStock")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_fail_exchange_controller_request_if_user_has_incorrect_role() throws Exception {
        mockMvc.perform(post("/exchange/addStock")
                .with(user("guest").roles("ANONYMOUS"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void should_allow_exchange_controller_request_if_user_authenticated() throws Exception {
        mockMvc.perform(post("/exchange/addStock")
                .with(user("user").roles("USER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AddStockToExchangeRequest("stock", "exchange")))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void should_prevent_access_to_swagger_docs_if_not_authenticated() throws Exception {
        mockMvc.perform(get("/swagger-ui.html")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_allow_access_to_swagger_docs_if_authenticated() throws Exception {
        mockMvc.perform(get("/swagger-ui.html")
                .with(user("user").roles("USER"))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
