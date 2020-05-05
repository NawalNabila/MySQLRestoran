package id.dana.nabila;

import java.io.DataInputStream;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Scanner;

public class Server {
    private ServerSocket ss;
    private DataInputStream dis;

    //Start connection to socket server
    public void startConnection(int port) {
        try {
            ss = new ServerSocket(port);
            Socket s = ss.accept();
            dis = new DataInputStream(s.getInputStream());
            System.out.println("Server active");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Read order from client -> save to DB
    public void readOrder() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resto?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "jljlkl890090");

            String insert = "INSERT INTO orders (id_customer, food, transaksi, point) VALUES (?, ?, ?, ?)";
            PreparedStatement statement1 = conn.prepareStatement(insert);
            try {
                    //Get data from client
                    String msg = (String) dis.readUTF();
                    String[] data = msg.split("[|]");
                    int id = Integer.parseInt(data[0]);
                    String food = data[1];
                    int transaksi = Integer.parseInt(data[2]);
                    int jlh = Integer.parseInt(data[3]);

                    //total transaksi * quantity
                    int total = transaksi*jlh;
                    int point = 10;

                    //Save data to DB
                    statement1.setInt(1,  id);
                    statement1.setString(2, food);
                    statement1.setInt(3, total);
                    statement1.setInt(4, point);

                    int rowInserted = statement1.executeUpdate();
                    if (rowInserted > 0) {
                        System.out.println("Order was accepted!");
                    }

            } catch (Exception e) {
                System.out.println(e);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Print struct order customer by ID Customer
    public void printStruct(int id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resto?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "jljlkl890090");

            String select1 = "SELECT * FROM orders WHERE id_customer=" + id;
            String select2 = "SELECT sum(transaksi) FROM orders WHERE id_customer=" + id;
            String select3 = "SELECT name FROM customer WHERE id=" + id;

            Statement stmt = conn.createStatement();
            ResultSet rs;

            rs = stmt.executeQuery(select3);
            rs.next();
            String name = rs.getString(1);
            System.out.println("------------------------------------");
            System.out.println("Customer: " + name);
            System.out.println("------------------------------------");

            rs = stmt.executeQuery(select1);
            while(rs.next())
                System.out.println(rs.getString(2)+"  "+rs.getInt(3));
            System.out.println("------------------------------------");

            rs = stmt.executeQuery(select2);
            rs.next();
            int total = rs.getInt(1);
            System.out.println("Total Transaksi: " + total);
            System.out.println("------------------------------------");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Send Recapitulation file to FTP
    public void sendRecapitulation() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resto?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "jljlkl890090");
            //Read All of Data from DB
            String select = "SELECT c.id, c.name, c.age, sum(o.transaksi), sum(o.point) FROM customer c, orders o WHERE c.id = o.id_customer group by c.id order by c.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(select);

            System.out.println("| ID | Name | Age | Transaksi | Point |");
            String dataRecapt = "";
            while (rs.next()) {
                System.out.println("| " + rs.getInt(1) + " | " + rs.getString(2)
                        + " | " + rs.getInt(3) + " | " + rs.getInt(4) + " | " + rs.getInt(5) + " |");
                dataRecapt = "| " + rs.getInt(1) + " | " + rs.getString(2)
                        + " | " + rs.getInt(3) + " | " + rs.getInt(4) + " | " + rs.getInt(5) + " |\n";
            }
            //Write data to a file
            FileWriter writer = new FileWriter("/home/nabilla/Documents/recapitulation.txt");
            writer.write("| ID | Name | Age | Transaksi | Point |\n");
            writer.write(dataRecapt);
            writer.close();

            //Upload file from local to server
            FTPUploader ftpUploader = new FTPUploader("ftp.myth.co.id", " ftpuser@myth.co.id", "P@ssw0rd12345");
            ftpUploader.uploadFile("/home/nabilla/Documents/recapitulation.txt", "recapitulation.txt", "/");
            System.out.println("Successfully Uploaded Recapitulation");

            //close connection FTP
            ftpUploader.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Stop conncetion to Socket Server
    public void stopConnection() {
        try {
            ss.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startConnection(6666);

        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("\n1.Accept Order");
            System.out.println("2.Print Struct");
            System.out.println("3.Send Recapt. to FTP");
            System.out.println("4.Exit");

            System.out.print("Choice: ");
            int choice = scan.nextInt();
            scan.nextLine();

            if (choice == 1) {
                server.readOrder();
            } else if (choice == 2) {
                System.out.print("ID Customer: ");
                int id = scan.nextInt();
                scan.nextLine();

                server.printStruct(id);
            } else if (choice == 3) {
                server.sendRecapitulation();
            }  else {
                server.stopConnection();
                break;
            }
        }

        scan.close();

    }
}
