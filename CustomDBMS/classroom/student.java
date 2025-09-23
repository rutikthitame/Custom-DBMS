package CustomDBMS.classroom;
import java.util.*;
import java.io.*;
public class student implements Serializable 
{
    private int id;
    private int RollNo;
    private String name;
    private int age;
    private int std;
    private String city;
    private static int counter;
    static { counter = 1; }
    public student(int RollNo, String name, int age, int std, String city) {
        this.id = counter++;
        this.RollNo = RollNo;
        this.name = name;
        this.age = age;
        this.std = std;
        this.city = city;
    }
    @Override
    public String toString() 
{
        return "id:" + this.id + ", RollNo:" + this.RollNo + ", name:" + this.name + ", age:" + this.age + ", std:" + this.std + ", city:" + this.city;
    }
}
