package com.lpb.mid.ekyc.config;

import com.lpb.mid.log.BaseFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class FilterConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers().permitAll()
                .anyRequest().permitAll();
        http.addFilterAfter(new BaseFilterChain(), UsernamePasswordAuthenticationFilter.class);
        http.cors();
    }
}