package ULScene.service;

import ULScene.dto.LoginRequest;
import ULScene.dto.LoginResponse;
import ULScene.dto.RefreshTokenRequest;
import ULScene.dto.RegisterRequest;
import ULScene.exceptions.ULSceneException;
import ULScene.model.*;
import ULScene.respository.RoleRepository;
import ULScene.respository.UserRepository;
import ULScene.respository.UserRolesRepository;
import ULScene.respository.VerificationTokenRepository;
import ULScene.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.management.relation.Role;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final JwtProvider jwtProvider;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserRolesRepository userRolesRepository;
    private final RoleRepository roleRepository;


    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail (registerRequest.getEmail());
        user.setCreateDate(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);
        String token = generateToken(user);
        mailService.sendMail(new NotificationEmail("Please activiate your account", user.getEmail(), "Click here to activiate account : " + "http://localhost:8080/api/auth/accountVerification/" + token));
        Roles newUser = new Roles();
        newUser.setId((long) 1);
        UserRoles userRoles = new UserRoles();
        userRoles.setUser(user);
        userRoles.setRole(newUser);
        userRolesRepository.save(userRoles);
    }
    @Transactional
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ULSceneException("User not found with name" + username));
        if(userRepository.existsByEmailAndEnabled(user.getEmail(), true)) {
            System.out.println("Email already enabled elsewhere");
        }else{
            System.out.println(userRepository.existsByEmailAndEnabled(user.getEmail(), true));
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
    @Transactional
    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(()-> new ULSceneException("Token not found"));
        fetchUserAndEnable(verificationToken.get());
    }
    public String generateToken(User user){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public boolean isBanned(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new ULSceneException("NF"));
        if(user.isBanned()){
            return true;
        }
        return false;
    }
    public int addAdmin(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new ULSceneException("NF"));
        UserRoles userRole = userRolesRepository.findByUser(user).orElseThrow(()->new ULSceneException("No role found"));
        User admin = getCurrentUser();
        UserRoles adminRole = userRolesRepository.findByUser(admin).orElseThrow(()->new ULSceneException("No role found"));
        Roles makeAdmin = new Roles();
        makeAdmin.setId((long) 2);
        if(adminRole.getRole().getId() == 2){
            userRole.setRole(makeAdmin);
            userRepository.save(user);
            return 1;
        }
        return 0;
    }
    public int removeAdmin(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new ULSceneException("NF"));
        UserRoles userRole = userRolesRepository.findByUser(user).orElseThrow(()->new ULSceneException("No role found"));
        User admin = getCurrentUser();
        Roles makeRegular = new Roles();
        makeRegular.setId((long) 1);
        UserRoles adminRole = userRolesRepository.findByUser(admin).orElseThrow(()->new ULSceneException("No role found"));
        if(adminRole.getRole().getId() == 2){
            userRole.setRole(makeRegular);
            userRepository.save(user);
            return 1;
        }
        return 0;
    }
    public boolean isCurrentUserBanned(){
        User user = getCurrentUser();
        if(user.isBanned()){
            return true;
        }
        return false;
    }
    @Transactional
    public int banUserByName(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ULSceneException("NF"));
        User admin = getCurrentUser();
        UserRoles userRole = userRolesRepository.findByUser(admin).orElseThrow(()->new ULSceneException("No role found"));
        if(userRole.getRole().getId() == 2){
            user.setBanned(true);
            userRepository.save(user);
            return 1;
        }
        return 0;
    }
    @Transactional
    public int unBanUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ULSceneException("NF"));
        User admin = getCurrentUser();
        UserRoles userRole = userRolesRepository.findByUser(admin).orElseThrow(()->new ULSceneException("No role found"));
        if(userRole.getRole().getId() == 2){
            user.setBanned(false);
            userRepository.save(user);
            return 1;
        }
        return 0;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }
    @Transactional(readOnly = true)
    public String getCurrentUsername() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }

    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.createTokenWithUsername(refreshTokenRequest.getUsername());
        return LoginResponse.builder()
                .jwt(token)
                .username(refreshTokenRequest.getUsername())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationinMillis()))
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .build();
    }
    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}
