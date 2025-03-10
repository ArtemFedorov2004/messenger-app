package com.communication.messengerserver.user;

import com.communication.messengerserver.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<User> searchUsersByQuery(String userId, String query) {
        List<User> users = userRepository.findByUsernameIgnoreCaseContaining(query);

        return users.stream()
                .filter(user -> !user.getId().equals(userId))
                .collect(Collectors.toList());
    }

    public String getUsernameById(String userId) {
        return userRepository.getUsernameById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " does not exist"));
    }
}
