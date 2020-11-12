package ULScene.service;

import ULScene.dto.RegisterRequest;
import ULScene.exceptions.ULSceneException;
import ULScene.model.NotificationEmail;
import ULScene.model.User;
import ULScene.model.VerificationToken;
import ULScene.respository.UserRepository;
import ULScene.respository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final VerificationTokenRepository verificationTokenRepository;


    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setCreateDate(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);
        String token = generateToken(user);
        mailService.sendMail(new NotificationEmail("Please activiate your account", user.getEmail(), "Click here to activiate account : " + "http://localhost:8080/api/auth/accountVerification/" + token));
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
}