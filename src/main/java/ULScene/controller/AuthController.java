package ULScene.controller;

import ULScene.dto.RegisterRequest;
import ULScene.exceptions.ULSceneException;
import ULScene.model.User;
import ULScene.respository.UserRepository;
import ULScene.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
        if(!registerRequest.getEmail().contains("@studentmail.ul.ie")){
            return new ResponseEntity<>("You must register with a UL student email!", HttpStatus.OK);
        }else  if(userRepository.existsByEmail(registerRequest.getEmail()) && !userRepository.existsByUsername(registerRequest.getUsername())){
            User foundUser = userRepository.findByEmail(registerRequest.getEmail()).orElseThrow(() -> new ULSceneException("User not found with name" + registerRequest.getUsername()));
            if(foundUser.isEnabled() == false) {
                authService.signup(registerRequest);
                return new ResponseEntity<>("User has been Registered in the system", HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("Username or email already exists", HttpStatus.OK);
            }

        }
        else if(!userRepository.existsByEmail(registerRequest.getEmail()) && userRepository.existsByUsername(registerRequest.getUsername())){
                return new ResponseEntity<>("Username already exists", HttpStatus.OK);
            }


        else if(userRepository.existsByEmail(registerRequest.getEmail()) && userRepository.existsByUsername(registerRequest.getUsername())){

                return new ResponseEntity<>("Username or email already exists", HttpStatus.OK);
        }


        else if(!userRepository.existsByEmail(registerRequest.getEmail()) && !userRepository.existsByUsername(registerRequest.getUsername())){
            authService.signup(registerRequest);
            return new ResponseEntity<>("User has been Registered in the system", HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>("Username or email already exists", HttpStatus.OK);
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

    }



