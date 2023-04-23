package com.travel.trip.tide.user.service;

import com.travel.trip.tide.user.entity.User;
import com.travel.trip.tide.user.model.registration.UserRegistrationRequestModel;
import com.travel.trip.tide.user.model.registration.UserRegistrationResponseModel;
import com.travel.trip.tide.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private UserService userService;

    @Test
    void userShouldBeFoundById() {

        //GIVEN

        var user = User.builder().build();
        var expectedUser = new UserRegistrationResponseModel();
        var userRegistrationRequestModel = new UserRegistrationRequestModel();

        //WHEN

        when(userRepository.save(user))
                .thenReturn(user);
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

}
