package ra.dev.model.serviceImp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.dev.jwt.JwtTokenProvider;
import ra.dev.model.entity.User;
import ra.dev.model.repository.UserRepository;
import ra.dev.model.service.UserService;
import ra.dev.payload.request.LoginRequest;
import ra.dev.payload.response.JwtResponse;
import ra.dev.security.CustomUserDetails;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private PasswordEncoder encoder;


    @Value("${ra.jwt.secret}")
    private String JWT_SECRET;
    @Value(("${ra.jwt.expiration}"))
    private int JWT_EXPIRATION;
    //Tao jwt tu thong tin cua User

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email) ;
    }

    @Override
    public User findByID(int userID) {
        return userRepository.findById(userID).get();
    }

    @Override
    public User saveOrUpdate(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(int userID) {

    }

    @Override
    public List<User> searchByName(String userName) {
        return null;
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication auth = new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails customUserDetail = (CustomUserDetails) authentication.getPrincipal();
        User users = userRepository.findByEmail(customUserDetail.getEmail());
        if (!customUserDetail.isUserStatus()) {
            return null;
        } else {

        String jwt = generateToken(customUserDetail);
            List<String> listRoles = customUserDetail.getAuthorities().stream()
                    .map(item -> item.getAuthority()).collect(Collectors.toList());
            return new JwtResponse(jwt, users.getUserID(), customUserDetail.getUsername(), customUserDetail.getEmail(),
                    customUserDetail.getPhone(), customUserDetail.getFullName(), customUserDetail.getAddress(), listRoles);
        }
    }

    @Override
    public boolean getToken(String email) {
        try {
            String jwt = generateTokenEmail(email);
            sendSimpleMessage(email, "Token", jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User resetPass(String token, String newPass) {
        String userName = getUserNameFromJwt(token);
        User users = findByUserName(userName);
        users.setPassword(encoder.encode(newPass));
        return userRepository.save(users);
    }

    public String generateToken(CustomUserDetails customUserDetails) {
        Date now = new Date();
        Date dateExpired = new Date(now.getTime() + JWT_EXPIRATION);
        //Tao chuoi JWT tu userName
        return Jwts.builder().setSubject(customUserDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(dateExpired)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET).compact();
    }
    public String generateTokenEmail(String email) {
        User users = userRepository.findByEmail(email);
        Date now = new Date();
        Date dateExpired = new Date(now.getTime() + JWT_EXPIRATION);
        //Tao chuoi JWT tu userName
        return Jwts.builder().setSubject(users.getUserName())
                .setIssuedAt(now)
                .setExpiration(dateExpired)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET).compact();
    }
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("daohung.rks@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
    public String getUserNameFromJwt(String token) {
        Claims claims = Jwts.parser().setSigningKey(JWT_SECRET)
                .parseClaimsJws(token).getBody();
        //tra lai thong tin username
        return claims.getSubject();
    }
}
