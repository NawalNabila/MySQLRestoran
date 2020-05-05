package id.dana.nabila;

import java.io.*;
import java.util.Scanner;

public class BackOffice {
    private static FTPDownloader ftpDownloader;

    public void connect() throws  Exception{
        ftpDownloader = new FTPDownloader("ftp.myth.co.id", " ftpuser@myth.co.id", "P@ssw0rd12345");
        System.out.println("FTP is connected");
    }

    public void disconnect() {
        ftpDownloader.disconnect();
        System.out.println("FTP is disconnected");
    }

    public void downloadFile() throws FileNotFoundException {
        String fileOnServer = "/recapitulation.txt";
        ftpDownloader.downloadFile(fileOnServer, "/home/nabilla/Downloads"+fileOnServer);
        System.out.println("File Downloaded Successfully");

        File file = new File("/home/nabilla/Downloads/recapitulation.txt");
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            System.out.println(reader.nextLine());
        }
    }

    public static void main(String[] args) throws Exception {
        BackOffice backOffice = new BackOffice();

        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("\n1.Connect to FTP");
            System.out.println("2.Get & Show Data");
            System.out.println("3.Diconnect from FTP");

            System.out.print("Choice: ");
            int choice = scan.nextInt();
            scan.nextLine();

            if (choice == 1) {
                backOffice.connect();
            } else if (choice == 2) {
                backOffice.downloadFile();
            } else if (choice == 3) {
                backOffice.disconnect();
                break;
            }  else {
                break;
            }
        }
        scan.close();
    }

}
