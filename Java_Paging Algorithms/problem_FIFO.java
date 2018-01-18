package com.company;

import java.util.Arrays;

//referenas dizisini alırken lütfen eksili sayılar girmeyiniz desin
public class problem_FIFO{

    int cerceve_sayisi;
    int[] referansDizisi;
    int[] cerceveDizisi;
    int sayfa_hatasi=0;
    int cerceve_doluluk=0;
    int[][] cercevenin_akis_diyagramı;
    problem_FIFO(int gelenCerceve,int[] gelenReferansDizi)
    {
        cerceve_sayisi=gelenCerceve;
        referansDizisi=gelenReferansDizi;
        cercevenin_akis_diyagramı=new int[gelenReferansDizi.length][gelenCerceve];
    }

    public int fifo_method()
    {
        cerceveDizisi=new int[cerceve_sayisi];
        cerceveDizisi=diziBaslat(cerceveDizisi);
        int cerceve_dolana_kadar=0;//cerceve(CERCEVESAYISI-1).indisin yerleştirilmesinden sonra dolacak.
        for (int i=0;i<referansDizisi.length;i++)
        {
            if(Deger_Kontrol(referansDizisi[i]))//değer varsa hata vermeden geç yada bir şeyler yazdır
            {
                    for(int g=0;g<cerceve_sayisi;g++)
                    {
                        cercevenin_akis_diyagramı[i][g]=cerceveDizisi[g];
                    }
            }
            else//değer yok ise hata değerini arttır. FIFO kuralına göre elemanı cerceve dizisine yerleştir.
            {
                sayfa_hatasi++;
                int cerceve_temp_index=0;

                if(cerceve_dolana_kadar<cerceve_sayisi)//-1 bulduğun ilk yere sayıyı yerleştir.
                {
                    cerceveDizisi[cerceve_dolana_kadar]=referansDizisi[i];
                    cerceve_dolana_kadar++;
                    for(int g=0;g<cerceve_sayisi;g++)
                    {
                        cercevenin_akis_diyagramı[i][g]=cerceveDizisi[g];
                    }
                }
                else
                //eğer olurda boş yer bulamaz isem.
                // ilk giren değer yani en üsttekini sil
                // indislerin hepsini 1 azalt
                // yani bütün değerleri bir yukarı çek
                // ardından en alta yeni gelen değeri yerleştir
                {
                    for (int c=0;c<cerceve_sayisi;c++)
                    {
                        if(c!=cerceve_sayisi-1)
                            cerceveDizisi[c]=cerceveDizisi[c+1];
                    }
                    cerceveDizisi[cerceve_sayisi-1]=referansDizisi[i];
                    for(int g=0;g<cerceve_sayisi;g++)
                    {
                        cercevenin_akis_diyagramı[i][g]=cerceveDizisi[g];
                    }
                }
            }

        }
        System.out.println("Cercevelerin akış durumu:");
        for(int g=0;g<cerceve_sayisi;g++)
        {
            for(int h=0;h<referansDizisi.length;h++)
            {
                System.out.print("   "+cercevenin_akis_diyagramı[h][g]+ "    ");
            }
                System.out.println();
        }


        //System.out.println(Arrays.deepToString(cercevenin_akis_diyagramı));
        return sayfa_hatasi;
    }

    public boolean Deger_Kontrol(int gelenDeger)
    {
        boolean varmi=false;
        for (int k=0;k<cerceve_sayisi;k++)
        {
            if(cerceveDizisi[k]==gelenDeger)
                varmi=true;
        }
        return varmi;
    }
    public int[] diziBaslat(int[] gelenDizi)//dizimizin - degerler içermediğini varsayarak cerceve dolana kadar ki sürecte "0" degerlerinin güvenliğini alıyoruz
    {

        for(int i=0;i<gelenDizi.length;i++)
        {
            gelenDizi[i]=-1;
        }
        return gelenDizi;
    }
}
