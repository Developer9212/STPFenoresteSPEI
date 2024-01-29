package fenoreste.spei.util;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory",transactionManagerRef = "transactionManager",basePackages = "fenoreste.spei.dao")
@Service
public class SpringConfiguration {	
	@Autowired
	private Environment env;	
	
	@Autowired
	FicheroConexion fichero;
	
	@Primary
    @Bean(name="conexion")    
    public DataSource conexion() {		
    	DriverManagerDataSource datasource = new DriverManagerDataSource(); 
       try {
    		datasource.setUrl("jdbc:postgresql://"+fichero.getHost()+":5432/"+fichero.getDatabase().trim());
        	datasource.setUsername(env.getProperty("spring.datasource.username"));
        	datasource.setPassword(env.getProperty("spring.datasource.password"));
        	datasource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		} catch (Exception e) {
		  System.out.println("Error al crear el datasource:"+e.getMessage());
		}    	
    	return datasource;
    }	   
	    
	@Primary
	@Bean(name ="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entity(){
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(conexion());
		em.setPackagesToScan("fenoreste.spei.entity");		
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);		
		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect",env.getProperty("spring.jpa.database-platform"));		
		em.setJpaPropertyMap(properties);			
		return em;		
	}
	
	@Primary
	@Bean(name="transactionManager")
	public PlatformTransactionManager manager() {
		JpaTransactionManager managerJpa = new JpaTransactionManager();
		managerJpa.setEntityManagerFactory(entity().getObject());		
		return managerJpa;
	}    
	
}
