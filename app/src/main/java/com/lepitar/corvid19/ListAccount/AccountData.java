package com.lepitar.corvid19.ListAccount;

public class AccountData {
    private String name;
    private String schoolName;
    private String birth;
    private String k;
    private String overlap;
    private String website;
    private String smskey;
    private Boolean sms;

    public String getSmskey() {
        return smskey;
    }

    public void setSmskey(String smskey) {
        this.smskey = smskey;
    }

    public AccountData(String name, String schoolName, String birth, String k, String overlap, String smskey, String website, Boolean sms) {
        this.name = name;
        this.schoolName = schoolName;
        this.birth = birth;
        this.k = k;
        this.overlap = overlap;
        this.website = website;
        this.sms = sms;
        this.smskey = smskey;
    }

    public Boolean getSms() {
        return sms;
    }

    public void setSms(Boolean sms) {
        this.sms = sms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getOverlap() {
        return overlap;
    }

    public void setOverlap(String overlap) {
        this.overlap = overlap;
    }
}
