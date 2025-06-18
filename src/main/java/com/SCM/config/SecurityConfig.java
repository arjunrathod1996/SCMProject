package com.SCM.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.SCM.dtoForJWT.JWTAuthFilter;
import com.SCM.dtoForJWT.JwtAuthenticationEntryPoint;
import com.SCM.helpers.CustomAccessDeniedHandler;
import com.SCM.service.SecuritycCustomerUserDetailService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Autowired
    private SecuritycCustomerUserDetailService securitycCustomerUserDetailService;

    @Autowired
    private OAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

   /* @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }*/ 
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            logger.error("Access denied exception: {}", accessDeniedException.getMessage());
            response.sendRedirect("/access-denied");
        };
    }

    @Bean
    public InMemoryTokenRepositoryImpl tokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(securitycCustomerUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.authenticationProvider(authenticationProvider());
        logger.info("AuthenticationManagerBuilder configured.");
        return auth.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors().and().csrf().disable()
            .authorizeRequests()
                .requestMatchers("/email/forgot", "/send-otp", "/oauth2/authorization/github/", "/oauth2/**", "/auth/", "/my_app/login", "/auth/**", "/auth/generate-otp", "/verify-otp", "/token", "/home", "/", "/about", "/login", "/register", "/css/**", "/images/**", "/js/**", "/error").permitAll()
                .anyRequest().authenticated()
            .and()
            .oauth2Login()
                .successHandler(authenticationSuccessHandler)
            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login") // Redirect to login page after logout
                .invalidateHttpSession(true)  
                .deleteCookies("JSESSIONID", "token")
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler())
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)  
            .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler((request, response, accessDeniedException) -> response.sendRedirect("/error"));

        // Set cache control headers to prevent browser caching of secured pages
        httpSecurity.headers().cacheControl().disable();
        logger.info("HttpSecurity configured.");
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void configure(AuthenticationManagerBuilder builder) {
        builder.eraseCredentials(false);
    }

    @Bean
    public OAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuthenticationSuccessHandler();
    } 
    
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new SimpleUrlLogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.Authentication authentication) throws IOException, java.io.IOException, ServletException {
                super.onLogoutSuccess(request, response, authentication);
                logger.info("User logged out successfully.");
            }
        };
    }
}





