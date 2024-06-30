package es.codeurjc.web.security;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Autowired
    public RepositoryUserDetailsService userDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**");
                //.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PRIVATE ENDPOINTS
                        .requestMatchers(HttpMethod.POST,"/api/books/").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/books/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/books/**").hasRole("ADMIN")
                        // PUBLIC ENDPOINTS
                        .anyRequest().permitAll()
                );

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
      //  http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(authorize -> authorize

                        // PRIVATE PAGES
                        .requestMatchers("/GroupClasses/*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/JoinClass-*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/JoinClassConfirmation-*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/LeaveClass-*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/LeaveClassConfirmation-*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/blog/new").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/blog/changePost/*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/blog/deletePost/*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/me").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/users").hasRole("ADMIN")
                        .requestMatchers("/users/*").hasRole("ADMIN")
                        .requestMatchers("/users/*/delete").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/users/*/edit").hasAnyRole("USER","ADMIN")
                        // PUBLIC PAGES
                        .requestMatchers("/blog").permitAll()
                        .requestMatchers("/blog/*").permitAll()
                        .requestMatchers("/blog/*/image").permitAll()
                        .requestMatchers("/css/*").permitAll()
                        .requestMatchers("/blog/css/*").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/blog/error").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/signup").permitAll()
                        .requestMatchers("/loginerror").permitAll()
                        .requestMatchers("/").permitAll()
                        //
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/h2-console/login.do?jsessionid=*").permitAll()
                        .requestMatchers("/h2-console/header.jsp*").permitAll()
                        .requestMatchers("/h2-console/tables.do*").permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/loginerror")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

}
