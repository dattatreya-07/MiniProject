package attendance.model;
import java.time.LocalDate;

public class Student {
    private int studentId;
    private int userId;
    private String rollNo;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String mobile;
    private String email;
    private String address;
    private String dept;
    private int year;
    private String section;

    // constructors, getters and setters
    public Student() {}
    // getters & setters (generate in Eclipse) ...
    // For brevity include needed getters/setters in your IDE.
    public int getStudentId(){ return studentId; }
    public void setStudentId(int id){ this.studentId = id; }
    public int getUserId(){ return userId; }
    public void setUserId(int id){ this.userId = id; }
    public String getRollNo(){ return rollNo; }
    public void setRollNo(String s){ this.rollNo = s; }
    public String getFullName(){ return fullName; }
    public void setFullName(String s){ this.fullName = s; }
    public LocalDate getDob(){ return dob; }
    public void setDob(LocalDate d){ this.dob = d; }
    public String getGender(){ return gender; }
    public void setGender(String g){ this.gender = g; }
    public String getMobile(){ return mobile; }
    public void setMobile(String m){ this.mobile = m; }
    public String getEmail(){ return email; }
    public void setEmail(String e){ this.email = e; }
    public String getAddress(){ return address; }
    public void setAddress(String a){ this.address = a; }
    public String getDept(){ return dept; }
    public void setDept(String d){ this.dept = d; }
    public int getYear(){ return year; }
    public void setYear(int y){ this.year = y; }
    public String getSection(){ return section; }
    public void setSection(String s){ this.section = s; }
}
