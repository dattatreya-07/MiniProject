package attendance.model;
import java.time.LocalDate;

public class Faculty {
    private int facultyId;
    private int userId;
    private String name;
    private LocalDate dob;
    private String gender;
    private String mobile;
    private String email;
    private String address;
    private String dept;
    private int yearHandling;
    private String section;

    // getters/setters
    public Faculty() {}
    public int getFacultyId(){ return facultyId; }
    public void setFacultyId(int id){ this.facultyId = id; }
    public int getUserId(){ return userId; }
    public void setUserId(int id){ this.userId = id; }
    public String getName(){ return name; }
    public void setName(String s){ this.name = s; }
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
    public int getYearHandling(){ return yearHandling; }
    public void setYearHandling(int y){ this.yearHandling = y; }
    public String getSection(){ return section; }
    public void setSection(String s){ this.section = s; }
}

