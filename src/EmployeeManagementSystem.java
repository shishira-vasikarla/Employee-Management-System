import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class EmployeeManagementSystem {

    Connection connection;
    Statement statement;

    EmployeeManagementSystem(String db_name, String db_username, String db_password) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", db_username,
                db_password);
        Statement statement = connection.createStatement();
        String query = "CREATE DATABASE IF NOT EXISTS employee_management_system";
        statement.executeUpdate(query);
        connection.close();

        this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db_name, db_username,
                db_password);
        this.statement = this.connection.createStatement();
        String create_employee_table = "create table if not exists employee (id int primary key, name varchar(25), " +
                "gender varchar(10), dob date, phoneNum varchar(13), email varchar(25), designation varchar(20), " +
                "salary double);";
        this.statement.executeUpdate(create_employee_table);

        String create_admin_table = "create table if not exists admin (username varchar(25) primary key, " +
                "password varchar(25));";
        this.statement.executeUpdate(create_admin_table);
    }

    public boolean login(String username, String password) throws SQLException {
        Statement statement = connection.createStatement();
        String query = String.format("select password from admin where username = '%s';", username);
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getString(1).equals(password);
        }
        return false;
    }

    public void viewEmployees() throws SQLException {
        String view = "select * from employee;";
        ResultSet resultSet = statement.executeQuery(view);
        System.out.println();
        while (resultSet.next()){
            int id = resultSet.getInt(1);
            String name = resultSet.getString(2);
            String gender = resultSet.getString(3);
            Date dob = resultSet.getDate(4);
            String phone_num = resultSet.getString(5);
            String email = resultSet.getString(6);
            String designation = resultSet.getString(7);
            double salary = resultSet.getDouble(8);
            LocalDate dob_ = LocalDate.parse(dob.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate curDate = LocalDate.now();
            int age = Period.between(dob_, curDate).getYears();
            System.out.println("----------------------------------------------------------------");
            System.out.printf("""
                    Employee ID: %d
                    Name: %s
                    Age: %d
                    Gender: %s
                    Date of birth: %s
                    Phone number: %s
                    Email: %s
                    Designation: %s
                    Salary: $%.2f%n""", id, name, age, gender, dob, phone_num, email, designation, salary
            );
            System.out.println("----------------------------------------------------------------");
            System.out.println();
        }

    }

    public void addEmployee(int employee_id, String employee_name, String gender, String dob, String phone_num, String email,
                            String designation, double salary) throws SQLException {

        try {
            String insert_employee = String.format("insert into employee values ('%d', '%s', '%s', '%s', '%s', '%s', '%s', " +
                    "'%.2f')" , employee_id, employee_name, gender, dob, phone_num, email, designation, salary);

            statement.executeUpdate(insert_employee);
            System.out.println("New employee added successfully!");

        }
        catch (java.sql.SQLIntegrityConstraintViolationException e){
            System.out.printf("Employee with id %d already exists\n", employee_id);
        }
    }

    public void modifyEmployee(int employee_id, String field, String value) throws SQLException {
        String update = String.format("update employee set %s = '%s' where id = %d;", field, value, employee_id);
        statement.executeUpdate(update);
    }

    public void modifyEmployee(int employee_id, String field, double value) throws SQLException {
        String update = String.format("update employee set %s = '%.2f' where id = %d;", field, value, employee_id);
        statement.executeUpdate(update);
    }

    public void deleteEmployee(int employee_id) throws SQLException {
        String delete = "delete from employee where id = " + employee_id;
        statement.executeUpdate(delete);
    }

    public void addAdmin(String username, String password) throws SQLException {

        try {
            String insert = String.format("insert into admin values ('%s', '%s');", username, password);
            statement.executeUpdate(insert);
            System.out.println("Admin added successfully!");
        }
        catch (java.sql.SQLIntegrityConstraintViolationException e){
            System.out.println("Username already exists!");
        }
    }


    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        String db_name = "employee_management_system";
        String db_username = "root";
        String db_password = "Mysql123";

        Scanner s = new Scanner(System.in);

        EmployeeManagementSystem ems = new EmployeeManagementSystem(db_name, db_username, db_password);

        System.out.println("Welcome to Employee Management System!");

        while (true){
            System.out.println("Enter username: ");
            String username = s.nextLine();
            System.out.println("Enter password: ");
            String password = s.nextLine();

            if (ems.login(username, password)){
                System.out.println("Login Successful!");

                while (true) {

                    System.out.println("\t1. View all Employees");
                    System.out.println("\t2. Add an Employee");
                    System.out.println("\t3. Modify Employee");
                    System.out.println("\t4. Delete Employee");
                    System.out.println("\t5. Add an Admin");
                    System.out.println("\tq. Quit");

                    String choice = s.nextLine();

                    if (choice.toLowerCase().equals("q")) {
                        System.out.println("Bye!");
                        break;
                    }

                    try {
                        int c = Integer.parseInt(choice);

                        switch (c) {
                            case 1 -> ems.viewEmployees();
                            case 2 -> {
                                System.out.println("Enter employee details");
                                System.out.println("Enter employee id");
                                int employee_id = s.nextInt();
                                s.nextLine();
                                System.out.println("Enter employee name");
                                String name = s.nextLine();
                                System.out.println("Enter employee gender");
                                String gender = s.nextLine();
                                System.out.println("Enter employee date of birth (yyyy-mm-dd format)");
                                String dob = s.nextLine();
                                System.out.println("Enter employee phone number");
                                String phone_num = s.nextLine();
                                System.out.println("Enter employee email");
                                String email = s.nextLine();
                                System.out.println("Enter employee designation");
                                String designation = s.nextLine();
                                System.out.println("Enter employee salary");
                                double salary = s.nextDouble();
                                s.nextLine();
                                ems.addEmployee(employee_id, name, gender, dob, phone_num, email, designation, salary);

                            }
                            case 3 -> {
                                ems.viewEmployees();
                                System.out.println("Select employee by ID");
                                int id = s.nextInt();
                                System.out.println("Select field");
                                System.out.println("1. Phone Number");
                                System.out.println("2. Email");
                                System.out.println("3. Designation");
                                System.out.println("4. Salary");
                                c = s.nextInt(); s.nextLine();
                                String field = "";

                                switch (c){
                                    case 1 -> field = "phone_num";
                                    case 2 -> field = "email";
                                    case 3 -> field = "designation";
                                    case 4 -> field = "salary";
                                }

                                System.out.println("Enter new value");

                                String value = s.nextLine();

                                if (c == 4){
                                    double sal = Double.parseDouble(value);
                                    ems.modifyEmployee(id, field, sal);
                                }
                                else {
                                    ems.modifyEmployee(id, field, value);
                                }
                                System.out.println("Employee updated successfully!");
                            }

                            case 4 -> {
                                ems.viewEmployees();
                                ems.viewEmployees();
                                System.out.println("Select employee by ID");
                                int id = s.nextInt(); s.nextLine();
                                System.out.println("Are you sure you want to delete employee with id? Press y to confirm" + id);
                                String ch = s.nextLine();
                                if(ch.toLowerCase().equals("y")){
                                    ems.deleteEmployee(id);
                                    System.out.println("Employee deleted successfully!");
                                }
                                else {
                                    System.out.println("Deletion cancelled");
                                }
                            }

                            case  5 -> {
                                System.out.println("Enter new username: ");
                                username = s.nextLine();
                                System.out.println("Enter new password: ");
                                password = s.nextLine();
                                ems.addAdmin(username, password);
                            }
                        }


                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }

                }

                break;

            }
            else {
                System.out.println("Invalid Credentials!");
            }
        }

    }
}
