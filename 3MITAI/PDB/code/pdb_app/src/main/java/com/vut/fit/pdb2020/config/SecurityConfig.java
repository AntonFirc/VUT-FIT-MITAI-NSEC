package com.vut.fit.pdb2020.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@Profile("!https")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**").permitAll();
        http.csrf().disable();
        /*http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        "/index*", "/static/**", "/*.js", "/*.json", "/*.ico")
                .permitAll()
                .antMatchers("/admin/**").hasAnyAuthority("admin", "superadmin")
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/",true)
                .failureUrl("/login")
                .and()
                .logout()
                .logoutUrl("/logout").
                logoutSuccessUrl("/");
        // TODO security*/


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/img/**", "/css/**");
    }
}
