package es.codeurjc.web.controller;

import es.codeurjc.web.Model.User;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;
import es.codeurjc.web.security.jwt.AuthResponse;
import es.codeurjc.web.security.jwt.AuthResponse.Status;
import es.codeurjc.web.security.jwt.LoginRequest;
import es.codeurjc.web.security.jwt.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/")
public class UserApiController {

    @Autowired
    private UserLoginService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidateService validateService;

    @Autowired
    private UserService userService2;

    @PostMapping("auth/login")
    public ResponseEntity<AuthResponse> login(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestBody LoginRequest loginRequest) {

        return userService.login(loginRequest, accessToken, refreshToken);
    }

    @PostMapping("auth/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        return userService.refresh(refreshToken);
    }

    @PostMapping("auth/logout")
    public ResponseEntity<AuthResponse> logOut(HttpServletRequest request, HttpServletResponse response) {

        return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(request, response)));
    }

    @PostMapping("auth/signup")
    public ResponseEntity<?> createUser(@RequestBody User user) throws IOException {
        String error = validateService.validateUser(user);
        if (error != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", error);
            response.put("user", user);
            return ResponseEntity.badRequest().body(response);
        } else {
            user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
            userService2.saveUser(user);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getUserid()).toUri();
            return ResponseEntity.created(location).body(user);
        }
    }
    @GetMapping("users")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if(principal != null) {
            return ResponseEntity.ok(userService2.findAll());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("users/me")
    public ResponseEntity<User> me(HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if(principal != null) {
            return ResponseEntity.ok(userService2.findByUsername(principal.getName()).orElseThrow());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("users/signup")
    public ResponseEntity<?> createUser2(@RequestBody User user) throws IOException {
        String error = validateService.validateUser(user);
        if (error != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", error);
            response.put("user", user);
            return ResponseEntity.badRequest().body(response);
        } else {
            user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
            userService2.saveUser(user);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getUserid()).toUri();
            return ResponseEntity.created(location).body(user);
        }
    }
    @PutMapping("users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody User newuser, HttpServletRequest request) throws IOException {
        Principal principal = request.getUserPrincipal();
        String error = validateService.validateUser(newuser);
        if (error == null && principal!= null) {
            String name = principal.getName();
            Optional<User> userOptional = userService2.findByUsername(name);
            if (userOptional.isPresent()) {
                User userLogged = userOptional.get();
                if (userService2.isUser(userLogged.getUserid(), id) || request.isUserInRole("ADMIN")) {
                    newuser.setUserid(id);
                    userService2.updateUser(newuser);

                    return ResponseEntity.ok(newuser);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else if (error!= null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", error);
            response.put("user", newuser);
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id, HttpServletRequest request) {
        Optional<User> user = userService2.findById(id);
        Principal principal = request.getUserPrincipal();
        if (principal!= null) {
            String name = principal.getName();
            Optional<User> userOptional = userService2.findByUsername(name);
            if (userOptional.isPresent()) {
                User userLogged = userOptional.get();
                if (userService2.isUser(userLogged.getUserid(), id) || request.isUserInRole("ADMIN")) {
                    userService2.delete(id);
                    return ResponseEntity.ok(user);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if(principal !=null)
        {
            String name = principal.getName();
            Optional<User> userOptionalLogged = userService2.findByUsername(name);
            User userLogged = userOptionalLogged.get();
            Optional<User> user = userService2.findById(id);
            if((userOptionalLogged.isPresent()) && (userService2.isUser(userLogged.getUserid(), id)|| request.isUserInRole("ADMIN"))){
                return ResponseEntity.ok(user.get());
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
    }


}



