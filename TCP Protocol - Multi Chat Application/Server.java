//141180018
//Kürşat CAKAL
//Gazi Üniversitesi Bilgisayar Mühendisliği
//Bilgisayar Ağları Ödev 6
//Teslim Tarihi : 29/04/2019

package com.company;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

public class Server extends Thread {

    private static final int PORT = 6789;
    private ServerSocket socketFollower = null;
    private static ArrayList<PrintWriter> yayinciPW;
    private static ArrayList<String> kullaniciList;
    BufferedReader input;
    PrintWriter output;
    String name;
    public static ArrayList<KullaniciBilgileriClass> lst_authenticatedUsers;



    Server() throws IOException {
        lst_authenticatedUsers = new ArrayList<KullaniciBilgileriClass>();
        socketFollower = new ServerSocket(PORT);
        kullaniciList = new ArrayList<String>();
        yayinciPW = new ArrayList<PrintWriter>();
    }
    public static void addElement(KullaniciBilgileriClass gelen)
    {
        lst_authenticatedUsers.add(gelen);
    }
    public void run() {
        System.out.println("< " + socketFollower.getLocalPort()+" > nolu Server portu dinleniyor.");
        while (true) {
            try {
                Socket clientConnectionSocketTakipci = socketFollower.accept();
                System.out.println("Bağlantı: " + clientConnectionSocketTakipci.getRemoteSocketAddress());
                input = new BufferedReader(new InputStreamReader(clientConnectionSocketTakipci.getInputStream()));
                output = new PrintWriter(clientConnectionSocketTakipci.getOutputStream(), true);
                new Read(input, output).start();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Thread t = new Server();
        t.start();
    }

    public void serverYayınYap(String message, PrintWriter out) {
        for (PrintWriter pw : yayinciPW) {
            if (pw != out) {
                pw.println(message);
            }
        }
    }

    class Read extends Thread {

        String kullaniciIDThread;
        BufferedReader inp;
        PrintWriter out;

        Read(BufferedReader inp, PrintWriter out) {
            this.inp = inp;
            this.out = out;
        }

        public void run() {
            try {
                String s;
                kullaniciIDThread = inp.readLine();
                String password = inp.readLine();



                yayinciPW.add(output);
                kullaniciList.add(kullaniciIDThread);
                System.out.println(kullaniciIDThread + " kullanıcısı oturuma katıldı.");
                serverYayınYap(kullaniciIDThread + " kullanıcısı oturuma katıldı.", out);
                s = inp.readLine().trim();
                while (!s.equals("8ylVQeGptt/GeeyA0X3UoA==^")) {
                    while (s.equals("")) {
                        s = inp.readLine();
                    }
                    if (s.indexOf("~<N!_/_!D>~") != -1 && s.indexOf(" kullanıcısı oturumdan ayrıldı.") != -1 && s.indexOf(":") == -1) {
                        break;
                    }
                    serverYayınYap(s, out);
                    s = inp.readLine().trim();
                }
                System.out.println(kullaniciIDThread + " kullanıcısı oturumdan ayrıldı.");
                serverYayınYap(kullaniciIDThread + " kullanıcısı oturumdan ayrıldı.", out);
                for (int i = 0; i != yayinciPW.size(); i++) {
                    if (yayinciPW.get(i).equals(out)) {
                        yayinciPW.remove(i);
                        kullaniciList.remove(i);
                    }
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
//141180018
//Kürşat CAKAL
//Gazi Üniversitesi Bilgisayar Mühendisliği
//Bilgisayar Ağları Ödev 6
//Teslim Tarihi : 29/04/2019
