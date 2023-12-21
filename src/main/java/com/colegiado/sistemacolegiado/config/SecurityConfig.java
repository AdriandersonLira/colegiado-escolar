package com.colegiado.sistemacolegiado.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private DataSource dataSource;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/imagens/**").permitAll()
                    .requestMatchers("/alunos/**").hasAnyRole("ADMIN", "ALUNOS")
                    .requestMatchers("/professores/**").hasAnyRole("PROFESSOR", "ADMIN", "COORDENADOR")
                .anyRequest().authenticated())
            .formLogin((form) -> form
                .loginPage("/auth/login")
                .defaultSuccessUrl("/home", true)
                .permitAll())
            .logout((logout) -> logout.logoutUrl("/auth/logout"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Alguns usuários básicos, criados quando da 1a. execução da aplicaçao
        UserDetails admin = User.withUsername("admin").password(passwordEncoder().encode("admin")).roles("ADMIN", "COORDENADOR", "PROFESSOR", "ALUNO").build();

        UserDetails candido = User.withUsername("candido").password(passwordEncoder().encode("candido@123")).roles("COORDENADOR", "PROFESSOR").build();

        UserDetails fred = User.withUsername("fred").password(passwordEncoder().encode("fred@123")).roles("PROFESSOR").build();
        UserDetails ada = User.withUsername("ada").password(passwordEncoder().encode("firstcoder")).roles("PROFESSOR").build();

        UserDetails lira = User.withUsername("lira").password(passwordEncoder().encode("lira@123")).roles("ALUNO").build();
        UserDetails samuel = User.withUsername("samuel").password(passwordEncoder().encode("samuel@123")).roles("ALUNO").build();
        UserDetails gabriel = User.withUsername("gabriel").password(passwordEncoder().encode("gabriel@123")).roles("ALUNO").build();
        UserDetails sagan = User.withUsername("sagan").password(passwordEncoder().encode("cosmos")).roles("ALUNO").build();
        UserDetails turing = User.withUsername("turing").password(passwordEncoder().encode("enignma")).roles("ALUNO").build();
        // Evita duplicação dos usuários no banco
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        if (!users.userExists(admin.getUsername())) {
            users.createUser(admin);

            users.createUser(candido);

            users.createUser(fred);
            users.createUser(ada);

            users.createUser(lira);
            users.createUser(samuel);
            users.createUser(gabriel);
            users.createUser(sagan);
            users.createUser(turing);
        }
        return users;
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
