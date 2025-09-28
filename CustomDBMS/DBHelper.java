
/******************************************************
 * File: DBHelper.java
 * Package: CustomDBMS
 * Author: Rutik Thitame
 * Date:   23-09-2025
 * 
 * Description:
 * ------------
 * Helper class for the Custom DBMS project.
 * This class dynamically generates Java source code
 * for Entity and DB classes based on user-defined 
 * schema, compiles them at runtime, and provides 
 * support for CRUD operations (Insert, Select, Update,
 * Delete) along with Backup/Restore functionality.
 ******************************************************/

package CustomDBMS;

import javax.tools.*;
import java.io.*;

/**
 * DBHelper class
 * ----------------
 * Responsible for:
 *  - Generating entity class (.java)
 *  - Generating DB class (.java)
 *  - Compiling them dynamically
 *  - Storing inside "CustomDBMS/<DBName>/"
 */

// Helper class
public class DBHelper 
{
    private String Arr[];
    private String cName;
    private String DBName;

    public DBHelper(String Arr[], String DBName, String className) 
    {
        this.Arr = Arr;
        this.cName = className;
        this.DBName = DBName;
    }


    /**
     * CreateDatabase
     * ---------------
     * Generates source code for:
     *  1. Entity class (e.g., Student.java)
     *  2. Table handler class (e.g., StudentDB.java)
     * Compiles both at runtime and saves them under
     * CustomDBMS/<DBName>/
     */

    public void CreateDatabase() throws Exception 
    {
        String className = cName;


        /**********************************************
         * ENTITY CLASS (e.g., Student.java)
         **********************************************/

        StringBuilder source = new StringBuilder();
        source.append("package CustomDBMS.").append(DBName).append(";\n");
        source.append("import java.util.*;\n");
        source.append("import java.io.*;\n");
        source.append("public class ").append(className).append(" implements Serializable \n{\n");

        // Fields
        source.append("    private int id;\n");
        for (String str : Arr) 
        {
            source.append("    private ").append(str).append(";\n");
        }

        // id auto increment
        source.append("    private static int counter;\n");
        source.append("    static { counter = 1; }\n");

        // Constructor params + body
        StringBuilder constructorParams = new StringBuilder();
        StringBuilder resourceString = new StringBuilder();

        for (int i = 0; i < Arr.length; i++) 
        {
            String str = Arr[i];
            String Brr[] = str.split(" "); // [type, name]

            constructorParams.append(Brr[0]).append(" ").append(Brr[1]);
            resourceString.append("        this.").append(Brr[1]).append(" = ").append(Brr[1]).append(";\n");

            if (i != Arr.length - 1) 
            {
                constructorParams.append(", ");
            }
        }

        // Constructor
        source.append("    public ").append(className).append("(").append(constructorParams).append(") {\n")
              .append("        this.id = counter++;\n")
              .append(resourceString)
              .append("    }\n");

        // toString
        StringBuilder returnString = new StringBuilder("\"id:\" + this.id");
        for (int i = 0; i < Arr.length; i++) 
        {
            String Brr[] = Arr[i].split(" ");
            returnString.append(" + \", ").append(Brr[1]).append(":\" + this.").append(Brr[1]);
        }
        source.append("    @Override\n")
              .append("    public String toString() \n{\n")
              .append("        return ").append(returnString).append(";\n")
              .append("    }\n");

        // Close entity class
        source.append("}\n");



        /**********************************************
         * DATABASE CLASS (e.g., StudentDB.java)
         **********************************************/

        StringBuilder source2 = new StringBuilder();
        // Create DB class
        source2.append("package CustomDBMS.").append(DBName).append(";\n");
        source2.append("import java.util.*;\n");
        source2.append("import java.io.*;\n");
        source2.append("public class ").append(className).append("DB implements Serializable \n{\n")
              .append("    private LinkedList<").append(className).append("> list;\n")
              .append("    public ").append(className).append("DB() { list = new LinkedList<>(); }\n");




        /**********************************************
         * INSERT INTO TABLE
         **********************************************/

        StringBuilder printStatement = new StringBuilder();

        for (int i = 0; i < Arr.length; i++) 
        {
            String Drr[] = Arr[i].split(" "); // [type, name]

            printStatement.append("        System.out.println(\"Enter data for ").append(Drr[1]).append(":\");\n");

            if (Drr[0].equals("int")) 
            {
                printStatement.append("        int ").append(Drr[1]).append(" = sobj.nextInt();\n");
                printStatement.append("        sobj.nextLine();\n");
            } 
            else if (Drr[0].equals("String")) 
            {
                printStatement.append("        String ").append(Drr[1]).append(" = sobj.nextLine();\n");
            }
        }

        source2.append("    public void InsertIntoTable() \n{\n")
              .append("        Scanner sobj = new Scanner(System.in);\n")
              .append(printStatement)
              .append("        ").append(className).append(" obj = new ").append(className).append("(");

        for (int i = 0; i < Arr.length; i++) 
        {
            String Drr[] = Arr[i].split(" ");
            source2.append(Drr[1]);
            if (i != Arr.length - 1) 
            {
                source2.append(", ");
            }
        }

        source2.append(");\n")
              .append("        list.add(obj);\n")
              .append("    }\n");

        
        /**********************************************
         * SELECT * FROM TABLE
         **********************************************/
        source2.append("    public void SelectAll() \n{\n")
              .append("        System.out.println(\"-----------------------------\");\n")
              .append("        for(").append(className).append(" obj : list) \n{\n")
              .append("            System.out.println(obj);\n")
              .append("        }\n")
              .append("        System.out.println(\"-----------------------------\");\n")
              .append("    }\n");

        
        /**********************************************
         * TAKE / RESTORE BACKUP
         **********************************************/
        source2.append("    public void TakeBackup() {\n")
            .append("        try \n{\n")
            .append("            FileOutputStream fos = new FileOutputStream(\"CustomDBMS/")
            .append(DBName).append("/").append(className).append(".ser\");\n")
            .append("            ObjectOutputStream oos = new ObjectOutputStream(fos);\n")
            .append("            oos.writeObject(this);\n")
            .append("            oos.close(); fos.close();\n")
            .append("            System.out.println(\"Database stored successfully.\");\n")
            .append("        } catch(Exception e) \n{ System.out.println(e); }\n")
            .append("    }\n");


        // RestoreBackup
        source2.append("    public static ").append(className).append("DB RestoreBackup() \n{\n")
            .append("        try \n{\n")
            .append("            FileInputStream fis = new FileInputStream(\"CustomDBMS/")
            .append(DBName).append("/").append(className).append(".ser\");\n")
            .append("            ObjectInputStream ois = new ObjectInputStream(fis);\n")
            .append("            ").append(className).append("DB ret = (").append(className).append("DB)ois.readObject();\n")
            .append("            ois.close(); fis.close();\n")
            .append("            return ret;\n")
            .append("        } catch(Exception e) \n{ System.out.println(e); return null; }\n")
            .append("    }\n");


        
        /**********************************************
         * SELECT * FROM TABLE WHERE <condition>
         **********************************************/ 
        source2.append("    public void SelectSpecific(String column, String value) \n{\n")
              .append("        boolean found = false;\n")
              .append("        System.out.println(\"--------------------------------------------------------\");")  
              .append("        for(").append(className).append(" obj : list) \n{\n")
              .append("            try \n{\n")
              .append("                java.lang.reflect.Field field = obj.getClass().getDeclaredField(column);\n")
              .append("                field.setAccessible(true);\n")
              .append("                Object fieldValue = field.get(obj);\n")
              .append("                if(fieldValue instanceof String) \n{\n")
              .append("                    if(((String)fieldValue).equals(value)) \n{\n")
              .append("                        System.out.println(obj);\n")
              .append("                        found = true;\n")
              .append("                    }\n")
              .append("                } else if(fieldValue instanceof Integer) \n{\n")
              .append("                    if(((Integer)fieldValue) == Integer.parseInt(value)) {\n")
              .append("                        System.out.println(obj);\n")
              .append("                        found = true;\n")
              .append("                    }\n")
              .append("                }\n")
              .append("            } catch(Exception e) \n{ e.printStackTrace(); }\n")
              .append("        }\n")
              .append("        System.out.println(\"--------------------------------------------------------\");")
              .append("        if(!found) System.out.println(\"No such record\");\n")
              .append("    }\n");

        
        /**********************************************
         * DELETE FROM TABLE WHERE <condition>
         **********************************************/
        source2.append("    public void DeleteSpecific(String column, String value) \n{\n")
              .append("        boolean found = false;\n")
              .append("        ArrayList<Integer> indexList = new ArrayList<>();\nint index = 0;")
              .append("        for(").append(className).append(" obj : list) \n{\n")
              .append("            try \n{\n")
              .append("                java.lang.reflect.Field field = obj.getClass().getDeclaredField(column);\n")
              .append("                field.setAccessible(true);\n")
              .append("                Object fieldValue = field.get(obj);\n")
              .append("                if(fieldValue instanceof String) \n{\n")
              .append("                    if(((String)fieldValue).equals(value)) \n{\n")
              .append("                        indexList.add(index);\n")
              .append("                        found = true;\n")
              .append("                    }\n")
              .append("                } else if(fieldValue instanceof Integer) \n{\n")
              .append("                    if(((Integer)fieldValue) == Integer.parseInt(value)) {\n")
              .append("                        indexList.add(index);\n")
              .append("                        found = true;\n")
              .append("                    }\nindex++;")
              .append("                }\n")
              .append("            } catch(Exception e) \n{ e.printStackTrace(); }\n")
              .append("        }\n")
              .append("        if(!found) System.out.println(\"No such record\");\n")
              .append("         else{\nfor(int i : indexList)\n{\nlist.remove(i);\nSystem.out.println(\"Record deleted succesfully\");}\n}\n")
              .append("    }\n");

        
        /**********************************************
         * UPDATE TABLE SET <params>[,...] WHERE <conditions>[,...]
         **********************************************/
        source2.append("    public void UpdateSpecific(String params[], String conditions[]) \n{\n")
            .append("        boolean found = false;\n")
            .append("        for(").append(className).append(" obj : list) \n{\n")
            .append("            boolean matches = true;\n")
            .append("            try {\n")
            .append("                for(String cond : conditions) {\n")
            .append("                    if(cond == null || cond.trim().isEmpty()) continue;\n")
            .append("                    String[] condArray = cond.trim().split(\" \");\n")
            .append("                    if(condArray.length < 3) continue;\n")
            .append("                    String fieldName = condArray[0].trim();\n")
            .append("                    String operator = condArray[1].trim();\n")
            .append("                    String condValue = condArray[2].trim();\n")
            .append("                    java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);\n")
            .append("                    field.setAccessible(true);\n")
            .append("                    Object fieldValue = field.get(obj);\n")
            .append("                    if(fieldValue instanceof Number) {\n")
            .append("                        int fVal = ((Number)fieldValue).intValue();\n")
            .append("                        int cVal = Integer.parseInt(condValue);\n")
            .append("                        switch(operator) {\n")
            .append("                            case \"<\": matches &= (fVal < cVal); break;\n")
            .append("                            case \">\": matches &= (fVal > cVal); break;\n")
            .append("                            case \"=\": matches &= (fVal == cVal); break;\n")
            .append("                            default: matches = false;\n")
            .append("                        }\n")
            .append("                    } else {\n")
            .append("                        matches &= fieldValue.toString().trim().equalsIgnoreCase(condValue.trim());\n")
            .append("                    }\n")
            .append("                }\n")
            .append("                if(matches) {\n")
            .append("                    found = true;\n")
            .append("                    for(String param : params) {\n")
            .append("                        if(param == null || param.trim().isEmpty()) continue;\n")
            .append("                        String[] paramArray = param.split(\"=\");\n")
            .append("                        if(paramArray.length < 2) continue;\n")
            .append("                        String fieldName = paramArray[0].trim();\n")
            .append("                        String newValue = paramArray[1].trim();\n")
            .append("                        if(fieldName.equalsIgnoreCase(\"id\")) continue; // skip ID updates\n")
            .append("                        java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);\n")
            .append("                        field.setAccessible(true);\n")
            .append("                        if(field.getType() == int.class) {\n")
            .append("                            field.setInt(obj, Integer.parseInt(newValue));\n")
            .append("                        } else if(field.getType() == double.class) {\n")
            .append("                            field.setDouble(obj, Double.parseDouble(newValue));\n")
            .append("                        } else {\n")
            .append("                            field.set(obj, newValue.trim());\n")
            .append("                        }\n")
            .append("                    }\n")
            .append("                }\n")
            .append("            } catch(Exception e) { e.printStackTrace(); }\n")
            .append("        }\n")
            .append("        if(!found) System.out.println(\"No matching records found for update.\");\n")
            .append("        else System.out.println(\"Update complete.\");\n")
            .append("    }\n");


        // Close DB class
        source2.append("}\n");


        /**********************************************
         * FILE CREATION & COMPILATION
         **********************************************/
        File dir = new File("CustomDBMS/"+DBName);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, className + ".java");
        try (FileWriter fw = new FileWriter(file)) 
        {
            fw.write(source.toString());
        }

        File file2 = new File(dir, className + "DB.java");
        try (FileWriter fw = new FileWriter(file2)) 
        {
            fw.write(source2.toString());
        }

        // Compile with -d option
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, "-d", ".", file.getPath(),file2.getPath());

        if (result == 0) 
        {
            System.out.println("Compilation successful");
        } else 
        {
            System.out.println("Compilation failed");
        }
    }
}
