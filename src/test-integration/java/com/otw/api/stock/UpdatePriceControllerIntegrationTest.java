package com.otw.api.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
public class UpdatePriceControllerIntegrationTest {

    @MockBean
    private StockService stockService;

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
    void should_fail_if_request_body_is_invalid() throws Exception {
        String invalidRequest = "{request:invalid}";
        postUpdatePriceRequest(invalidRequest, status().isBadRequest());
    }

    @Test
    void should_fail_if_request_body_does_not_include_all_mandatory_parameters() throws Exception {
        String invalidRequest = "{stockName:name}";
        postUpdatePriceRequest(invalidRequest, status().isBadRequest());
    }

    @Test
    void should_fail_if_stock_not_found() throws Exception {
        doThrow(StockNotFoundException.class).when(stockService).updatePrice("name", BigDecimal.ONE);
        String request = objectMapper.writeValueAsString(new UpdatePriceRequest("name", BigDecimal.ONE));
        postUpdatePriceRequest(request, status().isNotFound());
    }

    @Test
    void should_update_stock_price_successfully() throws Exception {
        String request = objectMapper.writeValueAsString(new UpdatePriceRequest("name", BigDecimal.ONE));
        postUpdatePriceRequest(request, status().isOk());
    }

    private void postUpdatePriceRequest(String request, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(post("/stock/updatePrice")
                .with(user("user").roles("USER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(expectedStatus);
    }
}