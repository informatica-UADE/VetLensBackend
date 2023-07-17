package com.api.vetlens.service;

import com.api.vetlens.dto.MessageDTO;
import com.api.vetlens.dto.UserRequestDTO;
import com.api.vetlens.dto.UserResponseDTO;
import com.api.vetlens.entity.Role;
import com.api.vetlens.entity.User;
import com.api.vetlens.exceptions.EmailNotSendException;
import com.api.vetlens.exceptions.UserNotFoundException;
import com.api.vetlens.repository.UserRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker;
    private final EmailService emailService;
    private ModelMapper mapper = new ModelMapper();

    public UserResponseDTO update(UserRequestDTO request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(request.getEmail());
            if (request.getRole().equals("VET")) {
                user.setRole(Role.VET);
            }
            user.setFirstName(request.getFirstName());
            user.setLastname(request.getLastname());
            user.setLicenceNumber(request.getLicenceNumber());
            User savedUser = userRepository.save(user);
            return mapper.map(savedUser, UserResponseDTO.class);
        }
        throw new UserNotFoundException("Usuario " + request.getUsername() + " no encontrado");
    }

    public MessageDTO changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario " + username + " no encontrado");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserNotFoundException("Contraseña incorrecta");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new MessageDTO("Contraseña actualizada exitosamente");
    }

    public MessageDTO forgotPassword(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario " + username + " no encontrado");
        }
        User user = userOptional.get();
        String pass = faker.internet().password();
        user.setPassword(passwordEncoder.encode(pass));

        this.sendEmailChangedPass(user.getEmail(), pass);
        userRepository.save(user);

        return new MessageDTO("Se ha enviado un mail con una nueva contraseña");
    }

    public void sendEmailNewAccount(String email, String password, String user) {
        emailService.sendEmail(email, "CUENTA CREADA CON EXITO", "" +
                "<html>" +
                "<body>" +
                "   <div style='font-family:garamond'>" +
                "   <h1>¡Bienvenido a VetLens!</h1>" +
                "   <h2>Su cuenta ha sido creada con exito</h2>" +
                "<h3>" +
                "Le informamos que su cuenta fue creada exitosamente, sus credenciales son:" +
                "</h3>" +
                "<ul>" +
                "<li>" +
                "<h3><u>Usuario</u>: " + user + "</h3>" +
                "</li>" +
                "<li>" +
                "<h3><u>Contraseña</u>: " + password + "</h3>" +
                "</li> " +
                "</ul>" +
                "</p>" +
                "   <br> " +
                "<p>" +
                "Ya puede loguearse y disfrutar de la aplicación. Gracias por unirte a nuestra comunidad de usuarios!" +
                "</p>" +
                "<p><b>-VetLens Team</b></p>" +
                "   </div>" +
                "</body>" +
                "</html>");
    }

    private void sendEmailChangedPass(String email, String password) {
        emailService.sendEmail(email, "CONTRASEÑA MODIFICADA CON EXITO", "" +
                " <html>" +

                "<body>" +
                "<div style='font-family:garamond;'>" +
                "<h1>VetLens</h1>" +
                "<h2>Su contraseña ha sido modificada</h2>" +
                "<h3>" +
                "Le informamos que la contraseña de su cuenta fue actualizada exitosamente, su nueva contraseña es:" +
                "</h3>" +
                "<ul>" +
                "<li>" +
                "<h3><u>Contraseña</u>:" + password + "</h3>" +
                "</li>" +
                "</ul>" +
                "<p>" +
                "Ya puede loguearse y disfrutar de la aplicación nuevamente." +
                "</p>" +
                "<p><b>-VetLens Team</b></p>" +
                "</div>" +

                "</body>" +

                "</html>");
    }
}
