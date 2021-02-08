package com.vut.fit.gja2020.app.beans;

import com.vut.fit.gja2020.app.models.SMTPSettings;
import com.vut.fit.gja2020.app.repository.SMTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@Component
@ManagedBean
@ApplicationScoped
public class SMTP {

    @Autowired
    SMTPRepository smtpRepository;

    private static final String SMTP_SERVICE_NAME = "smtpServer";

    private String from;

    private String host;

    private String username;

    private String password;

    private Integer port;

    private boolean ssl;

    private boolean auth;

    private SMTPSettings settings;

    /**
     * Save settings to database
     */
    public void saveSMTP() {
        SMTPSettings settings = new SMTPSettings();
        settings.setName(SMTP_SERVICE_NAME);
        settings.setFrom(from);
        settings.setHost(host);
        settings.setUsername(username);
        settings.setPassword(password);
        settings.setPort(port);
        settings.setSsl(ssl);
        settings.setAuth(auth);
        smtpRepository.save(settings);

        FacesMessage message = new FacesMessage("Uloženo");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     * Reload SMTP settings from database on change
     */
    private void refreshObject() {
        this.settings = smtpRepository.findByName(SMTP_SERVICE_NAME);
        if (settings == null) {
            this.settings = new SMTPSettings();
        }
        else {
            from = settings.getFrom();
            host = settings.getHost();
            username = settings.getUsername();
            password = settings.getPassword();
            port = settings.getPort();
            ssl = settings.isSsl();
            auth = settings.isAuth();
        }
    }

    public String getFrom() {
        this.refreshObject();
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHost() {
        this.refreshObject();
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        this.refreshObject();
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        this.refreshObject();
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPort() {
        this.refreshObject();
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isSsl() {
        this.refreshObject();
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public boolean isAuth() {
        this.refreshObject();
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public static String getSmtpServiceName() {
        return SMTP_SERVICE_NAME;
    }
}
