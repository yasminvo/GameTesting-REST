package br.ufscar.dc.dsw.GameTesting;

import br.ufscar.dc.dsw.GameTesting.enums.Role;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.findByName("admin").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("senha123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            logger.info("Admin default user was created");
        }
        if(userRepository.findByName("tester").isEmpty()) {
            User admin = new User();
            admin.setName("tester");
            admin.setEmail("tester@example.com");
            admin.setPassword(passwordEncoder.encode("senha123"));
            admin.setRole(Role.TESTER);
            userRepository.save(admin);
            logger.info("Tester default user was created");
        }

        if(userRepository.findByName("guest").isEmpty()) {
            User admin = new User();
            admin.setName("guest");
            admin.setEmail("guest@example.com");
            admin.setPassword(passwordEncoder.encode("senha123"));
            admin.setRole(Role.GUEST);
            userRepository.save(admin);
            logger.info("Guest default user was created");
        }

    }
}
