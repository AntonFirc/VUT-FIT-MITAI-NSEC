package com.vut.fit.gja2020.app;

import com.sun.faces.config.ConfigureListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.faces.webapp.FacesServlet;
import javax.persistence.spi.PersistenceProvider;
import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Bean
	JpaVendorAdapter JpaVendorAdapter() {
		return new AbstractJpaVendorAdapter() {
			@Override
			public PersistenceProvider getPersistenceProvider() {
				return new com.objectdb.jpa.Provider();
			}

			@Override
			public Map<String,?> getJpaPropertyMap() {
				return Collections.singletonMap(
						"javax.persistence.jdbc.url", "eduServerManager.odb");
			}
		};
	}

	@Bean
	public ServletRegistrationBean facesServletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean<>(new FacesServlet(), "*.xhtml");
		registration.setLoadOnStartup(1);
		return registration;
	}

	@Bean
	public ServletContextInitializer servletContextInitializer() {
		return servletContext -> {
			servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());
		};
	}

	@Bean
	public ServletListenerRegistrationBean<ConfigureListener> jsfConfigureListener() {
		return new ServletListenerRegistrationBean<>(new ConfigureListener());
	}

}
