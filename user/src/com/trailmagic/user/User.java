package com.trailmagic.user;


public class User {
    private long m_id;
    private String m_screenName;
    private String m_firstName;
    private String m_lastName;
    private String m_primaryEmail;
    private String m_password;

    public User() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public String getScreenName() {
        return m_screenName;
    }

    public void setScreenName(String sn) {
        m_screenName = sn;
    }

    public String getFirstName() {
        return m_firstName;
    }

    public void setFirstName(String name) {
        m_firstName = name;
    }

    public String getLastName() {
        return m_lastName;
    }

    public void setLastName(String name) {
        m_lastName = name;
    }

    public String getPrimaryEmail() {
        return m_primaryEmail;
    }

    public void setPrimaryEmail(String email) {
        m_primaryEmail = email;
    }

    /**
     * MD5 password digest - stored in hex chars
     **/
    public String getPassword() {
        return m_password;
    }

    /**
     * MD5 password digest
     **/
    public void setPassword(String pass) {
        m_password = pass;
    }

}
