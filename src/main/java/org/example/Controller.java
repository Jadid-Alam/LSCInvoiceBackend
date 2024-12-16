package org.example;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class Controller {

    public static List<Student> s = new ArrayList<>();
    public static List<Parent> p = new ArrayList<>();
    public static List<Address> a = new ArrayList<>();


    @GetMapping("/get-parents")
    public List<Parent> getParents() {
        System.out.println(p.toString());
        return p;
    }

    @PostMapping("/build-pdf")
    public void buildPdf(@RequestBody BuildJson b) {
        /*
        int parentId = b.parentId;
        for (Student student : s) {
            if (student.parentId == parentId) {
                System.out.println("Student: " + student.name + " DOB: " + student.dob);
            }
        }
        for (Parent parent : p) {
            if (parent.id == parentId) {
                System.out.println("Parent: " + parent.fName + " " + parent.lName);
            }
        }
        for (Address address : a) {
            if (address.addressId == parentId) {
                System.out.println("Address: " + address.address + " " + address.town + " " + address.county + " " + address.postcode);
            }
        }
*/

    }

    @PostMapping("/add-customer")
    public void addCustomer(@RequestBody CustomerJson c) {
        System.out.println("Adding customer");
        int n = p.size()+1;
        Parent newParent = new Parent(
                n,
                c.getTitle(),
                c.getFirstName(),
                c.getLastName(),
                c.getRegistry(),
                n
        );
        p.add(newParent);
        Address newAddress = new Address(
                n,
                c.getAddress(),
                c.getTown(),
                c.getCounty(),
                c.getPostcode()
        );
        a.add(newAddress);
    }

    @PostMapping("/add-student")
    public void addStudent(@RequestBody StudentJson s1) {
        int n = p.size();
        s.add(new Student(n, s1.studentName1, s1.studentDOB1));
        if (s1.studentName2 != null && !s1.studentName1.isEmpty()) {
            s.add(new Student(n, s1.studentName2, s1.studentDOB2));
        }
        if (s1.studentName3 != null && !s1.studentName3.isEmpty()) {
            s.add(new Student(n, s1.studentName3, s1.studentDOB3));
        }
        if (s1.studentName4 != null && !s1.studentName4.isEmpty()) {
            s.add(new Student(n, s1.studentName4, s1.studentDOB4));
        }
    }
}

class Student
{
    int parentId;
    String name;
    String dob;

    public Student(int parentId, String name, String dob)
    {
        this.parentId = parentId;
        this.name = name;
        this.dob = dob;
    }
}

class BuildJson
{
    int parentId;
    String invoiceDate;
    String startDate;
    String endDate;
    String hoursPerWeek;
    String feesPerWeek;
    String totalPrice;
}

class StudentJson
{
    String studentName1;
    String studentDOB1;
    String studentName2;
    String studentDOB2;
    String studentName3;
    String studentDOB3;
    String studentName4;
    String studentDOB4;

}
