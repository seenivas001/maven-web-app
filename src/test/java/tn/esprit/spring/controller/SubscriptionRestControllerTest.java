package tn.esprit.spring.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.controllers.SubscriptionRestController;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.services.ISubscriptionServices;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionRestController.class)
 class SubscriptionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISubscriptionServices subscriptionServices;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void testUpdateSubscription() throws Exception {
        Subscription updatedSub = new Subscription();
        updatedSub.setNumSub(4L);
        updatedSub.setPrice(200.0f);

        when(subscriptionServices.updateSubscription(any(Subscription.class))).thenReturn(updatedSub);

        mockMvc.perform(put("/subscription/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSub)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(200.0));
    }


}
