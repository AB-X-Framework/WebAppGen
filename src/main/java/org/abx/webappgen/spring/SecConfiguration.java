package org.abx.webappgen.spring;

import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.dao.MapEntryRepository;
import org.abx.webappgen.persistence.dao.MapResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.Transactional;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Autowired
    private PageModel pageModel;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).
                authorizeHttpRequests(authz -> {
                    authz
                            .requestMatchers("web/**")
                            .permitAll()
                            .requestMatchers("session/**")
                            .permitAll()
                            .requestMatchers("resources/**")
                            .permitAll()
                            .requestMatchers("page/**")
                            .permitAll()
                            .requestMatchers("pages/**")
                            .permitAll()
                            .requestMatchers("components/**")
                            .permitAll()
                            .requestMatchers("process/**")
                            .permitAll()
                            .requestMatchers("error").permitAll()
                            .anyRequest()
                            .authenticated();
                }).exceptionHandling(security -> {
                    security.authenticationEntryPoint(
                            (request, response, authException) -> {
                                String home = pageModel.getHome();
                                response.sendRedirect("/page/"+home);
                            }
                    );

                });
        return http.build();
    }

}
