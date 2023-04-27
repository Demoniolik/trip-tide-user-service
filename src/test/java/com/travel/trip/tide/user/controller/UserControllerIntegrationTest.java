package com.travel.trip.tide.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.trip.tide.user.entity.User;
import com.travel.trip.tide.user.model.UserResponseModel;
import com.travel.trip.tide.user.model.error.ErrorResponseModel;
import com.travel.trip.tide.user.model.error.UserValidationErrorResponseModel;
import com.travel.trip.tide.user.model.registration.UserRegistrationRequestModel;
import com.travel.trip.tide.user.model.registration.UserRegistrationResponseModel;
import com.travel.trip.tide.user.repository.UserRepository;
import com.travel.trip.tide.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserControllerIntegrationTest {

    private static final String USER_CONTROLLER_URL = "/api/v1/users";

    private static final MongoDBContainer container =
            new MongoDBContainer(DockerImageName.parse("mongo"));

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getConnectionString);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    @AfterEach
    void cleanUpMongo() {
        userRepository.deleteAll();
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
                .andExpect(jsonPath("$.errorCode")
                        .value(invalidUserRequest.getErrorCode()))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]")
                        .value(invalidUserRequest.getErrors().get(0)));
    }

    @Test
    void shouldThrowExceptionUserEmailAlreadyOccupied() throws Exception {
        //GIVEN

        var duplicateEmail = "some@email.com";
        var userRegistrationRequestModel = new UserRegistrationRequestModel(
                "John", "Doe", duplicateEmail,
                "payrdE1$", "+3801133232"
        );
        var errorResponseModel = new ErrorResponseModel(
                "user.email.already.occupied",
                "The given email is already occupied: " + duplicateEmail
        );

        var previousUser = new User();
        previousUser.setEmail(duplicateEmail);

        var registerRequestUrl = USER_CONTROLLER_URL + "/register";

        var requestModelString = objectMapper.writeValueAsString(userRegistrationRequestModel);

        //WHEN

        //Registering user with an email that will be the same to the request email
        userRepository.save(previousUser);

        //THEN

        mockMvc.perform(
                        post(registerRequestUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestModelString)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode")
                        .value(errorResponseModel.getErrorCode()))
                .andExpect(jsonPath("$.errorDescription")
                        .value(errorResponseModel.getErrorDescription()));
    }

    @Test
    void shouldRegisterUser() throws Exception {
        //GIVEN
        var userRegistrationRequestModel = new UserRegistrationRequestModel(
                "John", "Doe", "some@email.com",
                "payrdE1$", "+3801133232"
        );

        var expectedUserRegistrationModel = new UserRegistrationResponseModel(
                "id", "John", "Doe",
                "some@email.com", "+3801133232"
        );

        var registerRequestUrl = USER_CONTROLLER_URL + "/register";


        var requestModelString = objectMapper.writeValueAsString(userRegistrationRequestModel);
        var ref = new Object() {
            String obtainedId;
        };

        mockMvc.perform(
                        post(registerRequestUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestModelString)
                )
                .andExpect(status().isCreated())
                .andDo(result -> {
                    var responseAsString = result.getResponse().getContentAsString();
                    ref.obtainedId = objectMapper
                            .readValue(responseAsString, UserRegistrationResponseModel.class)
                            .getId();
                })
                .andExpect(jsonPath("$.id").value(ref.obtainedId))
                .andExpect(jsonPath("$.firstName")
                        .value(expectedUserRegistrationModel.getFirstName()))
                .andExpect(jsonPath("$.lastName")
                        .value(expectedUserRegistrationModel.getLastName()))
                .andExpect(jsonPath("$.email")
                        .value(expectedUserRegistrationModel.getEmail()))
                .andExpect(jsonPath("$.phoneNumber")
                        .value(expectedUserRegistrationModel.getPhoneNumber()));


        userRepository.findById(ref.obtainedId).ifPresentOrElse((user) ->
                        assertEquals(user.getEmail(), expectedUserRegistrationModel.getEmail()),
                com.mongodb.assertions.Assertions::fail);
    }

    @Test
    void shouldReturnUserById() throws Exception {
        //GIVEN

        var user = new User();
        user.setFirstName("First name");
        user.setLastName("Last name");
        var expectedUserResponseModel = new UserResponseModel(
                null, "First name", "Last name", null, null
        );

        //WHEN

        var regiteredUser = userRepository.save(user);
        var userId = regiteredUser.getId();
        expectedUserResponseModel.setId(userId);

        final var getUserByIdUrl = USER_CONTROLLER_URL + "/{id}";

        //THEN

        mockMvc.perform(
                        get(getUserByIdUrl, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUserResponseModel.getId()))
                .andExpect(jsonPath("$.firstName")
                        .value(expectedUserResponseModel.getFirstName()))
                .andExpect(jsonPath("$.lastName")
                        .value(expectedUserResponseModel.getLastName()));
    }

    @Test
    void shouldReturnUserNotFoundErrorResponseWhenUserIdIsWrong() throws Exception {
        //GIVEN

        var user = new User();
        user.setFirstName("First name");
        user.setLastName("Last name");
        final var wrongId = "wrong id";
        var expectedErrorResponseModel = new ErrorResponseModel(
                "user.not.found.by.id",
                "user was not found by the provided id " + wrongId
        );

        //WHEN

        userRepository.save(user);

        final var getUserByIdUrl = USER_CONTROLLER_URL + "/{id}";

        //THEN

        mockMvc.perform(
                        get(getUserByIdUrl, wrongId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode")
                        .value(expectedErrorResponseModel.getErrorCode()))
                .andExpect(jsonPath("$.errorDescription")
                        .value(expectedErrorResponseModel.getErrorDescription()));
    }

    @Test
    void shouldFindUserByEmail() throws Exception {
        //GIVEN

        var user = new User();
        var email = "some@email.com";
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setEmail(email);
        var expectedUserResponseModel = new UserResponseModel(
                null, "First name",
                "Last name", email, null
        );

        //WHEN

        var regiteredUser = userRepository.save(user);
        var userId = regiteredUser.getId();
        expectedUserResponseModel.setId(userId);


        //THEN
        final var getUserByEmailUrl = USER_CONTROLLER_URL + "/search/email";
        var paramsMap = new LinkedMultiValueMap<String, String>();
        paramsMap.add("email", email);

        mockMvc.perform(
                        get(getUserByEmailUrl).params(paramsMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUserResponseModel.getId()))
                .andExpect(jsonPath("$.firstName")
                        .value(expectedUserResponseModel.getFirstName()))
                .andExpect(jsonPath("$.lastName")
                        .value(expectedUserResponseModel.getLastName()))
                .andExpect(jsonPath("$.email")
                        .value(expectedUserResponseModel.getEmail()));
    }

    @Test
    void shouldReturnUserNotFoundErrorResponseWhenNonExistingEmailProvided() throws Exception {
        //GIVEN

        var user = new User();
        var wrongEmail = "wrong@email.com";
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setEmail("correct@email.com");
        var expectedErrorResponseModel = new ErrorResponseModel(
                "user.not.found.by.email",
                "user was not found by the provided email " + wrongEmail
        );

        //WHEN

        userRepository.save(user);

        //THEN
        final var getUserByEmailUrl = USER_CONTROLLER_URL + "/search/email";
        var paramsMap = new LinkedMultiValueMap<String, String>();
        paramsMap.add("email", wrongEmail);

        mockMvc.perform(
                        get(getUserByEmailUrl).params(paramsMap))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode")
                        .value(expectedErrorResponseModel.getErrorCode()))
                .andExpect(jsonPath("$.errorDescription")
                        .value(expectedErrorResponseModel.getErrorDescription()));
    }


}
