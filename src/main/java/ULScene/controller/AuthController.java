package ULScene.controller;

import ULScene.dto.LoginRequest;
import ULScene.dto.LoginResponse;
import ULScene.dto.RefreshTokenRequest;
import ULScene.dto.RegisterRequest;
import ULScene.exceptions.ULSceneException;
import ULScene.model.User;
import ULScene.respository.UserRepository;
import ULScene.security.JwtProvider;
import ULScene.service.AuthService;
import ULScene.service.RefreshTokenService;
import ULScene.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtTokenUtil;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
        if(registerRequest.getPassword().length() < 6){
            return new ResponseEntity<>("Password too short", HttpStatus.BAD_REQUEST);
        }
        if(!registerRequest.getEmail().contains("@studentmail.ul.ie")){
            return new ResponseEntity<>("You must register with a UL student email!", HttpStatus.BAD_REQUEST);
        }else  if(userRepository.existsByEmail(registerRequest.getEmail()) && !userRepository.existsByUsername(registerRequest.getUsername())){
            User foundUser = userRepository.findByEmail(registerRequest.getEmail()).orElseThrow(() -> new ULSceneException("User not found with name" + registerRequest.getUsername()));
            if(foundUser.isEnabled() == false) {
                authService.signup(registerRequest);
                return new ResponseEntity<>("User has been Registered in the system", HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("Username or email already exists", HttpStatus.BAD_REQUEST);
            }

        }
        else if(!userRepository.existsByEmail(registerRequest.getEmail()) && userRepository.existsByUsername(registerRequest.getUsername())){
                return new ResponseEntity<>("Username already exists", HttpStatus.NOT_ACCEPTABLE);
            }


        else if(userRepository.existsByEmail(registerRequest.getEmail()) && userRepository.existsByUsername(registerRequest.getUsername())){

                return new ResponseEntity<>("Username or email already exists", HttpStatus.BAD_REQUEST);
        }


        else if(!userRepository.existsByEmail(registerRequest.getEmail()) && !userRepository.existsByUsername(registerRequest.getUsername())){
            authService.signup(registerRequest);
            return new ResponseEntity<>("User has been Registered in the system", HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>("Username or email already exists", HttpStatus.BAD_REQUEST);
        }

        /*
        The above code will check if the username or password being entered already exists, if it does we then will check
        if the account is enabled, if it is not enabled the user can register with this username/password
        The code also checks if the above users email is a student email
         */
    }
    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("You have successfully completed registration",HttpStatus.OK);
    }
    @PostMapping("/login")
    public LoginResponse createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {
            try {
                Authentication authenticate = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authenticate);
            } catch (BadCredentialsException e) {
                throw new Exception("Incorrect username or password");
            }
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());
            final String jwt = jwtTokenUtil.generateToken(userDetails);
            //final String username = jwtTokenUtil.
            //return ResponseEntity.ok(new AuthenticationResponse(jwt));
            return LoginResponse.builder()
                    .jwt(jwt)
                    .username(authenticationRequest.getUsername())
                    .expiresAt(Instant.now().plusMillis(jwtTokenUtil.getJwtExpirationinMillis()))
                    .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                    .build();
            //.refreshToken(refreshTokenService.generateRefreshToken().getToken())

    }
    @GetMapping("/currentUser")
    public ResponseEntity<User> getCurrentUser(){
        return ResponseEntity.ok(authService.getCurrentUser());
    }
    @GetMapping("/currentUsername")
    public ResponseEntity<String> getCurrentUsername(){
        return ResponseEntity.ok(authService.getCurrentUsername());
    }
    @PostMapping("/refresh/token")
    public LoginResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body("Refresh token deleted successfully");
    }
    @PostMapping("/banUser")
    public ResponseEntity banUser(@RequestBody String username){
        return ResponseEntity.status(HttpStatus.OK).body(authService.banUserByName(username));
    }
    @PostMapping("/unBanUser")
    public ResponseEntity unBanUser(@RequestBody String username){
        return ResponseEntity.status(HttpStatus.OK).body(authService.unBanUser(username));
    }
    @GetMapping("/checkBanned/{name}")
    public ResponseEntity<Boolean> checkBanned(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(authService.isBanned(name));
    }
    @GetMapping("/checkBanned")
    public ResponseEntity<Boolean> checkBanned(){
        return ResponseEntity.status(HttpStatus.OK).body(authService.isCurrentUserBanned());
    }
    @PostMapping("/addAdmin")
    public ResponseEntity makeAdmin(@RequestBody String username){
        return ResponseEntity.status(HttpStatus.OK).body(authService.addAdmin(username));
    }
    @PostMapping("/removeAdmin")
    public ResponseEntity removeAdmin(@RequestBody String username){
        return ResponseEntity.status(HttpStatus.OK).body(authService.removeAdmin(username));
    }

}



