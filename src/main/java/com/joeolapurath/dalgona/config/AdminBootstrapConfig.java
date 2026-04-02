package com.joeolapurath.dalgona.config;

import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.model.Role;
import com.joeolapurath.dalgona.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    public CommandLineRunner adminBootstrapRunner(AccountRepository accountRepository,
                                                  PasswordEncoder passwordEncoder,
                                                  @Value("${dalgona.admin.email:}") String adminEmail,
                                                  @Value("${dalgona.admin.password:}") String adminPassword) {
        return args -> {
            if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
                return;
            }
            if (accountRepository.existsByEmail(adminEmail)) {
                return;
            }

            Account admin = Account.builder()
                    .email(adminEmail.trim())
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();
            accountRepository.save(admin);
        };
    }
}
