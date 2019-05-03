//141180018
//Kürşat CAKAL
//Gazi Üniversitesi Bilgisayar Mühendisliği
//Bilgisayar Ağları Ödev 6
//Teslim Tarihi : 29/04/2019

package com.company;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.*;

public class Client extends Thread {

    final JTextArea konusmalarAlani = new JTextArea();
    final JTextField mesajlasmaAlani = new JTextField();

    //Kayıt islemleri
    String userListFileName;
    //Kayıt islemleri
    BufferedReader input;
    BufferedReader br;
    BufferedReader brClient;
    BufferedReader brUserChecker;
    BufferedWriter bwClient;
    BufferedReader onlyForRefresh;
    BufferedReader brMessageDB;
    BufferedReader brMessagesFromDB;
    BufferedReader brUserMessagesChecker;
    BufferedWriter bwMessageDB;
    BufferedReader brUserStateChecker;
    BufferedWriter brUserStateWriter;


    DefaultTreeModel modelTree;
    DefaultMutableTreeNode rootTree;

    KullaniciBilgileriClass tempUser;
    String serverName;
    int PORT;
    String userAddName;
    String userAddPassword;
    PrintWriter output;
    Socket server;
    JTree userListTree;

    void dosyaKontrolu(String filename) { // bu olmadan IO işlemleri sıkıntıya giriyor.
        File f = new File(filename);
        if (!f.exists()) {
            try {
                f.createNewFile();
            }
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
            }
        }
    }

    public Client() throws Exception
    {

        userListFileName="userListTempDB.txt";
        String filenameMessageDB="userMessagesDB.txt";
        dosyaKontrolu(userListFileName);

        brClient = new BufferedReader(new FileReader(userListFileName));
        bwClient = new BufferedWriter(new FileWriter(userListFileName, true));
        brMessageDB=new BufferedReader(new FileReader(filenameMessageDB));
        Font font = new Font("Arial", Font.BOLD, 14);
        this.serverName = "127.0.0.1";
        this.PORT = 6789;
        this.userAddName = "";



        JFrame clientFrame = new JFrame("TCP Chat Uygulaması Client Arayüzü - HAZIRLAYAN: 141180018");
        clientFrame.getContentPane().setLayout(null);
        clientFrame.setSize(1300, 430);
        clientFrame.setResizable(false);
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //Burada her kullanıcı çıktığında kendi ismini listeden silecek.
                try{
                    String snClose, spClose;

                    while ((snClose = brClient.readLine()) != null)
                    {
                        spClose = brClient.readLine();
                        if (snClose.equals(userAddName)) {
                            snClose=snClose.replaceAll(userAddName,"-");
                        }
                    }
                }
                catch (IOException ex)
                {
                    JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
                }
            }
        });


        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Genel Sohbet Odası");
        DefaultMutableTreeNode ailem = new DefaultMutableTreeNode("Ailem");
        DefaultMutableTreeNode arkadaslarım = new DefaultMutableTreeNode("Arkadaşlarım");
        DefaultMutableTreeNode diger = new DefaultMutableTreeNode("Diğer");

        Icon user=new ImageIcon("src/user.png");
        Icon room=new ImageIcon("src/room.png");

        UIManager.put("Tree.closedIcon",user);
        UIManager.put("Tree.openIcon",room);
        UIManager.put("Tree.leafIcon",user);

        userListTree = new JTree(root);
        modelTree = (DefaultTreeModel) userListTree.getModel();
        rootTree= (DefaultMutableTreeNode) modelTree.getRoot();
        rootTree.add(ailem);
        rootTree.add(arkadaslarım);
        rootTree.add(diger);
        userListTree.expandPath(new TreePath(root.getPath()));

        userListTree.setShowsRootHandles(true);
        userListTree.setBounds(820,10,230,337);
        JScrollPane scrollUserList = new JScrollPane(userListTree);
        scrollUserList.setBounds(820,10,230,324);

        konusmalarAlani.setBounds(25, 220, 700, 263); //GEREKSIZ
        konusmalarAlani.setEditable(false);
        konusmalarAlani.setFont(font);
        konusmalarAlani.setMargin(new Insets(6, 6, 6, 6));
        konusmalarAlani.setWrapStyleWord(true);

        JScrollPane scrollKonusmalarAlani = new JScrollPane(konusmalarAlani);
        scrollKonusmalarAlani.setBounds(175, 70, 600, 263);
        mesajlasmaAlani.setBounds(175, 350, 500,40);
        mesajlasmaAlani.setFont(font);
        final JButton btnGonder = new JButton("Gönder");
        btnGonder.setBackground(java.awt.Color.GREEN);
        btnGonder.setBounds(675, 350, 100,40);
        final JTextField txtKullaniciID = new JTextField("ID");
        final JTextField txtKullaniciPW = new JPasswordField("PW");
        final JTextField jtfAddr = new JTextField("127.0.0.1");
        final JButton btnSunucuyaBaglan = new JButton("Bağlan");
        final JButton btnSearch =new JButton("Search");
        JButton btnAddUserToSpesificNode=new JButton("Kullanici Taşı");

        btnAddUserToSpesificNode.setBackground(Color.GREEN);
        JComboBox userListDynamic=new JComboBox();
        btnAddUserToSpesificNode.setBounds(1050,10,150,30);
        userListDynamic.setBounds(1050,50,150,30);

        userListDynamic.setSelectedItem("Seçiniz.");

        JLabel lblID=new JLabel("Kullanıcı ID:");
        JLabel lblPW=new JLabel("Kullanıcı PW:");
        btnSearch.setBackground(java.awt.Color.GREEN);
        btnSearch.setBounds(675,10,100,50);
        final JTextField txtSearch= new JTextField("Aranacak Kelime Giriniz..");
        txtSearch.setBounds(175,10,500,50);
        btnSunucuyaBaglan.setBackground(java.awt.Color.GREEN);
        final JButton btnClearUserList=new JButton("UserList DB Temizle");
        JButton btnClearUserMessagesDB=new JButton("Mesaj DB Temizle");
        btnClearUserMessagesDB.setBackground(Color.GREEN);
        btnClearUserList.setBackground(java.awt.Color.GREEN);
        final JButton btnRefreshtList=new JButton("Listeyi Yenile");
        btnRefreshtList.setBackground(java.awt.Color.GREEN);
        btnRefreshtList.setBounds(820,350,230,40);
        JButton btnKullaniciGecmisMesajları=new JButton("K. Gecmis Mesajlar");
        btnKullaniciGecmisMesajları.setBounds(1050,100,150,30);

        lblID.setBounds(15, 10, 155, 40);
        txtKullaniciID.setBounds(15, 50, 155, 40);
        lblPW.setBounds(15, 90, 155, 40);
        txtKullaniciPW.setBounds(15, 140, 155, 40);
        btnSunucuyaBaglan.setBounds(15, 190, 155, 40);
        btnClearUserMessagesDB.setBounds(15,240,155,40);
        btnClearUserList.setBounds(15,290,155,40);


        btnKullaniciGecmisMesajları.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(userListDynamic.getSelectedItem()!=null)
                {
                    GecmisMesajListeleClass mesajlarInit=new GecmisMesajListeleClass(userListDynamic.getSelectedItem().toString(),checkUserMessages(userListDynamic.getSelectedItem().toString()));
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"Lütfen mesaj listeleme islemi için combobox'tan kullanici seciniz.");
                }
            }
        });
        btnAddUserToSpesificNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kullanıcıGrupListesiDuzenle(userListDynamic.getSelectedItem().toString());
            }
        });
        mesajlasmaAlani.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    veriTabanınaMesajEkle(mesajlasmaAlani.getText(),userAddName); //kullanıcı bazlı kayıt tutma
                    mesajGonderici();
                    mesajlasmaAlani.setText("");
                    konusmalarAlani.setCaretPosition(konusmalarAlani.getDocument().getLength());
                }
            }
        });

        btnGonder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                mesajGonderici();
            }
        });
        btnClearUserMessagesDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    int i=JOptionPane.showConfirmDialog(null, "Bütün Konuşma veriler silinecek eminmisiniz ?");
                    System.out.println(i);
                    if(i==0)
                    {
                        PrintWriter dosyaTemizlemePW = new PrintWriter("userMessagesDB.txt");
                        dosyaTemizlemePW.print("");
                        dosyaTemizlemePW.close();
                        JOptionPane.showMessageDialog(null,"Veriler Silindi.");
                    }


                }
                catch (IOException ex)
                {
                    JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
                }

            }
        });
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlighForSearchAction(konusmalarAlani,txtSearch.getText());
            }
        });
        btnRefreshtList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try{
                    onlyForRefresh = new BufferedReader(new FileReader(userListFileName));
                    String readlineUserIterator;

                    while ((readlineUserIterator = onlyForRefresh.readLine()) != null)
                    {
                        //bütün elemanlar silinip tekrar eklenecek. Çünkü refresh yapınca DB'nin boşaltıldığını yakalamıyor.
                        //jtree add yap burada

                        //modelTree.insertNodeInto(new DefaultMutableTreeNode(readlineUserIterator.split("/splitter/",3)[0].toString()),rootTree,rootTree.getChildCount());
                        userListDynamic.addItem(readlineUserIterator.split("/splitter/",3)[0].toString());
                    }
                    userListDynamic.showPopup();
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
                }
            }
        });
        btnClearUserList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    int i=JOptionPane.showConfirmDialog(null, "Bütün Kullanıcı Profil verileri silinecek eminmisiniz ?");
                    System.out.println(i);
                    if(i==0)
                    {
                        PrintWriter dosyaTemizlemePW = new PrintWriter(userListFileName);
                        dosyaTemizlemePW.print("");
                        dosyaTemizlemePW.close();
                        JOptionPane.showMessageDialog(null,"Veriler Silindi.");
                    }
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
                }
            }
        });
        btnSunucuyaBaglan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {

                    userAddName = txtKullaniciID.getText();
                    userAddPassword = txtKullaniciPW.getText();
                    serverName = jtfAddr.getText();
                    //userListTempDB.txt icin yeni node ve user ekleme islemleri
                    if (!checkUser(userAddName,userAddPassword))
                    {
                        JOptionPane.showMessageDialog(clientFrame, "Yeni Kullanıcı Eklenecek.. Eklendi...");
                        kullaniciEkle(userAddName.toString(),userAddPassword.toString());
                    }

                    //if (checkUser(userAddName,userAddPassword)) //böyle yaparsan bir kat daha fazla sorgu zamanı maliyeti eklenir.
                    else
                    {
                        JOptionPane.showMessageDialog(clientFrame, "Bu kullanıcı adı veritabanında kayıtlıdır.\n"+"<<"+userAddName+">> ismi ile Giriş Yapılacak..");
                        //kullaniciStateDegistirme(userAddName,userAddPassword,"1");
        //#bookmark
                    } // bu kontrol addUser içinde de yapılabilirdi fakat o halde iç içe döngü maliyeti daha fazla olacaktı.

                    //userListTempDB.txt icin yeni node ve user ekleme islemleri

                    konusmalarAlani.append("Bağlanılıyor.." + serverName + " port " + PORT + "...");
                    server = new Socket(serverName, PORT);
                    konusmalarAlani.append("\nBağlanılıyor.." + server.getRemoteSocketAddress());
                    konusmalarAlani.append("\nKullanıcı Giriş Yapıyor..\n");
                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    output = new PrintWriter(server.getOutputStream(), true);
                    br = new BufferedReader(new InputStreamReader(System.in));
                    output.println(userAddName);
                    output.println(userAddPassword);



                    konusmalarAlani.append("\n");
                    veriTabanındanMesajGetir();//GEÇMİŞ MESAJLAR YÜKLENECEK
                    konusmalarAlani.append("******************************");
                    konusmalarAlani.append("\nYENİ KULLANICI OLARAK OTURUMA GİRDİNİZ " + "SAYIN  <"+ userAddName +">  HOŞ GELDİNİZ"+"\n"); //yeni gelen kullanıcının durumu belirtilecek
                    konusmalarAlani.append("******************************");

                    clientFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                            try
                            {
                                //kullaniciStateDegistirme(userAddName,userAddPassword,"0");
                                output.println("~<N!_/_!D>~" + userAddName + " kullanıcısı oturumdan ayrıldı."); //list exit
                                System.exit(0);

                            } catch (Exception ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    Thread r = new Read();
                    r.start();
                    lblID.setEnabled(false);
                    lblPW.setEnabled(false);
                    txtKullaniciID.setEnabled(false);
                    txtKullaniciPW.setEnabled(false);
                    btnSunucuyaBaglan.setEnabled(false);
                    jtfAddr.setEnabled(false);

                    clientFrame.add(btnGonder);
                    clientFrame.add(mesajlasmaAlani);
                    clientFrame.revalidate();
                    clientFrame.repaint();
                } catch (Exception ex) {
                    konusmalarAlani.append("\nServer'a bağlanılamadı.");
                    JOptionPane.showMessageDialog(clientFrame, ex.getMessage());
                }
            }
        });

        clientFrame.add(lblID);
        clientFrame.add(lblPW);
        clientFrame.add(btnGonder);
        clientFrame.add(btnSearch);
        clientFrame.add(txtSearch);
        clientFrame.add(btnClearUserList);
        clientFrame.add(btnClearUserMessagesDB);
        clientFrame.add(btnSunucuyaBaglan);
        clientFrame.add(scrollKonusmalarAlani);
        clientFrame.add(txtKullaniciID);
        clientFrame.add(txtKullaniciPW);
        clientFrame.add(jtfAddr);
        clientFrame.add(scrollUserList);
        clientFrame.add(btnRefreshtList);
        clientFrame.add(userListDynamic);
        clientFrame.add(btnAddUserToSpesificNode);
        clientFrame.add(btnKullaniciGecmisMesajları);

        clientFrame.setVisible(true);
        //clientFrame.add(userListTree); x HATA ALIRSIN BURADA EKLENMEZ

    }

    public void mesajGonderici() {
        try {
            String message = mesajlasmaAlani.getText().trim();
            if (message.equals("")) {
                return;
            }
            konusmalarAlani.append("\n" + userAddName + " : " + message);
            if (message.length() < 30)
            {

                output.println(userAddName + " : " + message);
            } else {
                output.println("~<N!_/_!D>~" + userAddName + " : " + message);
            }
            mesajlasmaAlani.requestFocus();
            mesajlasmaAlani.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }
    public String veriTabanınaMesajEkle(String message, String user)
    {
        try
        {

            String filenameMessageDB="userMessagesDB.txt";
            bwMessageDB=new BufferedWriter(new FileWriter(filenameMessageDB,true));
            String sn, sp;

            while ((sn = brMessageDB.readLine()) != null) {
                sp = brMessageDB.readLine();
            }
            String temp=userAddName+" : "+message;//.replace(" ","");
            bwMessageDB.write(temp);
            bwMessageDB.newLine();
            bwMessageDB.close();
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
        }
        return "added";
    }
    public void veriTabanındanMesajGetir()
    {
        try
        {
            String filenameMessageDB="userMessagesDB.txt";
            bwMessageDB=new BufferedWriter(new FileWriter(filenameMessageDB,true));
            String sn;

            while ((sn = brMessageDB.readLine()) != null) {
                konusmalarAlani.append(sn);
                konusmalarAlani.append("\n");
            }

        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
        }
    }
    public void kullanıcıGrupListesiDuzenle(String addItem)
    {

        DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode) userListTree.getLastSelectedPathComponent();
        modelTree.insertNodeInto(new DefaultMutableTreeNode(addItem.toString()),selectedNode,selectedNode.getChildCount());
    }

    public static void main(String[] args) throws Exception
    {
        Client client = new Client();
    }

    class Read extends Thread {

        public void run() {
            String s;
            String message;
            while (true) {
                try {
                    s = input.readLine();
                    if (s.indexOf("~<N!_/_!D>~") == -1) {
                        konusmalarAlani.append("\n" + s);
                    } else {
                        konusmalarAlani.append("\n" + s.substring("~<N!_/_!D>~".length()));
                    }
                } catch (Exception ex) {
//                    System.err.println(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Bağlantı uç sistem tarafından kapatıldı.");
                    System.exit(0);
                }
            }
        }
    }
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
    {
        public MyHighlightPainter(java.awt.Color color){
            super(color);
            //super(com.sun.prism.paint.Color(color));
            //super(color);
        }
    }

    Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.pink);

    public void highlighForSearchAction(JTextComponent textCom,String pattern)
    {
        removeHighlight(textCom);
        try{

            Highlighter hilite= textCom.getHighlighter();
            Document doc=textCom.getDocument();
            String text=doc.getText(0,doc.getLength());
            System.out.println(text);
            int pos=0;
            //JOptionPane.showMessageDialog(null,konusmalarAlani.getText());
            while((pos=text.toLowerCase().indexOf(pattern.toLowerCase(),pos))>=0)
            {
                System.out.println("temel vuruş highlite");
                hilite.addHighlight(pos,pos+pattern.length(),myHighlightPainter);
                pos+=pattern.length();
            }

        }
        catch(Exception e){}
    }

    public void removeHighlight(JTextComponent textCom)
    {
        Highlighter hilite= textCom.getHighlighter();
        Highlighter.Highlight[] silinecek=hilite.getHighlights();
        for(int k=0;k<silinecek.length;k++)
        {
            if(silinecek[k].getPainter() instanceof MyHighlightPainter)
            {
                hilite.removeHighlight(silinecek[k]);
            }
        }
    }
    public void kullaniciStateDegistirme(String username, String password, String state)
    {
        try
        {
            brUserStateChecker= new BufferedReader(new FileReader("userListTempDB.txt"));
            brUserStateWriter=new BufferedWriter(new FileWriter("userListTempDB.txt",true));
            String kullaniciStateKontrolcu;
            String kullaniciStateYazici;
            String[] stateArray=null;
            String stateUser="";
            while ((kullaniciStateKontrolcu = brUserStateChecker.readLine()) != null)
            {
                //equals(username+"/splitter/"+password)
                //stateArray=kullaniciStateKontrolcu.split("splitter",3);
                //if (stateArray[2]=="offline") {
                stateArray=kullaniciStateKontrolcu.split("/splitter/",3);
                stateUser=kullaniciStateKontrolcu.substring(kullaniciStateKontrolcu.length()-1,kullaniciStateKontrolcu.length());
                if(stateArray[0].equals(username) && stateArray[1].equals(password))
                {
                    //brUserStateWriter.write(state);
                    //brUserStateWriter.close();
                }

            }

        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null,"Kullanıcı State Güncellemesi Yapıldı");
        }
    }
    public boolean checkUser(String username, String password)
    {
        try
        {
            brUserChecker= new BufferedReader(new FileReader("userListTempDB.txt"));
            String kullaniciKontrolcu;
            String[] tempArray=null;
            while ((kullaniciKontrolcu = brUserChecker.readLine()) != null) {
                tempArray=kullaniciKontrolcu.split("/splitter/",3);
                if (tempArray[0].equals(username) && tempArray[1].equals(password)) {
                    return true;
                }
            }
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
        }
        return false;
    }
    public ArrayList checkUserMessages(String username)
    {
        ArrayList gonderilecekMesajlar=new ArrayList();
        try
        {
            brUserMessagesChecker= new BufferedReader(new FileReader("userMessagesDB.txt"));
            String kullaniciKontrolcu;
            gonderilecekMesajlar=new ArrayList();
            while ((kullaniciKontrolcu = brUserMessagesChecker.readLine()) != null)
            {
                //String[] tempArrayForMesaj=kullaniciKontrolcu.split("/splitter/",2);
                String[] tempArrayForMesaj=kullaniciKontrolcu.split(":",2);
                if (tempArrayForMesaj[0].trim().equals(username)) { //splitter mantığı kullan ilk ":" görene kadarki kısmı al onla karşılaştır.
                    //return true;//not böyle bir kullanımda kullanıcılara ait mesajlar sadece "username" odaklı getirilir yani aynı username'e sahip farklı parolalara sahip olarak kayıt edilen kullanıcıların mesajları aynı kişiye aitmiş gibi listelenir. Zaman kaybı olmaması açısından şimdilik sadece kullanıcı ismi merkezli listeleme yapılmıştır.
                    //gonderilecekMesajlar.add(tempArrayForMesaj[1].split(":",2)[1].toString());
                    gonderilecekMesajlar.add(tempArrayForMesaj[1].toString());
                }
            }
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
        }
        return gonderilecekMesajlar;
    }
    public void kullaniciEkle(String username,String password)
    {
        try
        {
            String sn;
            while ((sn = brClient.readLine()) != null)
            {
                if (sn.equals(username+"/splitter/"+password))
                {
                    JOptionPane.showMessageDialog(null,"Bu kullanıcı adı geçersizdir.");
                }
            }

            bwClient.write(username+"/splitter/"+password+"/splitter/"+"1");
            bwClient.newLine();
            bwClient.close();
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null,"Beklenmeyen IO Hatası oluştu lütfen talimatlara uyunuz.");
        }
    }
}
//141180018
//Kürşat CAKAL
//Gazi Üniversitesi Bilgisayar Mühendisliği
//Bilgisayar Ağları Ödev 6
//Teslim Tarihi : 29/04/2019

