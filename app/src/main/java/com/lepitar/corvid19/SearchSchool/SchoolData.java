package com.lepitar.corvid19.SearchSchool;

public class SchoolData {
    private String schoolName;
    private String schulCode;

    public SchoolData(String schoolName, String schulCode) {
        this.schoolName = schoolName;
        this.schulCode = schulCode;
    }

    public String getSchulCode() {
        return schulCode;
    }

    public void setSchulCode(String schulCode) {
        this.schulCode = schulCode;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
