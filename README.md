#  Custom DBMS

A simple **custom-built Database Management System (DBMS)** implemented in **Java**.  
It supports **basic SQL-like commands** such as creating databases and tables, inserting, selecting, updating, and deleting records — all stored in serialized files.  

This project demonstrates **file handling, reflection, dynamic class creation, compilation at runtime, and basic query parsing**.

---

##  Features
-  Create and drop databases  
-  Create and drop tables with dynamic schema  
-  Insert new records with auto-incremented IDs  
-  Select all or filter records with `WHERE` conditions  
-  Update records with conditional logic  
-  Delete records selectively  
-  Backup and restore tables using Java Serialization  
-  Command-line interface with SQL-like syntax  

---

##  Supported Commands

### Database Commands
```sql
create database <DBName>
show databases
use <DBName>
drop database <DBName>
```
### Table Commands 
```sql
create table <tableName> ( int RollNo , String Name , int Age , int Std , String City )
drop table <tableName>
```
### Data Commands
```sql
insert into <tableName>
select * from <tableName>
select * from <tableName> where <column> = <value>
delete * from <tableName> where <column> = <value>
update <tableName> set <col>=<value>[, ...] where <col> <operator> <value>
```
### Utility
```sql
help
back
exit
```
### Example Ussage
```sql
$ java -cp . CustomDBMSmain
MarvellousDBMS > create database classroom
classroom created successfully

MarvellousDBMS > use classroom
classroom > create table student ( int RollNo , String name , int age , int std , String city )
Compilation successful
Table student created and ready.

classroom > insert into student
Enter data for RollNo:
1
Enter data for name:
Amit
Enter data for age:
12
Enter data for std:
7
Enter data for city:
Pune
Database stored successfully.

classroom > select * from student
-----------------------------
id:1, RollNo:1, name:Amit, age:12, std:7, city:Pune
-----------------------------

classroom > update student set city=Nagpur where name=Amit
Update complete.
```
### Tech Stack
Java (Core + Reflection API)
File I/O & Serialization
Java Compiler API (javax.tools)
Collections Framework (LinkedList, ArrayList)

### Project Structure
```bash
CustomDBMS/
│
├── CustomDBMSmain.java       # Entry point, CLI handling
├── DBHelper.java             # Dynamically generates & compiles entity + DB classes
├── <DBName>/                 # Folder for each database
│   ├── <Table>.java          # Entity class (auto-generated)
│   ├── <Table>DB.java        # Table handler class (auto-generated)
│   └── <Table>.ser           # Serialized table data (backup)
```
