package security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
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
public class SecurityConfigurations {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
        ROLE_ADMIN > ROLE_COORDENACAO
        ROLE_COORDENACAO > ROLE_DCE
        ROLE_COORDENACAO > ROLE_DA
        ROLE_DCE > ROLE_USER
        ROLE_DA > ROLE_USER
        ROLE_USER > ROLE_ALUNO
        ROLE_USER > ROLE_PROFESSOR
    """);
        return hierarchy;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpsecurity) throws Exception {
        return httpsecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").hasRole("ADMIN")
                        // Configurações do Mural
                        .requestMatchers(HttpMethod.POST, "/mural").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/mural/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/mural/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/mural/**").hasRole("USER")
                        // Configurações do FAQ
                        .requestMatchers(HttpMethod.POST, "/faq").hasRole("COORDENACAO")
                        .requestMatchers(HttpMethod.PUT, "/faq/**").hasRole("COORDENACAO")
                        .requestMatchers(HttpMethod.DELETE, "/faq/**").hasRole("COORDENACAO")
                        .requestMatchers(HttpMethod.GET, "/faq/**").hasRole("ALUNO")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean

    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();

    }


    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();

    }

}
