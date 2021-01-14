package Models;

import Utility.DatabaseConnection;
import Utility.SQLQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class Customer {

    private int customerId;
    private String customerName;

    private int addressId;
    private String phone;
    private String address;

    private static int addressIdHolder;

    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static final ObservableList<Customer> phoneTree = FXCollections.observableArrayList();

    //Customer Constructor

    public Customer(int customerId, String customerName, int addressId, String phone, String address) {

        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
        this.phone = phone;
        this.address = address;

    }

    // Customer constructor for the Phone Tree method

    public Customer(String customerName, String phone) {
        this.customerName = customerName;
        this.phone = phone;
    }

    //Getters and Setters Methods

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getCustomerId(String customerName) {
        Connection connection = DatabaseConnection.getConnection();
        String customerIdSelect = "SELECT customerId FROM customer WHERE customerName = ?";
        ResultSet rs;
        customerId = 0;

        SQLQuery.setPreparedStatement(connection, customerIdSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setString(1, customerName);
            rs = ps.executeQuery();
            rs.next();
            customerId = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getAddressId() {return addressId;}

    public void setAddressId(int addressId) {this.addressId = addressId;}

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static ObservableList<Customer> getAllCustomers() {

        allCustomers.clear();

        //Establish connection with DB and create variables for sql statement and result set

        Connection connection = DatabaseConnection.getConnection();
        String selectStatement = "SELECT customerId, customerName, t1.addressId, phone, address FROM customer t1 " +
                "LEFT JOIN address t2 ON t1.addressId = t2.addressId ORDER BY customerId;";
        ResultSet rs;

        SQLQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            rs = ps.executeQuery();

            while (rs.next()) {
                Customer newCustomer = new Customer(
                        rs.getInt("customerId"),
                        rs.getString("customerName"),
                        rs.getInt("addressId"),
                        rs.getString("phone"),
                        rs.getString("address")
                );
                allCustomers.add(newCustomer);
            }
            return allCustomers;
        } catch (SQLException e) {
            System.out.println("6. " + e.getMessage());
        }
        return null;
    }

    //Candidate for Lambda as its used only once.
    public static ObservableList<Customer> getPhoneTree() {

        phoneTree.clear();

        ObservableList<Customer> allCustomers = Customer.getAllCustomers();

        for (Customer customer : allCustomers) {
            Customer phoneCustomer = new Customer(customer.getCustomerName(), customer.getPhone());
            phoneTree.add(phoneCustomer);
        }
        return phoneTree;

    }

    //Methods to add, update, and delete customer records in DB
    public static void addCustomer(String customerName, String phoneNumber, String address) {

        ObservableList<Customer> allCustomers = Customer.getAllCustomers();

        Connection connection = DatabaseConnection.getConnection();
        String addressInsert = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) " +
                "VALUES (?, '', 3, '11111', ?, NOW(), 'test', 'test');";

        SQLQuery.setPreparedStatement(connection, addressInsert);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {

            if (allCustomers.isEmpty()) {

               ps.setString(1, address);
               ps.setString(2, phoneNumber);
               ps.executeUpdate();

               SQLQuery.setPreparedStatement(connection, "SELECT MAX(addressId) FROM address;");
               PreparedStatement prepared = SQLQuery.getPreparedStatement();

               try {

                   ResultSet resultSet = prepared.executeQuery();
                   resultSet.next();
                   addressIdHolder = resultSet.getInt(1);

               } catch (SQLException e) {

                   System.out.println("Didn't get addressId: " + e.getMessage());

               }
            }

            else {

                for (Customer customer : allCustomers) {

                    if (customer.getAddress().equals(address) && customer.getPhone().equals(phoneNumber)) {

                        addressIdHolder = customer.getAddressId();
                        break;
                    }
                    else if (allCustomers.lastIndexOf(customer) == allCustomers.size() - 1) {

                        try {

                            ps.setString(1, address);
                            ps.setString(2, phoneNumber);
                            ps.executeUpdate();

                        } catch (SQLException e) {

                            System.out.println("112 Error :" + e.getMessage());
                        }
                        String addressIdSelect = "SELECT MAX(addressId) FROM address;";
                        SQLQuery.setPreparedStatement(connection, addressIdSelect);
                        ps = SQLQuery.getPreparedStatement();

                        try {
                            ResultSet rs = ps.executeQuery();
                            rs.next();
                            addressIdHolder = rs.getInt(1);

                        } catch (SQLException e) {

                            System.out.println("getting addressId: " + e.getMessage());
                        }
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("1. " + e.getMessage());

        }

        String customerInsert = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) " +
                "VALUES (?, ?, 1, NOW(), 'test', 'test');";

        SQLQuery.setPreparedStatement(connection, customerInsert);
        ps = SQLQuery.getPreparedStatement();
        try {
            ps.setString(1, customerName);
            ps.setInt(2, addressIdHolder);
            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("2. " + e.getMessage());
        }
    }

    public static void updateCustomer(int customerId, String customerName, int addressId, String phoneNumber, String address) {

        Connection connection = DatabaseConnection.getConnection();

        String addressUpdate = "UPDATE address SET phone = ?, address = ? WHERE addressId = ? AND ((SELECT COUNT(customerId) FROM customer WHERE addressId = ?) = 1);";
        SQLQuery.setPreparedStatement(connection, addressUpdate);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {

            ps.setString(1, phoneNumber);
            ps.setString(2, address);
            ps.setInt(3, addressId);
            ps.setInt(4, addressId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {

                SQLQuery.setPreparedStatement(connection, "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) " +
                        "VALUES (?, '', 3, '11111', ?, NOW(), 'test', 'test');");
                ps = SQLQuery.getPreparedStatement();

                ps.setString(1, address);
                ps.setString(2, phoneNumber);
                ps.executeUpdate();

                SQLQuery.setPreparedStatement(connection, "SELECT MAX(addressId) FROM address;");
                PreparedStatement prepared = SQLQuery.getPreparedStatement();

                try {

                    ResultSet resultSet = prepared.executeQuery();
                    resultSet.next();
                    addressIdHolder = resultSet.getInt(1);

                } catch (SQLException e) {

                    System.out.println("Didn't get addressId: " + e.getMessage());

                }

                String updateAddress = "UPDATE customer SET addressId = ? WHERE customerId = ?;";
                SQLQuery.setPreparedStatement(connection,updateAddress);
                ps = SQLQuery.getPreparedStatement();

                try {

                    ps.setInt(1, addressIdHolder);
                    ps.setInt(2, customerId);
                    ps.executeUpdate();

                } catch (SQLException e) {

                    System.out.println("Add new address: " + e.getMessage());

                }
            }
        } catch (SQLException e) {

            System.out.println("3. " + e.getMessage());

        }

        String customerUpdateSameAddress = "UPDATE customer SET customerName = ? WHERE customerId = ?;";
        SQLQuery.setPreparedStatement(connection, customerUpdateSameAddress);
        ps = SQLQuery.getPreparedStatement();

        try {

            ps.setString(1, customerName);
            ps.setInt(2, customerId);
            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("4. " + e.getMessage());

        }
    }

    public static void deleteCustomer(int customerId, int addressId) {

        ObservableList<Customer> allCustomers = Customer.getAllCustomers();

        Connection connection = DatabaseConnection.getConnection();

        String customerDelete = "DELETE FROM customer WHERE customerId = ?;";
        SQLQuery.setPreparedStatement(connection, customerDelete);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {

            ps.setInt(1, customerId);
            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("5. " + e.getMessage());

        }

        String addressDelete = "DELETE FROM address WHERE addressId = ?";
        SQLQuery.setPreparedStatement(connection, addressDelete);
        ps = SQLQuery.getPreparedStatement();

        for (Customer customer : allCustomers) {

            if (customer.getAddressId() == addressId) {

                break;

            } else {

                try {

                    ps.setInt(1, addressId);
                    ps.executeUpdate();

                } catch (SQLException e) {

                    System.out.println(e.getMessage());
                }
            }
        }
    }
}