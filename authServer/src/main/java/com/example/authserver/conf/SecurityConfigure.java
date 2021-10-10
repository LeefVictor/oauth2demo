package com.example.authserver.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;

//security和 oauth2是两个独立的东西， 前者是接口校验， 后者是授权服务， 不一样的

@EnableWebSecurity
@Configuration
public class SecurityConfigure extends WebSecurityConfigurerAdapter {
    private PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(encoder)
                .withUser("user1")
                .password(encoder.encode("123456"))
                .roles("READ")
                .and()
                .withUser("user2").password(encoder.encode("123456"))
                .authorities("READ")
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/oauth/**","/login/**", "/logout").permitAll()
                .anyRequest().authenticated()   // 其他地址的访问均需验证
                .and()
                .formLogin().permitAll().and().httpBasic();
    }
}
