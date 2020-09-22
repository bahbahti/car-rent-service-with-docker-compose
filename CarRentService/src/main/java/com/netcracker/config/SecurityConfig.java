package com.netcracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

import static com.netcracker.constants.Queries.ROLES_QUERY;
import static com.netcracker.constants.Queries.USERS_QUERY;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder bcryptPasswordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private DataSource dataSource;

    @Value(USERS_QUERY)
    private String usersQuery;

    @Value(ROLES_QUERY)
    private String rolesQuery;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.
                jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(usersQuery)
                .authoritiesByUsernameQuery(rolesQuery)
                .passwordEncoder(bcryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/customers/{id}/cars", "/customers/{id}/repairOrders", "/customers/{id}/paidOrders", "/customers/{id}/currentOrders", "/repairOrders/{id}/updateStatus").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/cars", "/customers", "/orders", "/repairOrders", "/customers/{id}", "/cars/{id}", "/orders/{id}", "/repairOrders/{id}").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/customers/{id}").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/cars/**", "/customers/**", "/orders/**", "/repairOrders/**").hasAuthority("ADMIN")
                .and()
                .formLogin().permitAll()
                .defaultSuccessUrl("/swagger-ui.html")
                .and()
                .logout().permitAll();
    }

}
