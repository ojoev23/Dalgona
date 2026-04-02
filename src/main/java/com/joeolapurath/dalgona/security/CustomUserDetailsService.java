package com.joeolapurath.dalgona.security;

import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.model.Role;
import com.joeolapurath.dalgona.repository.AccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findByEmail(username);
        if(optionalAccount.isEmpty()){
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        Account account = optionalAccount.get();
        Role role = account.getRole() == null ? Role.USER : account.getRole();
        return new User(
                account.getEmail(),
                account.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
    }

}
