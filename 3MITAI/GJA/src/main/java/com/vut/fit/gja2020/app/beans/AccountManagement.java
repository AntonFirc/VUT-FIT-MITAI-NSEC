package com.vut.fit.gja2020.app.beans;

import com.vut.fit.gja2020.app.utils.FirewallUtility;
import com.vut.fit.gja2020.app.utils.UserAccountUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.IOException;

@Component
@ManagedBean
@ApplicationScoped
public class AccountManagement {

    @Autowired
    UserAccountUtility userAccountUtility;

    @Autowired
    FirewallUtility firewallUtility;

    @Autowired
    Dashboard dashboard;

    private String homeDirectoryPrefix = "/home/";

    private String backupDirectoryPrefix = "/user-backups/";

    private String backupGroupDirectoryPrefix = "/group-backups/";

    public void createStudentAccounts() throws IOException, InterruptedException {
        if (userAccountUtility.createAccounts(homeDirectoryPrefix)) {
            FacesMessage message = new FacesMessage("Úspěch", "účty vytvořené.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        else {
            FacesMessage message = new FacesMessage("Chyba");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public String getHomeDirectoryPrefix() {
        return homeDirectoryPrefix;
    }

    public void setHomeDirectoryPrefix(String homeDirectoryPrefix) {
        //if path does not end with / add it (it is needed in further processes)
        if (!homeDirectoryPrefix.substring(homeDirectoryPrefix.length() - 1).equals("/")) {
            homeDirectoryPrefix = homeDirectoryPrefix.concat("/");
        }
        this.homeDirectoryPrefix = homeDirectoryPrefix;
    }

    public String getBackupDirectoryPrefix() {
        return backupDirectoryPrefix;
    }

    public void setBackupDirectoryPrefix(String backupDirectoryPrefix) {
        //if path does not end with / add it (it is needed in further processes)
        if (!backupDirectoryPrefix.substring(backupDirectoryPrefix.length() - 1).equals("/")) {
            backupDirectoryPrefix = backupDirectoryPrefix.concat("/");
        }
        this.backupDirectoryPrefix = backupDirectoryPrefix;
    }

    public String getBackupGroupDirectoryPrefix() {
        return backupGroupDirectoryPrefix;
    }

    public void setBackupGroupDirectoryPrefix(String backupGroupDirectoryPrefix) {
        //if path does not end with / add it (it is needed in further processes)
        if (!backupGroupDirectoryPrefix.substring(backupGroupDirectoryPrefix.length() - 1).equals("/")) {
            backupGroupDirectoryPrefix = backupGroupDirectoryPrefix.concat("/");
        }
        this.backupGroupDirectoryPrefix = backupGroupDirectoryPrefix;
    }

    /**
     * Removes all student accounts from system
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void deleteStudents() throws IOException, InterruptedException {
        dashboard.setStudentList(null);
        if (userAccountUtility.deleteStudentAccounts(backupDirectoryPrefix)) {
            FacesMessage message = new FacesMessage("Úspěch", "studenti smazáni.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        else {
            FacesMessage message = new FacesMessage("Chyba");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    /**
     * Create project groups and directories for projects
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void groupsAdd() throws IOException, InterruptedException {
        dashboard.setGroupList(null);
        if (userAccountUtility.createGroupDirectories()) {
            FacesMessage message = new FacesMessage("Úspěch", "skupiny importované.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        else {
            FacesMessage message = new FacesMessage("Chyba");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    /**
     * Delete project groups
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void groupsDel() throws IOException, InterruptedException {
        dashboard.setGroupList(null);
        if (userAccountUtility.deleteProjectGroup(backupGroupDirectoryPrefix)) {
            FacesMessage message = new FacesMessage("Úspěch", "skupiny smazané.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        else {
            FacesMessage message = new FacesMessage("Chyba");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    /**
     * Remove access to server for all students (iptables, hosts.allow)
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void removeAccess() throws IOException, InterruptedException {
        if (firewallUtility.removeStudentAccess()) {
            FacesMessage message = new FacesMessage("Úspěch", "přístupy odstraněny.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        else {
            FacesMessage message = new FacesMessage("Chyba");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}
