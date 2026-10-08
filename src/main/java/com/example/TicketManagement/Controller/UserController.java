package com.example.TicketManagement.Controller;

import com.example.TicketManagement.Entity.User;
import com.example.TicketManagement.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
//@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/newUser")
//    public User saveUser(@RequestBody User user){
//        return userService.saveUser(user);
//    }
//
//    @GetMapping("/getUsers")
//    public List<User> getUsers(){
//        return userService.getUsers();
//    }
//
//    @GetMapping("/getUsers/{id}")
//    public Optional<User> getUserById(@PathVariable Long id){
//        return  userService.getUserById(id);
//    }
//
//    @GetMapping("/register")
//    public String registerPage() {
//        return "register";
//    }
//
//    @PutMapping("/updateUser")
//    public User updateUser(@RequestBody User user){
//        return userService.updateUser(user);
//    }
//
//    @DeleteMapping("/deleteUser/{id}")
//    public void deleteUser(@PathVariable Long id){
//        userService.deleteUser(id);
//    }

}
