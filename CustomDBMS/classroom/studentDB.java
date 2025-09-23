package CustomDBMS.classroom;
import java.util.*;
import java.io.*;
public class studentDB implements Serializable 
{
    private LinkedList<student> list;
    public studentDB() { list = new LinkedList<>(); }
    public void InsertIntoTable() 
{
        Scanner sobj = new Scanner(System.in);
        System.out.println("Enter data for RollNo:");
        int RollNo = sobj.nextInt();
        System.out.println("Enter data for name:");
        sobj.nextLine();
        String name = sobj.nextLine();
        System.out.println("Enter data for age:");
        int age = sobj.nextInt();
        System.out.println("Enter data for std:");
        int std = sobj.nextInt();
        System.out.println("Enter data for city:");
        sobj.nextLine();
        String city = sobj.nextLine();
        student obj = new student(RollNo, name, age, std, city);
        list.add(obj);
    }
    public void SelectAll() 
{
        System.out.println("-----------------------------");
        for(student obj : list) 
{
            System.out.println(obj);
        }
        System.out.println("-----------------------------");
    }
    public void TakeBackup() {
        try 
{
            FileOutputStream fos = new FileOutputStream("CustomDBMS/classroom/student.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close(); fos.close();
            System.out.println("Database stored successfully.");
        } catch(Exception e) 
{ System.out.println(e); }
    }
    public static studentDB RestoreBackup() 
{
        try 
{
            FileInputStream fis = new FileInputStream("CustomDBMS/classroom/student.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            studentDB ret = (studentDB)ois.readObject();
            ois.close(); fis.close();
            return ret;
        } catch(Exception e) 
{ System.out.println(e); return null; }
    }
    public void SelectSpecific(String column, String value) 
{
        boolean found = false;
        System.out.println("--------------------------------------------------------");        for(student obj : list) 
{
            try 
{
                java.lang.reflect.Field field = obj.getClass().getDeclaredField(column);
                field.setAccessible(true);
                Object fieldValue = field.get(obj);
                if(fieldValue instanceof String) 
{
                    if(((String)fieldValue).equals(value)) 
{
                        System.out.println(obj);
                        found = true;
                    }
                } else if(fieldValue instanceof Integer) 
{
                    if(((Integer)fieldValue) == Integer.parseInt(value)) {
                        System.out.println(obj);
                        found = true;
                    }
                }
            } catch(Exception e) 
{ e.printStackTrace(); }
        }
        System.out.println("--------------------------------------------------------");        if(!found) System.out.println("No such record");
    }
    public void DeleteSpecific(String column, String value) 
{
        boolean found = false;
        ArrayList<Integer> indexList = new ArrayList<>();
int index = 0;        for(student obj : list) 
{
            try 
{
                java.lang.reflect.Field field = obj.getClass().getDeclaredField(column);
                field.setAccessible(true);
                Object fieldValue = field.get(obj);
                if(fieldValue instanceof String) 
{
                    if(((String)fieldValue).equals(value)) 
{
                        indexList.add(index);
                        found = true;
                    }
                } else if(fieldValue instanceof Integer) 
{
                    if(((Integer)fieldValue) == Integer.parseInt(value)) {
                        indexList.add(index);
                        found = true;
                    }
index++;                }
            } catch(Exception e) 
{ e.printStackTrace(); }
        }
        if(!found) System.out.println("No such record");
         else{
for(int i : indexList)
{
list.remove(i);
System.out.println("Record deleted succesfully");}
}
    }
    public void UpdateSpecific(String params[], String conditions[]) 
{
        boolean found = false;
        for(student obj : list) 
{
            boolean matches = true;
            try {
                // --- Check all conditions ---
                for(String cond : conditions) {
                    if(cond == null || cond.trim().isEmpty()) continue;
                    String[] condArray = cond.trim().split(" ");
                    if(condArray.length < 3) continue;
                    String fieldName = condArray[0].trim();
                    String operator = condArray[1].trim();
                    String condValue = condArray[2].trim();
                    java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object fieldValue = field.get(obj);
                    if(fieldValue instanceof Number) {
                        int fVal = ((Number)fieldValue).intValue();
                        int cVal = Integer.parseInt(condValue);
                        switch(operator) {
                            case "<": matches &= (fVal < cVal); break;
                            case ">": matches &= (fVal > cVal); break;
                            case "=": matches &= (fVal == cVal); break;
                            default: matches = false;
                        }
                    } else {
                        matches &= fieldValue.toString().trim().equalsIgnoreCase(condValue.trim());
                    }
                }
                // --- Apply updates if record matches ---
                if(matches) {
                    found = true;
                    for(String param : params) {
                        if(param == null || param.trim().isEmpty()) continue;
                        String[] paramArray = param.split("=");
                        if(paramArray.length < 2) continue;
                        String fieldName = paramArray[0].trim();
                        String newValue = paramArray[1].trim();
                        if(fieldName.equalsIgnoreCase("id")) continue; // skip ID updates
                        java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        if(field.getType() == int.class) {
                            field.setInt(obj, Integer.parseInt(newValue));
                        } else if(field.getType() == double.class) {
                            field.setDouble(obj, Double.parseDouble(newValue));
                        } else {
                            field.set(obj, newValue.trim());
                        }
                    }
                }
            } catch(Exception e) { e.printStackTrace(); }
        }
        if(!found) System.out.println("No matching records found for update.");
        else System.out.println("Update complete.");
    }
}
