package org.woven.digicommerce.userservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woven.digicommerce.userservice.entity.User;
import org.woven.digicommerce.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(user);
    }
    
    public List<User> createUsers(List<User> users) {
        return userRepository.saveAll(users);
    }
    
    public User updateUser(Long id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(user.getEmail());
                    existingUser.setFirstName(user.getFirstName());
                    existingUser.setLastName(user.getLastName());
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}