import CustomDBMS.*;

import java.util.*;
import java.io.*;

public class program848
{
    public static void main(String A[]) throws Exception
    {     
        Scanner sobj = new Scanner(System.in);
        DBHelper dbHelper;

        int mainChoice = 0;
        String Choice= "";
        String command[] = null;
        String colName = "";
        String value = "";

        while(true)
        {
            ArrayList<String> columns = new ArrayList<>();
            command = null;

            System.out.print("MarvellousDBMS > ");
            Choice = sobj.nextLine();

            command = Choice.split(" ");

            if(command.length >= 3 && command[0].equalsIgnoreCase("create") && command[1].equalsIgnoreCase("database"))
            {
                String DBName = command[2];

                System.out.println(DBName+" created succesfully");
            }

            else if(command.length >= 1 && command[0].equalsIgnoreCase("use"))
            {
                String DBName = command[1];
                System.out.print(DBName+" > ");

                Choice = sobj.nextLine();
                command = Choice.split(" ");

                if(command.length >= 3 && command[0].equalsIgnoreCase("create") && command[1].equalsIgnoreCase("table"))
                {
                    String tableName = command[2];
                    String colArray[] = Arrays.copyOfRange(command,4,command.length);
                    for(int i = 0; i < colArray.length-1; i += 2)
                    {
                        columns.add(colArray[i]+" "+colArray[i+1]);
                    }

                    dbHelper = new DBHelper(columns.toArray(new String[0]), DBName, tableName);
                    dbHelper.CreateDatabase(); // <-- MUST call this to create + compile the table

                    // Now load the compiled DB class dynamically
                    Class<?> cName = Class.forName("CustomDBMS."+DBName+"."+tableName+"DB");
                    Object obj = cName.getDeclaredConstructor().newInstance();

                    System.out.println("Table " + tableName + " created and ready.");

                    while(true)
                    {
                        System.out.print(DBName+" > ");

                        Choice = sobj.nextLine();
                        command = Choice.split(" ");

                        if(command.length >= 3 && command[0].equalsIgnoreCase("insert") && command[1].equalsIgnoreCase("into"))
                        {
                            cName.getMethod("InsertIntoTable").invoke(obj);
                        }
                        else if(command.length >= 4 && command[0].equalsIgnoreCase("select") && command[1].equalsIgnoreCase("*") && command[2].equalsIgnoreCase("from") && command[3].equalsIgnoreCase(DBName))
                        {
                            cName.getMethod("SelectAll").invoke(obj);
                        }
                        else if(command.length >= 8 && command[0].equalsIgnoreCase("select") && command[1].equalsIgnoreCase("*") && command[2].equalsIgnoreCase("from") && command[3].equalsIgnoreCase(tableName) && command[4].equalsIgnoreCase("where"))
                        {
                            cName.getMethod("SelectSpecific",String.class,String.class).invoke(obj,command[5],command[7]);
                        }
                        else if(command.length >= 8 && command[0].equalsIgnoreCase("delete") && command[1].equalsIgnoreCase("*") && command[2].equalsIgnoreCase("from") && command[3].equalsIgnoreCase(tableName) && command[4].equalsIgnoreCase("where"))
                        {
                            cName.getMethod("DeleteSpecific",String.class,String.class).invoke(obj,command[5],command[7]);
                        }
                        else if(command.length >= 1 && command[0].equalsIgnoreCase("back"))
                        {
                            break;
                        }
                        else if(command.length >= 1 && command[0].equalsIgnoreCase("help"))
                        {
                            System.out.println("Available commands: insert, select, delete, back, help");
                        }
                        else
                        {
                            System.out.println("Invalid command...");
                        }
                    }
                }
            }

            else if(command.length >= 3 && command[0].equalsIgnoreCase("drop") && command[1].equalsIgnoreCase("table"))
            {
                File file = new File(command[2]+".class");

                if(file.exists())
                {
                    if(file.delete())
                    {
                        System.out.println(command[2]+"table succesfully deleted");
                    }
                    else
                    {
                        System.out.println("Failed to delete "+command[2]+" table");
                    }
                }
            }
            else if(command.length >= 1 && command[0].equalsIgnoreCase("help"))
            {
                System.out.println("Create new database : create database (DB name)");
                System.out.println("use database : use (DB name)");
                System.out.println("delete table : drop table (table name)");
                System.out.println("Insert new entry into DB : Insert into (table name)");
                System.out.println("Get DB : Select * from (table name)");
                System.out.println("Get by specific value : Select * from (table anme) where ___ = ___");
                System.out.println("Delete by specic value : delete from (table name) where ___ = ___");
                System.out.println("Switch to another DB : switch database");
                System.out.println("Go back to main menu :  back");
                System.out.println("To terminate the MarvellousDBMS :  exit");
            }
            else if(command.length >= 1 && command[0].equalsIgnoreCase("exit"))
            {
                System.out.println("Exiting MarvellousDBMS...");
                break;
            }
            else
            {
                System.out.println("Invalid command...");
                System.out.println("Enter command \"help\" for help.");
            }
        }

    
    }// End of main method
}// End of main class