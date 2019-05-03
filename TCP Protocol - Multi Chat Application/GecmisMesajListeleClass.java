//141180018
//Kürşat CAKAL
//Gazi Üniversitesi Bilgisayar Mühendisliği
//Bilgisayar Ağları Ödev 6
//Teslim Tarihi : 29/04/2019

package com.company;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;

public class GecmisMesajListeleClass {
    public GecmisMesajListeleClass(String gelenKullanici, ArrayList gelenMesajlar)
    {
        JFrame jMesajFrame = new JFrame("Geçmiş Mesajlar: ("+gelenKullanici+")");
        jMesajFrame.getContentPane().setLayout(null);
        jMesajFrame.setSize(500, 430);
        jMesajFrame.setResizable(false);
        jMesajFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea jMesajlariGor = new JTextArea();
        jMesajlariGor.setBounds(40,40,420,300);

        jMesajFrame.add(jMesajlariGor);
        jMesajFrame.setVisible(true);
        //String[] mesajTemp=null;
        //mesajTemp=gelenMesajlar.toArray();
        Iterator ik=gelenMesajlar.iterator();
        if(gelenMesajlar.size()!=0)
        {
            while (ik.hasNext()) {
                jMesajlariGor.append(gelenKullanici+" "+ik.next());
                jMesajlariGor.append("\n");
            }

        }
        JOptionPane.showMessageDialog(null,gelenKullanici+" 'ya ait mesajlar listelenecektir."+gelenMesajlar.toString());
    }
    public static void main(String args[])
    {
        //GecmisMesajListeleClass objectInıt=new GecmisMesajListeleClass("test");
    }

}
//141180018
//Kürşat CAKAL
//Gazi Üniversitesi Bilgisayar Mühendisliği
//Bilgisayar Ağları Ödev 6
//Teslim Tarihi : 29/04/2019
