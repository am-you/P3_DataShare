package fr.datashare.auth;

import fr.datashare.auth.dto.LoginRequest;
import fr.datashare.auth.dto.LoginResponse;
import fr.datashare.auth.dto.RegisterRequest;
import fr.datashare.auth.dto.UserResponse;
import fr.datashare.common.ConflictException;
import fr.datashare.common.UnauthorizedException;
import fr.datashare.user.User;
import fr.datashare.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

// Am.Yo : règles métier inscription et connexion
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email déjà utilisé");
        }
        User user = new User(
                UUID.randomUUID(),
                email,
                passwordEncoder.encode(request.password()),
                Instant.now());
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(token, jwtService.getExpirationSeconds());
    }
}
