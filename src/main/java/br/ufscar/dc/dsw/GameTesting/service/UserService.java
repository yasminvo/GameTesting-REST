package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.dtos.UserDTO;
import br.ufscar.dc.dsw.GameTesting.enums.Role;
import br.ufscar.dc.dsw.GameTesting.exceptions.AppException;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    public List<User> findAll() {
        return userRepository.findAll()
                .stream()
                .toList();
    }

    public User findById(Long id, Locale locale) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        messageSource.getMessage("user.notfound", null, locale),
                        HttpStatus.NOT_FOUND));
    }

    public User create(User user, Role role, Locale locale) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new AppException(
                    messageSource.getMessage("user.email.already_registered", null, locale),
                    HttpStatus.BAD_REQUEST);
        }
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(Long id, User userDetails, Locale locale) {
        User existingUser = findById(id, locale);

        userRepository.findByEmail(userDetails.getEmail())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new AppException(
                            messageSource.getMessage("user.email.already_registered_other", null, locale),
                            HttpStatus.BAD_REQUEST);
                });

        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public void delete(Long id, Locale locale) {
        User existingUser = findById(id, locale);
        try {
            userRepository.delete(existingUser);
        } catch (Exception e) {
            throw new AppException(
                    messageSource.getMessage("user.delete.error", new Object[]{e.getMessage()}, locale),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<UserDTO> getUsersByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        return userRepository.findAllById(ids)
                .stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
