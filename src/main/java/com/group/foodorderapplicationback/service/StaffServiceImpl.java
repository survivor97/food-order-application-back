package com.group.foodorderapplicationback.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.group.foodorderapplicationback.model.Manager;
import com.group.foodorderapplicationback.model.Role;
import com.group.foodorderapplicationback.model.Staff;
import com.group.foodorderapplicationback.repository.RoleRepository;
import com.group.foodorderapplicationback.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Staff> findAll() {
        return (List<Staff>) staffRepository.findAll();
    }

    @Override
    public Staff save(Staff staff) {
        staff.setPassword(passwordEncoder.encode(staff.getPassword()));

        Role role = roleRepository.findByName("ROLE_STAFF");

        staff.getRoleList().add(role);

        return staffRepository.save(staff);
    }

    @Override
    public void deleteById(Long id) {
        staffRepository.deleteById(id);
    }

    @Override
    public Staff update(Staff staff) {
        Staff dbStaff = staffRepository.findById(staff.getId()).get();

        dbStaff.setFirstName(staff.getFirstName());
        dbStaff.setLastName(staff.getLastName());
        dbStaff.setEmail(staff.getEmail());

        if(staff.getPassword() != null && staff.getPassword().length() > 0) {
            dbStaff.setPassword(passwordEncoder.encode(staff.getPassword()));
        }

        return staffRepository.save(dbStaff);
    }

    @Override
    public Staff updateAuthenticated(HttpServletRequest request, Staff staff) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //debug secret - same as authentication
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        log.info("Updating staff: {" + decodedJWT.getSubject() + "}");

        Staff dbStaff = staffRepository.findByUsername(decodedJWT.getSubject());

        dbStaff.setFirstName(staff.getFirstName());
        dbStaff.setLastName(staff.getLastName());
        dbStaff.setEmail(staff.getEmail());

        if(staff.getPassword() != null) {
            dbStaff.setPassword(passwordEncoder.encode(staff.getPassword()));
        }

        return staffRepository.save(dbStaff);
    }

    @Override
    public Staff getStaffInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());   //debug secret - same as authentication
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        log.info("Getting staff info for authorized staff: {" + decodedJWT.getSubject() + "}");

        return staffRepository.findByUsername(decodedJWT.getSubject());
    }
}
