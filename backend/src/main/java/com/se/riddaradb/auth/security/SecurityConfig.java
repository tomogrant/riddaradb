package com.se.riddaradb.auth.security;

import com.se.riddaradb.auth.user.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.site-locked:true}")
    private boolean locked;

    @Bean
    public AuthenticationManager authenticationManager
            (AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        //Allows the frontend to request cookies etc
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){

        final String adminPattern = "/admin/**";
        final String sagaPattern = "/sagas/**";
        final String sagaVersionPattern = "/sagaversions/**";
        final String bibPattern = "/bibentries/**";
        final String motifPattern = "/motifs/**";
        final String msPattern = "/ms/**";
        final String msRepoPattern = "/msrepository/**";
        final String characterPattern = "/characters/**";
        final String locationPattern = "/locations/**";

        httpSecurity
                //Enable CORS settings as defined in the CorsConfigurationSource bean
                .cors(Customizer.withDefaults())

                //Generate CSRF cookie for Single Page Application
                .csrf(CsrfConfigurer::spa);

//                .csrf(AbstractHttpConfigurer::disable)
                httpSecurity.authorizeHttpRequests(auth ->
                        {
                            auth.requestMatchers("/error").permitAll();

                            auth.requestMatchers("/config/getconfig").permitAll();

                            //Authentication
                            //--------------
                            auth.requestMatchers("/auth/me", "/auth/logout").authenticated()
                                    .requestMatchers("/auth/csrf").permitAll()
                                    .requestMatchers("/auth/login").permitAll();

                            //Admin actions
                            //-------------
                            auth.requestMatchers(adminPattern).hasRole(Role.ADMINISTRATOR.name());

                            //Sagas
                            //-----
                            if (locked){
                                auth.requestMatchers(HttpMethod.GET, sagaPattern).authenticated();
                            }
                            else{
                                auth.requestMatchers(HttpMethod.GET, sagaPattern).permitAll();
                            }

                            auth.requestMatchers(HttpMethod.DELETE, sagaPattern).hasRole(Role.ADMINISTRATOR.name())
                                    .requestMatchers(sagaPattern).authenticated();

                            //Saga versions
                            //-------------
                            if(locked){
                                auth.requestMatchers(HttpMethod.GET, sagaVersionPattern).authenticated();
                            }
                            else{
                                auth.requestMatchers(HttpMethod.GET, sagaVersionPattern).permitAll();
                            }
                            auth.requestMatchers(HttpMethod.DELETE, sagaVersionPattern).hasRole(Role.ADMINISTRATOR.name())
                                    .requestMatchers(sagaVersionPattern).authenticated();

                            //Bibliography entries
                            //--------------------
                            if (locked){
                                auth.requestMatchers(HttpMethod.GET, bibPattern).authenticated();
                            }
                            else{
                                auth.requestMatchers(HttpMethod.GET, bibPattern).permitAll();
                            }
                            auth.requestMatchers(bibPattern).authenticated();

                            //Motifs
                            //------
                            if (locked){
                                auth.requestMatchers(HttpMethod.GET, motifPattern).authenticated();
                            }
                            else{
                                auth.requestMatchers(HttpMethod.GET, motifPattern).permitAll();
                            }
                            auth.requestMatchers(motifPattern).authenticated();

                            //Manuscripts
                            //-----------
                            if(locked){
                                auth.requestMatchers(HttpMethod.GET, msPattern).authenticated();
                            }
                            else{
                                auth.requestMatchers(HttpMethod.GET, msPattern).permitAll();
                            }
                            auth.requestMatchers(msPattern).authenticated();

                            //Manuscript repositories
                            //-----------------------
                            if(locked){
                                auth.requestMatchers(HttpMethod.GET, msRepoPattern).authenticated();
                            }
                            else{
                                auth.requestMatchers(HttpMethod.GET, msRepoPattern).permitAll();
                            }
                            auth.requestMatchers(msRepoPattern).authenticated();

                            //Characters
                            //----------
                            if(locked){
                                auth.requestMatchers(HttpMethod.GET, characterPattern).authenticated();
                            }
                            else{
                                auth.requestMatchers(HttpMethod.GET, characterPattern).permitAll();
                            }
                            auth.requestMatchers(characterPattern).authenticated();

                            //Locations
                            //---------
                            if(locked){
                                auth.requestMatchers(HttpMethod.GET, locationPattern).authenticated();
                            }
                            else{
                                auth.requestMatchers(HttpMethod.GET, locationPattern).permitAll();
                            }
                            auth.requestMatchers(locationPattern).authenticated();
                        })

                //Maximum number of sessions
                .sessionManagement(session ->
                        session.maximumSessions(1))

                //Form login
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/login")
                        .successHandler(((request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT)))
                        .failureHandler(((request, response, exception) ->
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)))
                )

                //Log out endpoint
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication((true))
                        .logoutSuccessHandler(
                                (request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT))
                )

                // Spring Security normally reroutes unauthenticated requests to Spring's own login page.
                // Instead, we want to just send a HTTP response
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                );

        return httpSecurity.build();
    }
}
