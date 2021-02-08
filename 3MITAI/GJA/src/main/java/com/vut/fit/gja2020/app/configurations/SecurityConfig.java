package com.vut.fit.gja2020.app.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder().encode("admin"))
                .credentialsExpired(false)
                .accountExpired(false)
                .accountLocked(false)
                .roles("ADMIN");

    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/dashboard.xhtml").hasRole("ADMIN");
        // require all requests to be authenticated except for the resources
        http.authorizeRequests().antMatchers("/resources/**")
                .permitAll().anyRequest().authenticated();
        // login
        http.formLogin().loginPage("/login.xhtml").permitAll()
                .failureUrl("/login.xhtml?error=true").defaultSuccessUrl("/");
        // logout
        http.logout().logoutSuccessUrl("/login.xhtml");
        // not needed as JSF 2.2 is implicitly protected against CSRF
        http.cors().and().csrf().disable();
    }
}
