package fenoreste.spei.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import fenoreste.spei.entity.Tabla;
import fenoreste.spei.entity.TablaPK;
import fenoreste.spei.service.ITablaService;
@Configuration
@EnableWebSecurity
public class WebSecurityConfig 	extends WebSecurityConfigurerAdapter {
		
	    @Autowired
	    private ITablaService tablaService;

	    @Autowired
	    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
	    	TablaPK tablaPK = new TablaPK("spei_entrada","usuario_ws");
	    	Tabla tabla = tablaService.buscarPorId(tablaPK);
	        auth.inMemoryAuthentication().withUser(tabla.getDato1()).password(tabla.getDato2()).roles("USER");
	    }

	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        http.csrf().disable().authorizeRequests()
	        .antMatchers("/api**").hasRole("USER")
	        .and().httpBasic();
	    }

	    @SuppressWarnings("deprecation")
	    @Bean
	    public static NoOpPasswordEncoder passwordEncoder() {
	        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	    }
}
