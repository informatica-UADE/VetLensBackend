package com.api.vetlens.controller;

import com.api.vetlens.dto.*;
import com.api.vetlens.dto.dog.DogRequestDTO;
import com.api.vetlens.dto.dog.DogResponseDTO;
import com.api.vetlens.dto.user.UserRequestDTO;
import com.api.vetlens.dto.user.UserResponseDTO;
import com.api.vetlens.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody @Valid UserRequestDTO request) {
        return ResponseEntity.ok(userService.update(request));
    }

    @PutMapping("/password/{username}/{oldPassword}/{newPassword}")
    public ResponseEntity<MessageDTO> changePassword(@PathVariable String username, @PathVariable String oldPassword, @PathVariable String newPassword) {
        return ResponseEntity.ok(userService.changePassword(username, oldPassword, newPassword));
    }

    @PutMapping("/password/restore/{username}")
    public ResponseEntity<MessageDTO> forgotPassword(@PathVariable String username) {
        return ResponseEntity.ok(userService.forgotPassword(username));
    }

    @PostMapping("/dog/add")
    public ResponseEntity<DogResponseDTO> addDog(@RequestBody @Valid DogRequestDTO request) {
        return ResponseEntity.ok(userService.addDog(request));
    }

    @GetMapping("/dogs/{username}")
    public ResponseEntity<List<DogResponseDTO>> getDogs(@PathVariable String username) {
        return ResponseEntity.ok(userService.getAllDogs(username));
    }

    @PutMapping("/dog/photo/{idDog}")
    public ResponseEntity<MessageDTO> addDogPhoto(@RequestPart(name = "image") MultipartFile image, @PathVariable Integer idDog) {
        return ResponseEntity.ok(userService.addDogPhoto(idDog, image));
    }

    @PutMapping("/dog/photo/remove/{idDog}")
    public ResponseEntity<MessageDTO> removeDogPhoto(@PathVariable Integer idDog) {
        return ResponseEntity.ok(userService.removeDogPhoto(idDog));
    }

    @DeleteMapping("/dog/remove/{idDog}")
    public ResponseEntity<MessageDTO> removeDog(@PathVariable Integer idDog) {
        return ResponseEntity.ok(userService.removeDog(idDog));
    }
}