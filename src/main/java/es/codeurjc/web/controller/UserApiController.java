package es.codeurjc.web.controller;
import com.fasterxml.jackson.annotation.JsonView;
import es.codeurjc.web.Model.User;

import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Service.GroupClassService;

import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;
@RestController
@RequestMapping("api/")
public class UserApiController {
    @Autowired
    private GroupClassService groupClassService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateService validateService;

    //@GetMapping("/me")
    //@GetMapping("/me/edit")
    //@PostMapping("/me/edit")
    //@GetMapping("/me/delete")
    //@PostMapping("/me/delete")
}

