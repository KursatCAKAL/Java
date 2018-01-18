package com.company;

import java.util.Arrays;

public class problem_LRU {
    int cerceve_sayisi;
    int[] referansDizisi;
    int[] cerceveDizisi;
    int sayfa_hatasi=0;
    int cerceve_doluluk=0;
    int[][] cercevenin_akis_diyagramı;
    problem_LRU(int gelenCerceve,int[] gelenReferansDizi)
    {
        cerceve_sayisi=gelenCerceve;
        referansDizisi=gelenReferansDizi;
        cercevenin_akis_diyagramı=new int[gelenReferansDizi.length][gelenCerceve];
    }
    public int lru_method(){

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
            else//değer yok ise hata değerini arttır. LRU kuralına göre elemanı cerceve dizisine yerleştir.
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
                //cerceve dizisindeki her bir değer için counter tut
                //bu counter'ı dizinin o an olduğu yerden sonuna kadar git ve sayıyı en uzak nerede bulduysan bu mesafe kadar arttır.
                //daha sonra counter'ı en büyük olanı çıkart.
                //bu çıkardığın değer yerine yeni eklemen gerekeni ekle
                {
                    int en_uzak_degerin_cerceve_indisi;
                    int[] cerceve_optimal_value=new int[cerceve_sayisi];
                    //yeni bir dizi oluşturup bu dizide tutabilirsin. //hatta diğer işlemler sürerken her değer için arkada tarafta thread ile hesaplama yapabilir.
                    for (int j=0;j<cerceve_sayisi;j++)
                    {
                        int sayac=1;
                        //bulunulan yerden sayac degeri kadar geride kullanıldığını buluyoruz.
                        for (int c=i-1;c>=0;c--)//dolaştığın cerceve değeri için gezdiğin rakamlara eşit olmadığı sürece sayacı arttır.son eşit olduğu yeri de işaretle
                        {
                            if(cerceveDizisi[j]!=referansDizisi[c])//bulunmadığı sürece say
                            {
                                sayac++;
                                if(c==0)
                                //eğer olurda değer o indisten başlangıc indise kadar hiç bulunmaz ise dizinin başına kadar o degerden olmadığını ifade et
                                // Yani bunu yapmazsan kaç adım sonra kullanıldığına dair olan bilgi aslında hiç olmayan değer için 0 adım sonra oluyor
                                // Ve alttaki algoritma 0 'ı küçük görüp aslıdna hiç kullanılmayan bir değer rağmen 2 adım sonra kullanılmış bir değeri
                                // En uzak noktada kullanılmış gibi tercih yapılıyor.
                                {
                                    cerceve_optimal_value[j]=sayac;
                                }
                            }
                            else if(cerceveDizisi[j]==referansDizisi[c])//en erken kullanıldıkları yeri diziye ekle
                            {
                                cerceve_optimal_value[j]=sayac;//anlık olarak her yer olmadığında diziyi yenileyecek.
                                c=0;
                            }

                        }

                    }//artık kimin ne kadar uzun süre kullanılmayacağını biliyoruz.

                    int en_uzun_kullanilmayan=0;//en uzun süre kullanılmayanın 0.indiste olduğunu varsaydık yukarıda elde ettiğimiz dizideki en büyük değere sahip olan ile bu indisi değiştireceğiz

                    for(int k=1;k<cerceve_sayisi;k++)
                    {
                        if (cerceve_optimal_value[k]>cerceve_optimal_value[en_uzun_kullanilmayan])
                        {

                            en_uzun_kullanilmayan=k;//cerceve dizisindekilerden en uzun süre kullanılmayanı pinledik.

                        }
                    }//en uzun kullanılmayanı pinledik. Bir sonraki aşamada bunu silip yerine yeni değeri yerleştireceğim.
                    cerceveDizisi[en_uzun_kullanilmayan]=referansDizisi[i];
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
