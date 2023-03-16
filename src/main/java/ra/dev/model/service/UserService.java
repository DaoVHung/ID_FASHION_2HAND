package ra.dev.model.service;


import ra.dev.model.entity.User;
import ra.dev.payload.request.LoginRequest;
import ra.dev.payload.request.SignupRequest;
import ra.dev.payload.response.JwtResponse;

import java.util.List;

public interface UserService {

    boolean existsByEmail(String email);
    boolean saveOrUpdate(SignupRequest signupRequest);
    List<User> findAll();

    User findByEmail(String email);

    User findByID(int userID);

    void delete(int userID);

    List<User> searchByName(String userName);

    User findByUserName(String userName);

    boolean existsByUserName(String userName);

    JwtResponse login(LoginRequest loginRequest);
}
