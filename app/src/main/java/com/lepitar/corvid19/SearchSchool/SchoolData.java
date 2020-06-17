package com.lepitar.corvid19.SearchSchool;

public class SchoolData {
    private String schoolName;
    private String shculCode;

    public SchoolData(String schoolName, String shculCode) {
        this.schoolName = schoolName;
        this.shculCode = shculCode;
    }

    public String getShculCode() {
        return shculCode;
    }

    public void setShculCode(String shculCode) {
        this.shculCode = shculCode;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
