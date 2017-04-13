package com.example;

import java.util.ArrayList;
import java.util.List;

import pet.eq.Equity;
import pet.eq.MEquity;
import pet.eq.Poker;
import pet.eq.Value;
import pet.eq.impl.DrawPoker;
import pet.eq.impl.HEPoker;


/**
 * Created by Vilagra on 12.04.2017.
 */

public class Example {
    public static void main(String[] args) {
        HEPoker poker = new HEPoker(false,false);
        List<String> board = new ArrayList<>();
        List<String[]> cards = new ArrayList<>();
        cards.add(new String[]{"Ac","Ks"});
        cards.add(new String[]{"Jh","8h"});
        MEquity[] mEquities= poker.equity(board,cards,new ArrayList<String>(),0);
        for (MEquity mEquity : mEquities) {
            System.out.println(mEquity.eqs[0].total);
        }
    }
}
