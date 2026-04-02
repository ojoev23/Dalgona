package com.joeolapurath.dalgona.controller;

import com.joeolapurath.dalgona.dto.AuthResponse;
import com.joeolapurath.dalgona.dto.LoginRequest;
import com.joeolapurath.dalgona.dto.RegisterRequest;
import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.model.Role;
import com.joeolapurath.dalgona.repository.AccountRepository;
import com.joeolapurath.dalgona.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AccountRepository accountRepository, PasswordEncoder passwordEncoder){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){

        // Takes email & pwd and checks against db
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
        ));

        // If at this step login was good and now store users info in memory
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication.getName());
        Account account = accountRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return ResponseEntity.ok(new AuthResponse(jwt, account.getRole().name()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
        if(accountRepository.findByEmail(request.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("Email already in use");
        }
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(Role.USER);
        accountRepository.save(account);

        return ResponseEntity.ok("Registered User");
    }

}
