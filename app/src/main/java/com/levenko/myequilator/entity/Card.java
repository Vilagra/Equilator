package com.levenko.myequilator.entity;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;

/**
 * Created by Vilagra on 11.01.2017.
 */

public class Card {
    private Character rank;
    private Character suit;
    private String stringOfCard;
    private Drawable picture;

    public Card(Character rank, Character suit) {
        this.rank = rank;
        this.suit = suit;
        stringOfCard = ""+rank+suit;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (rank != null ? !rank.equals(card.rank) : card.rank != null) return false;
        return suit != null ? suit.equals(card.suit) : card.suit == null;

    }

    @Override
    public int hashCode() {
        int result = rank != null ? rank.hashCode() : 0;
        result = 31 * result + (suit != null ? suit.hashCode() : 0);
        return result;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Context ctx) {
        try {
            picture=Drawable.createFromStream(ctx.getAssets().open(getStringOfCard()+".png"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Character getRank() {
        return rank;
    }

    public Character getSuit() {
        return suit;
    }

    public String getStringOfCard(){
        return stringOfCard;
    }

    @Override
    public String toString() {
        return "Card{" +
                "rank=" + rank +
                ", suit=" + suit +
                '}';
    }


}
