package com.rssmail.controllers;

import com.rssmail.TestAppConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith({
  SpringExtension.class, 
  MockitoExtension.class
})
@Import(TestAppConfig.class)
public class HealthControllerTests {

    @Autowired
    private MockMvc mvc;

	@Test
	void testHealthEndpoint() throws Exception {
    //Arrange
    //Act
        ResultActions result =  this.mvc.perform(get("/health"))
      .andExpect(status().isOk()); //Assert

	}

}
