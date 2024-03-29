package com.group.foodorderapplicationback.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.group.foodorderapplicationback.model.Food;
import com.group.foodorderapplicationback.model.Orders;
import com.group.foodorderapplicationback.model.Role;
import com.group.foodorderapplicationback.model.User;
import com.group.foodorderapplicationback.repository.FoodRepository;
import com.group.foodorderapplicationback.repository.OrdersRepository;
import com.group.foodorderapplicationback.repository.RoleRepository;
import com.group.foodorderapplicationback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FoodRepository foodRepository;
    private final OrdersRepository ordersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER");

        user.getRoleList().add(role);

        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Food addFoodToFavorites(Long foodId, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //debug secret - same as authentication
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        log.info("Adding food to favorites for authorized user: {" + decodedJWT.getSubject() + "}");

        Food food = foodRepository.findById(foodId).get();
        userRepository.findByUsername(decodedJWT.getSubject()).getFavouriteFood().add(food);

        return food;
    }

    @Override
    public void removeFoodFromFavourites(Long id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //debug secret - same as authentication
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        log.info("Removing food from favorites for authorized user: {" + decodedJWT.getSubject() + "}");

        Food food = foodRepository.findById(id).get();
        userRepository.findByUsername(decodedJWT.getSubject()).getFavouriteFood().remove(food);
    }

    @Override
    public List<Food> findFavouriteFoodForUser(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //debug secret - same as authentication
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        log.info("Getting favourite food list for authorized user: {" + decodedJWT.getSubject() + "}");

        return userRepository.findByUsername(decodedJWT.getSubject()).getFavouriteFood();
    }

    @Override
    public List<Orders> findAllOrdersForUser(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //debug secret - same as authentication
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        log.info("Getting order history for authorized user: {" + decodedJWT.getSubject() + "}");

        return ordersRepository.findAllByUserUsernameOrderByDateTimeDesc(decodedJWT.getSubject());
    }

    @Override
    public User getUserInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //debug secret - same as authentication
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        log.info("Getting user info for authorized user: {" + decodedJWT.getSubject() + "}");

        return userRepository.findByUsername(decodedJWT.getSubject());
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> searchByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameContainingOrEmailContaining(username, email);
    }

    @Override
    public User update(HttpServletRequest request, User user) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //debug secret - same as authentication
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        log.info("Updating user: {" + decodedJWT.getSubject() + "}");

        User dbUser = userRepository.findByUsername(decodedJWT.getSubject());

        dbUser.setFirstName(user.getFirstName());
        dbUser.setLastName(user.getLastName());
        dbUser.setEmail(user.getEmail());

        if(user.getPassword() != null) {
            dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        dbUser.setPhoneNumber(user.getPhoneNumber());

        return userRepository.save(dbUser);
    }
}
