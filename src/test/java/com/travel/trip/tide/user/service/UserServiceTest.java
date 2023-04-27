package com.travel.trip.tide.user.service;

import com.travel.trip.tide.user.entity.User;
import com.travel.trip.tide.user.exception.UserEmailAlreadyOccupied;
import com.travel.trip.tide.user.exception.UserNotFoundException;
import com.travel.trip.tide.user.model.UserResponseModel;
import com.travel.trip.tide.user.model.UserUpdateRequestModel;
import com.travel.trip.tide.user.model.UserUpdateResponseModel;
import com.travel.trip.tide.user.model.registration.UserRegistrationRequestModel;
import com.travel.trip.tide.user.model.registration.UserRegistrationResponseModel;
import com.travel.trip.tide.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @Spy
    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void modelMapperSetup() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
    }

    @Test
    void userShouldBeRegistered() {
        //GIVEN

        var user = new User();
        var expectedUser = new UserRegistrationResponseModel();
        var userRegistrationRequestModel = new UserRegistrationRequestModel(
                "Jonh", "Doe", "some@email.com",
                "password", "12345678"
        );

        //WHEN

        when(userRepository.save(user))
                .thenReturn(user);
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(modelMapper.map(userRegistrationRequestModel, User.class))
                .thenReturn(user);
        when(modelMapper.map(user, UserRegistrationResponseModel.class))
                .thenReturn(new UserRegistrationResponseModel());

        var actualUser = userService.register(userRegistrationRequestModel);

        //THEN

        assertEquals(expectedUser, actualUser);

        verify(userService, times(1))
                .register(userRegistrationRequestModel);
        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void shouldThrowAnErrorWhenRegisteringAndEmailIsAlreadyOccupied() {
        //GIVEN

        var user = new User();
        var userRegistrationRequestModel = new UserRegistrationRequestModel(
                "Jonh", "Doe", "some@email.com",
                "password", "12345678"
        );

        //WHEN

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        //THEN

        assertThrows(UserEmailAlreadyOccupied.class, () -> {
            userService.register(userRegistrationRequestModel);
        });

        verify(userService, times(1))
                .register(userRegistrationRequestModel);
        verify(userRepository, never())
                .save(user);
    }

    @Test
    void shouldReturnUserById() {
        //GIVEN

        var user = new User();
        var expectedUserModel = new UserResponseModel();

        //WHEN

        when(userRepository.findById(anyString()))
                .thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseModel.class))
                .thenReturn(expectedUserModel);

        var actualUserModel = userService.getUserById(anyString());

        //THEN

        assertEquals(expectedUserModel, actualUserModel);

        verify(userService, times(1))
                .getUserById(anyString());
        verify(userRepository, times(1))
                .findById(anyString());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenNonExistingUserIdForGetUserById() {
        //GIVEN

        var nonExistingUserId = "nonExistingId";

        //WHEN

        when(userRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        //THEN

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(nonExistingUserId);
        });

        verify(userService, times(1))
                .getUserById(anyString());
        verify(userRepository, times(1))
                .findById(anyString());
    }

    @Test
    void shouldReturnUserByEmail() {
        //GIVEN

        var user = new User();
        var expectedUserModel = new UserResponseModel();

        //WHEN

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseModel.class))
                .thenReturn(expectedUserModel);

        var actualUserModel = userService.getUserByEmail(anyString());

        //THEN

        assertEquals(expectedUserModel, actualUserModel);

        verify(userService, times(1))
                .getUserByEmail(anyString());
        verify(userRepository, times(1))
                .findByEmail(anyString());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenProvidedNonExistingEmailForGettingUserByEmail() {
        //GIVEN

        var nonExistingUserEmail = "non.existing.email@gmail.com";

        //WHEN

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        //THEN

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByEmail(nonExistingUserEmail);
        });

        verify(userService, times(1))
                .getUserByEmail(anyString());
        verify(userRepository, times(1))
                .findByEmail(anyString());
    }

    @Test
    void shouldUpdateTheUser() {
        //GIVEN

        var user = new User();
        var userId = "userId";
        var userUpdateModel = UserUpdateRequestModel.builder()
                .lastName("Carlos")
                .build();
        var expectedUserModel = new UserUpdateResponseModel();

        //WHEN

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        when(userRepository.save(user))
                .thenReturn(user);
        when(userRepository.findById(anyString()))
                .thenReturn(Optional.of(user));
        doNothing().when(modelMapper).map(userUpdateModel, user);
        when(modelMapper.map(user, UserUpdateResponseModel.class))
                .thenReturn(expectedUserModel);

        var actualUserModel = userService
                .updateUser(userId, userUpdateModel);

        //THEN

        assertEquals(expectedUserModel, actualUserModel);

        verify(userService, times(1))
                .updateUser(anyString(), any(UserUpdateRequestModel.class));
        verify(userRepository, times(1))
                .findById(anyString());
        verify(userRepository, times(1))
                .save(user);
        verify(userRepository, never())
                .findByEmail(anyString());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUpdatingWithWrongId() {
        //GIVEN

        var user = new User();
        var wrongUserId = "userId";
        var userUpdateRequestModel = UserUpdateRequestModel.builder()
                .lastName("Carlos")
                .build();

        //WHEN

        when(userRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        //THEN

        assertThrows(UserNotFoundException.class, () -> userService
                .updateUser(wrongUserId, userUpdateRequestModel));

        verify(userService, times(1))
                .updateUser(anyString(), any(UserUpdateRequestModel.class));
        verify(userRepository, times(1))
                .findById(anyString());
        verify(userRepository, never())
                .save(user);
        verify(userRepository, never())
                .findByEmail(anyString());
    }

    @Test
    void shouldThrowUserEmailAlreadyOccupiedWhenUpdatingTheUserEmail() {
        //GIVEN

        var duplicationEmail = "some@email.com";
        var user = new User();
        user.setEmail(duplicationEmail);
        var userId = "userId";
        var userUpdateRequestModel = UserUpdateRequestModel.builder()
                .email(duplicationEmail)
                .build();

        //WHEN
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        //THEN

        assertThrows(UserEmailAlreadyOccupied.class, () -> userService
                .updateUser(userId, userUpdateRequestModel));

        verify(userService, times(1))
                .updateUser(anyString(), any(UserUpdateRequestModel.class));
        verify(userRepository, never())
                .findById(anyString());
        verify(userRepository, never())
                .save(user);
        verify(userRepository, times(1))
                .findByEmail(anyString());
    }

}
