package es.codeurjc.web.security;

import es.codeurjc.web.security.jwt.JwtRequestFilter;
import es.codeurjc.web.security.jwt.UnauthorizedHandlerJwt;
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
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PRIVATE ENDPOINTS
                        .requestMatchers(HttpMethod.POST,"/api/posts/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST,"/api/posts/*/image").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/posts/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/posts/*/image").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT,"/api/posts/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/api/posts/*/image").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/posts/**").permitAll()

                        .requestMatchers(HttpMethod.POST,"/api/groupClasses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/groupClasses/*/image").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/groupClasses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/groupClasses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/groupClasses/type/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/groupClasses/**").permitAll()

                        .requestMatchers(HttpMethod.POST,"/api/auth/login").anonymous()
                        .requestMatchers(HttpMethod.POST,"/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/users/signup").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/users/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT,"/api/users/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/auth/refresh").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/users/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.POST,"/api/users/**").authenticated()
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
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

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
                        .requestMatchers("/GroupClasses/find").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/JoinClass-*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/JoinClassConfirmation-*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/LeaveClass-*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/GroupClasses/*/LeaveClassConfirmation-*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/blog/*").hasAnyRole("USER","ADMIN")
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

        //http.csrf(csrf -> csrf.disable());
        return http.build();
    }

}
