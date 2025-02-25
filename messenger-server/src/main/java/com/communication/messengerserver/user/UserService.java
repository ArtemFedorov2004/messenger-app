package com.communication.messengerserver.user;

import com.communication.messengerserver.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public void editUser(String userId, User userToEdit) {
        Optional<User> existingUserOptional = userRepository.findById(userId);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            existingUser.setUsername(userToEdit.getUsername());
            existingUser.setFirstname(userToEdit.getFirstname());
            existingUser.setLastname(userToEdit.getLastname());
            existingUser.setEmail(userToEdit.getEmail());
            existingUser.setAddress(userToEdit.getAddress());
            existingUser.setCity(userToEdit.getCity());
            existingUser.setCountry(userToEdit.getCountry());
            existingUser.setPostalCode(userToEdit.getPostalCode());
            existingUser.setAboutMe(userToEdit.getAboutMe());

            userRepository.save(existingUser);
        }
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " does not exist"));
    }
}
