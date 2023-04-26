package com.travel.trip.tide.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.trip.tide.user.model.error.UserValidationErrorResponseModel;
import com.travel.trip.tide.user.model.registration.UserRegistrationRequestModel;
import com.travel.trip.tide.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserControllerTest {

    private static final String USER_CONTROLLER_URL = "/api/v1/users";

    private static MongoDBContainer container =
            new MongoDBContainer(DockerImageName.parse("mongo"));

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getConnectionString);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeAll
    static void setup() {
        container.start();
    }

    @BeforeEach
    void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void exceptionShouldBeThrownWhenNonValidDataComesIn() throws Exception {
        //GIVEN
        var userRegistrationRequestModel = new UserRegistrationRequestModel(
                "John", "123321312", "some@email.com",
                "payrdE1$", "+3801133232"
        );

        var registerRequestUrl = USER_CONTROLLER_URL + "/register";
        var invalidUserRequest = new UserValidationErrorResponseModel(
                "user.validation.error",
                Collections.singletonList("lastName: The field should contain only letters and be at least 3 characters long")
        );

        var requestModelString = objectMapper.writeValueAsString(userRegistrationRequestModel);

        mockMvc.perform(
                post(registerRequestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestModelString)
                )
                .andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.errorCode")
                        .value(invalidUserRequest.getErrorCode()))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]")
                        .value(invalidUserRequest.getErrors().get(0)));

    }

}
