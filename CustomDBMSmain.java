import CustomDBMS.*;

import java.util.*;
import java.io.*;

public class CustomDBMSmain
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

            if(command.length <= 3 && command[0].equalsIgnoreCase("create") && command[1].equalsIgnoreCase("database"))
            {
                String DBName = command[2];

                System.out.println(DBName+" created succesfully");
            }

            else if( command.length <= 2 && command[0].equalsIgnoreCase("show") && command[1].equalsIgnoreCase("databases"))
            {
                File dirFile = new File("CustomDBMS");

                if(dirFile.exists() && dirFile.isDirectory())
                {
                    File databases[] = dirFile.listFiles(File::isDirectory);
                    if(databases != null)
                    {
                        for(File f : databases)
                        {
                            System.out.println(f.getName());
                        }
                    }
                }
            }

            else if(command.length >= 1 && command[0].equalsIgnoreCase("use"))
            {
                String DBName = command[1];

                while (true) 
                {
                    System.out.print(DBName+" > ");

                    Choice = sobj.nextLine();
                    command = Choice.split(" ");
                    try
                    {
                        if(command.length >= 3 && command[0].equalsIgnoreCase("create") && command[1].equalsIgnoreCase("table"))
                        {
                            String tableName = command[2];
                            String colArray[] = Arrays.copyOfRange(command,4,command.length);
                            for(int i = 0; i < colArray.length; i += 3)
                            {
                                columns.add(colArray[i]+" "+colArray[i+1]);
                            }

                            dbHelper = new DBHelper(columns.toArray(new String[0]), DBName, tableName);
                            dbHelper.CreateDatabase(); 

                            Class<?> cName = Class.forName("CustomDBMS."+DBName+"."+tableName+"DB");
                            Object obj = cName.getDeclaredConstructor().newInstance();

                            System.out.println("Table " + tableName + " created and ready.");

                        }
                            
                        else if(command.length >= 3 && command[0].equalsIgnoreCase("insert") && command[1].equalsIgnoreCase("into"))
                        {
                            Class<?> tableClass = Class.forName("CustomDBMS."+DBName+"."+command[2]+"DB");
                            Object tableobj = tableClass.getMethod("RestoreBackup").invoke(null);
                            if(tableobj == null)
                            {
                                tableobj = tableClass.getDeclaredConstructor().newInstance();
                            }
                            tableClass.getMethod("InsertIntoTable").invoke(tableobj);
                            tableClass.getMethod("TakeBackup").invoke(tableobj);
                        }
                        else if(command.length <= 4 && command[0].equalsIgnoreCase("select") && command[1].equalsIgnoreCase("*") && command[2].equalsIgnoreCase("from"))
                        {
                            Class<?> tableClass = Class.forName("CustomDBMS."+DBName+"."+command[3]+"DB");
                            Object tableobj = tableClass.getMethod("RestoreBackup").invoke(null);
                            if (tableobj == null) 
                            {
                                System.out.println("No records found (empty table).");
                            } else 
                            {
                                tableClass.getMethod("SelectAll").invoke(tableobj);
                            }
                        }
                        else if(command.length <= 8 && command[0].equalsIgnoreCase("select") && command[1].equalsIgnoreCase("*") && command[2].equalsIgnoreCase("from") && command[4].equalsIgnoreCase("where"))
                        {
                            Class<?> tableClass = Class.forName("CustomDBMS."+DBName+"."+command[3]+"DB");
                            Object tableobj = tableClass.getMethod("RestoreBackup").invoke(null);
                            if(tableobj == null)
                            {
                                System.out.println("No records found (empty table).");
                            }
                            else
                            {
                                tableClass.getMethod("SelectSpecific",String.class,String.class).invoke(tableobj,command[5],command[7]);
                            }
                        }
                        else if(command.length <= 8 && command[0].equalsIgnoreCase("delete") && command[1].equalsIgnoreCase("*") && command[2].equalsIgnoreCase("from") && command[4].equalsIgnoreCase("where"))
                        {
                            Class<?> tableClass = Class.forName("CustomDBMS."+DBName+"."+command[3]+"DB");
                            Object tableobj = tableClass.getMethod("RestoreBackup").invoke(null);
                            if(tableobj == null)
                            {
                                System.out.println("No records found (empty table).");
                            }
                            else
                            {
                                tableClass.getMethod("DeleteSpecific",String.class,String.class).invoke(tableobj,command[5],command[7]);
                                tableClass.getMethod("TakeBackup").invoke(tableobj);
                            }
                        }
                        else if(command.length >= 4 && command[0].equalsIgnoreCase("update") && command[2].equalsIgnoreCase("set"))
                        {
                            ArrayList<String> params = new ArrayList<>();
                            ArrayList<String> conditions = new ArrayList<>();
                        
                            int i = 3;
                        
                            StringBuilder sb = new StringBuilder();
                            while(i < command.length && !command[i].equalsIgnoreCase("where")) 
                            {
                                sb.append(command[i]).append(" ");
                                i++;
                            }
                            String[] assignments = sb.toString().trim().split(",");
                            for(String assign : assignments) 
                            {
                                if(!assign.trim().isEmpty()) 
                                {
                                    params.add(assign.trim());
                                }
                            }
                        
                            if(i < command.length && command[i].equalsIgnoreCase("where")) 
                            {
                                i++;
                            }
                        
                            while(i < command.length) 
                            {
                                String cond = command[i];
                                if(cond.contains("=") || cond.contains("<") || cond.contains(">")) 
                                {
                                    conditions.add(cond.trim());
                                    i++;
                                } 
                                else if(i + 2 < command.length) 
                                {
                                    conditions.add(command[i] + " " + command[i+1] + " " + command[i+2]);
                                    i += 3;
                                } 
                                else 
                                {
                                    i++;
                                }
                            }
                        
                            Class<?> tableClass = Class.forName("CustomDBMS."+DBName+"."+command[1]+"DB");
                            Object tableobj = tableClass.getMethod("RestoreBackup").invoke(null);
                        
                            if(tableobj == null) 
                            {
                                System.out.println("No records found (empty table).");
                            } 
                            else 
                            {
                                tableClass.getMethod("UpdateSpecific", String[].class, String[].class)
                                          .invoke(tableobj, params.toArray(new String[0]), conditions.toArray(new String[0]));
                                tableClass.getMethod("TakeBackup").invoke(tableobj);
                            }
                        }


                        else if(command.length == 3 && command[0].equalsIgnoreCase("drop") && command[1].equalsIgnoreCase("table"))
                        {
                            String tableName = command[2];
                            File dbDir = new File("CustomDBMS/" + DBName);
                        
                            if(dbDir.exists() && dbDir.isDirectory())
                            {
                                File[] files = dbDir.listFiles((dir, name) -> name.startsWith(tableName));

                                if(files != null && files.length > 0)
                                {
                                    boolean allDeleted = true;
                                    for(File table : files)
                                    {
                                        if(table.delete())
                                        {
                                            System.out.println("Deleted: " + table.getName());
                                        }
                                        else
                                        {
                                            System.out.println("Failed to delete: " + table.getName());
                                            allDeleted = false;
                                        }
                                    }
                                
                                    if(allDeleted)
                                    {
                                        System.out.println("Table " + tableName + " dropped successfully.");
                                    }
                                }
                                else
                                {
                                    System.out.println("No files found for table: " + tableName);
                                }
                            }
                            else
                            {
                                System.out.println("Database not found: " + DBName);
                            }
                        }

                        else if(command.length >= 1 && command[0].equalsIgnoreCase("back"))
                        {
                            break;
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
                        else
                        {
                            System.out.println("Invalid command...");
                        }   
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        System.out.println("Error "+ e.getMessage());
                    }
                }   
            }

            else if( command.length <= 3 && command[0].equalsIgnoreCase("drop") && command[1].equalsIgnoreCase("database"))
            {
                File database = new File("CustomDBMS/"+command[2]);

                if(database.exists() && database.isDirectory())
                {
                    File[] files = database.listFiles();
                    boolean allDeleted = true;

                    if(files != null && files.length > 0)
                    {
                        for(File table : files)
                        {
                            if(table.delete())
                            {
                                System.out.println("Deleted: " + table.getName());
                            }
                            else
                            {
                                System.out.println("Failed to delete: " + table.getName());
                                allDeleted = false;
                            }
                        } 
                    }
                    if(allDeleted)
                    {
                        if(database.delete())
                        {
                            System.out.println(command[2]+" database dropped succesfully");
                        }
                        else
                        {
                            System.out.println("Unable drop datbase : "+command[2]);
                        }
                    }
                }
            }

            else if(command.length <= 1 && command[0].equalsIgnoreCase("help"))
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
            else if(command.length <= 1 && command[0].equalsIgnoreCase("exit"))
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