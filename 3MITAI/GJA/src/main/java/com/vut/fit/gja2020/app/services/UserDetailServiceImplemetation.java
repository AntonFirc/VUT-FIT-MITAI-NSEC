package com.vut.fit.gja2020.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.repository.StudentRepository;
import com.vut.fit.gja2020.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImplemetation implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<Student> optionalUser = userRepository.findByLogin(userName);
        if(optionalUser.isPresent()) {
            Student users = optionalUser.get();

            return User.builder()
                    .username(users.getLogin())
                    //change here to store encoded password in db
                    .password( passwordEncoder().encode(users.getPassword()))
                    .roles("STUDENT")
                    .build();
        } else {
            throw new UsernameNotFoundException("User Name is not Found");
        }
    }
}