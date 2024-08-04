package com.wproekt.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@EnableWebSecurity
@EnableJpaAuditing
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Configuration

public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final DefaultOAuth2UserService oAuthUserService;
    private final CustomUsernamePasswordAuthenticationProvider customUsernamePasswordAuthenticationProvider;
    private final AuthenticationSuccessHandler successHandler;

    public SecurityConfig(UserDetailsService userDetailsService, DefaultOAuth2UserService oAuthUserService, CustomUsernamePasswordAuthenticationProvider customUsernamePasswordAuthenticationProvider, AuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.oAuthUserService = oAuthUserService;
        this.customUsernamePasswordAuthenticationProvider = customUsernamePasswordAuthenticationProvider;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                antMatcher("/login**"),
                                antMatcher("/register"),
                                antMatcher("/verify/**/**"),
                                antMatcher("/img/**"),
                                antMatcher("/scripts/**"),
                                antMatcher("/css/**"),
                                antMatcher("/resetPassword"),
                                antMatcher("/reset/**/**"),
                                antMatcher("/resetForm"),
                                antMatcher("oauth2**")).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureHandler(customAuthenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuthUserService))
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .successHandler(successHandler)
                        .failureHandler(customAuthenticationFailureHandler())
                        .permitAll()
                );

        return http.build();
    }


    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            if (exception instanceof BadCredentialsException) {
                String errorMessage = exception.getMessage();
                // Customize the failure URL based on the exception message
                // For example:
                if (errorMessage.contains("Invalid Credentials")) {
                    response.sendRedirect("/login?err=invalid-credentials");
                } else if (errorMessage.contains("Not Verified")) {
                    response.sendRedirect("/login?err=not-verified");
                } else {
                    response.sendRedirect("/login?err=unknown");
                }
            }
        };
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customUsernamePasswordAuthenticationProvider);

        return authenticationManagerBuilder.build();

    }


}
