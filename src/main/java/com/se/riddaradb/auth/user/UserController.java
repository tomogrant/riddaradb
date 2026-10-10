package com.se.riddaradb.auth.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/admin/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("getusers")
    public Set<UserDto> getUsers(){
        return userService.getUsers();
    }

    @PostMapping("postuser")
    public UserDto createUser(@RequestBody @Valid UserDto userDto){
        return userService.createUser(userDto);
    }

    @PutMapping("putuser")
    public UserDto updateUser(@RequestBody @Valid UserDto userDto){
        return userService.updateUser(userDto);
    }

    @DeleteMapping("deleteuser/{username}")
    public void deleteUser(@PathVariable String username){
        userService.deleteUser(username);
    }
}
