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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<UserResponseModel> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponseModel.class))
                .toList();
    }

    public UserRegistrationResponseModel register(
            UserRegistrationRequestModel userRegistrationModel) {
        var email = userRegistrationModel.getEmail();
        checkIfUserWithThisEmailAlreadyExists(email);

        var user = modelMapper.map(userRegistrationModel, User.class);
        var registeredUser = userRepository.save(user);
        log.info("User is successfully registered");

        return modelMapper
                .map(registeredUser, UserRegistrationResponseModel.class);
    }

    public UserUpdateResponseModel updateUser(
            String id, UserUpdateRequestModel userUpdateModel) {
        var email = userUpdateModel.getEmail();
        checkIfUserWithThisEmailAlreadyExists(email);
        var user = findUserByIdOrElseThrowNotFound(id);
        modelMapper.map(userUpdateModel, user);
        user.setId(id);
        var updatedUser = userRepository.save(user);
        log.info("User was successfully updated");

        return modelMapper
                .map(updatedUser, UserUpdateResponseModel.class);
    }

    public UserResponseModel getUserById(String id) {
        var foundUser = findUserByIdOrElseThrowNotFound(id);
        log.info("User is successfully retrieved by id");

        return modelMapper.map(foundUser, UserResponseModel.class);
    }

    public UserResponseModel getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> modelMapper.map(user, UserResponseModel.class))
                .orElseThrow(() -> {
                    log.error("User was not found by given email " + email);
                    return new UserNotFoundException(
                            "user.not.found.by.email",
                            "user was not found by the provided email " + email
                    );
                });
    }

    private void checkIfUserWithThisEmailAlreadyExists(
            String email) {
        if (Objects.nonNull(email)) {
            userRepository.findByEmail(email)
                    .ifPresent(user -> {
                        log.error("User with provided email already exists: " + email);
                        throw new UserEmailAlreadyOccupied(
                                "user.email.already.occupied",
                                "The given email is already occupied: " + email
                        );
                    });
        }
    }

    private User findUserByIdOrElseThrowNotFound(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                            log.error("User was not found by given id " + id);
                            return new UserNotFoundException(
                                    "user.not.found.by.id",
                                    "user was not found by the provided id " + id
                            );
                        }
                );
    }
}
