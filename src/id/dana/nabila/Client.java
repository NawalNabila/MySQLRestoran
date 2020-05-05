package id.dana.nabila;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;
    private DataOutputStream dout;

    //Start connection to server
    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            dout = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Connected to Server");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Send data from client to server
    public void sendMessage(String msg) {
        try {
            dout.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Stop connection to server
    public void stopConnection() {
        try {
            dout.flush();
            dout.close();
            clientSocket.close();
            System.out.println("Client Disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Register new customer
    public void register(String customer) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resto?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "jljlkl890090");

            //Query to save new customer to DB
            String insert = "INSERT INTO customer (id, name, age) VALUES (?, ?, ?)";

            PreparedStatement statement1 = conn.prepareStatement(insert);

            String[] data = customer.split("[|]");
            int id = Integer.parseInt(data[0]);
            String name = data[1];
            int age = Integer.parseInt(data[2]);

            statement1.setInt(1,  id);
            statement1.setString(2, name);
            statement1.setInt(3, age);

            int rowInserted = statement1.executeUpdate();
            if (rowInserted > 0) {
                System.out.println("Register Success!");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Check point customer by ID Customer
    public void checkPoint(int id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resto?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "jljlkl890090");

            //GET total point from DB
            String select = "SELECT sum(point) FROM orders WHERE id_customer =" + id;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(select);
            rs.next();
            int totalPoint = rs.getInt(1);

            System.out.println("Your Point: "+ totalPoint);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

        Scanner scan = new Scanner(System.in);

        client.startConnection("localhost", 6666);


        while (true) {
            System.out.println("\n1.Order");
            System.out.println("2.Check Point");
            System.out.println("3.Register");
            System.out.println("4.EXIT");

            System.out.print("Choice: ");
            int choice = scan.nextInt();
            scan.nextLine();

            if (choice == 1) {
                System.out.print("ID|Menu|Price|Quantity: ");
                String data = scan.nextLine();

                client.sendMessage(data);
            } else if (choice == 2) {
                System.out.print("Type ID: ");
                int id = scan.nextInt();
                scan.nextLine();

                client.checkPoint(id);
            } else if (choice == 3) {
                System.out.print("ID|Name|age: ");
                String data = scan.nextLine();

                client.register(data);
            } else {
                client.stopConnection();
                break;
            }

        }

        scan.close();
    }
}
