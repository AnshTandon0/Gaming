package com.gaming.community.flexster.chat;

import com.gaming.community.flexster.model.ModelGameList;

import java.util.ArrayList;

public class Constantdatas
{

    public static ArrayList<ModelGameList> getroundlist() {
        ArrayList<ModelGameList> arrayList = new ArrayList();
        ModelGameList md1=new ModelGameList();
        md1.setName("1");
        arrayList.add(md1);

        ModelGameList md2=new ModelGameList();
        md2.setName("2");
        arrayList.add(md2);

        ModelGameList md3=new ModelGameList();
        md3.setName("3");
        arrayList.add(md3);

        ModelGameList md4=new ModelGameList();
        md4.setName("4");
        arrayList.add(md4);

        ModelGameList md5=new ModelGameList();
        md5.setName("5");
        arrayList.add(md5);


        ModelGameList md6=new ModelGameList();
        md6.setName("6");
        arrayList.add(md6);


        ModelGameList md7=new ModelGameList();
        md7.setName("7");
        arrayList.add(md7);

        ModelGameList md8=new ModelGameList();
        md8.setName("8");
        arrayList.add(md8);

        ModelGameList md9=new ModelGameList();
        md9.setName("9");
        arrayList.add(md9);

        ModelGameList md10=new ModelGameList();
        md10.setName("10");
        arrayList.add(md10);

        return arrayList;
    }

    public static ArrayList<ModelGameList> getvsdatas()
    {
        ArrayList<ModelGameList> arrayList = new ArrayList();
        ModelGameList md1=new ModelGameList();
        md1.setName("1 vs 1");
        arrayList.add(md1);

        /*ModelGameList md2=new ModelGameList();
        md2.setName("2 vs 2");
        arrayList.add(md2);*/

        /*ModelGameList md3=new ModelGameList();
        md3.setName("3 vs 3");
        arrayList.add(md3);*/

       /* ModelGameList md4=new ModelGameList();
        md4.setName("4 vs 4");
        arrayList.add(md4);*/

        ModelGameList md5=new ModelGameList();
        md5.setName("Club vs Club");
        arrayList.add(md5);

        return arrayList;
    }
}
