package com.tienda;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
 
@Configuration
public class SecurityConfig {
    //A continuacion van las rutas (urls) que los usuarios van a solicitar al sistema techshop
    //La próxima semana esto se borra...
    //A continuación van las rutas que TODOS los usuarios pueden acceder sin problema
    public static final String[] PUBLIC_URLS = {"/","/index","/consultas/**",
    "/fav/**","/webjars/**","/js/**","/login","/acceso_denegado"};
    //A continuación van las rutas que un USUARIO puede acceder sin problema
    public static final String[] USUARIO_URLS = {"/facturas/carrito"};
    //A continuación van las rutas que un VENDEDOR puede acceder sin problema
    public static final String[] VENDEDOR_URLS = {"/categoria/listado",
    "/producto/listado"};
    //A continuación van las rutas que un ADMIN puede acceder sin problema
    public static final String[] ADMIN_URLS = {"/categoria/**",
    "/producto/**","/usuario/**","/admin/**"};
    //El método siguiente se utiliza para gestionar todo lo referente a autorizacion y procesos de login o logout
    //La próxima semana se modifica algo...
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //Se establecen cuales rutas se acceden desde qué roles...
        http.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(USUARIO_URLS).hasRole("USUARIO")
                .requestMatchers(VENDEDOR_URLS).hasAnyRole("VENDEDOR","ADMIN")
                .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                .anyRequest().authenticated()
        );
        //Se establece el proceso para hacer "login"
        http.formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
        );
        //Se establece el proceso para hacer "logout"
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );
        //se establece el recurso para cuando hay alguna "excepción"
        http.exceptionHandling(ex -> ex.accessDeniedPage("/acceso_denegado"));
        //Se establece qué hacer con sesiones concurrentes
        http.sessionManagement(ses -> ses
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );        
        return http.build();                
    }
    //Se define el método para encriptar la clave
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //Se generan usuario en memoria... temporalmente.. la próxima semana este método se borra...
    @Bean
    public UserDetailsService user(PasswordEncoder passwordEncoder) {
        UserDetails user1 = User.builder().username("juan")
                .password(passwordEncoder.encode("123"))
                .roles("ADMIN")
                .build();
        UserDetails user2 = User.builder().username("rebeca")
                .password(passwordEncoder.encode("456"))
                .roles("VENDEDOR")
                .build();
        UserDetails user3 = User.builder().username("pedro")
                .password(passwordEncoder.encode("789"))
                .roles("USUARIO")
                .build();
        return new InMemoryUserDetailsManager(user1, user2, user3);        
    }
}  
