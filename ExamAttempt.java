package com.example.certigui;

import java.time.LocalDate;

public class ExamAttempt {
    private int personId;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String certificationName;
    private boolean passed;

    public ExamAttempt(String[] data) {
        this.personId = Integer.parseInt(data[1]);
        this.firstName = data[2];
        this.lastName = data[3];
        this.birthday = (data[4] == null || data[4].isEmpty()) ? null : LocalDate.parse(data[4]);
        this.certificationName = data[6];
        this.passed = Boolean.parseBoolean(data[14]);
    }

    public String getFullName() { return lastName + " " + firstName; }

    public int getAge(int currentYear) {
        return birthday == null ? 0 : currentYear - birthday.getYear();
    }
    public String getCertificationName() { return certificationName; }

    public boolean isPassed() { return passed; }
}
