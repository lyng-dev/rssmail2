package com.rssmail.controllers;

import com.rssmail.TestAppConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith({
  SpringExtension.class, 
  MockitoExtension.class
})
@Import(TestAppConfig.class)
public class SubscriptionControllerTests {

    @Autowired
    private MockMvc mvc;

	@Test
	void testCreateSubscription() throws Exception {
    //Arrange
    //Act
        ResultActions result =  this.mvc.perform(post("/subscription/create"))
      .andExpect(status().isOk()); //Assert

	}

    @Test
    void testDeleteSubscription() throws Exception {
        //Arrange
        //Act
        ResultActions result =  this.mvc.perform(delete("/subscription/delete"))
                .andExpect(status().isOk()); //Assert

    }

}
