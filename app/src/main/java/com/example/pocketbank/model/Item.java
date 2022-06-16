package com.example.pocketbank.model;

public class Item
{

    private int itemNameResourceId,itemImageResourceId;

    public Item(int itemNameResourceId , int itemImageResourceId)
    {
        this.itemNameResourceId = itemNameResourceId;
        this.itemImageResourceId = itemImageResourceId;
    }

    public int getItemNameResourceId()
    {
        return itemNameResourceId;
    }

    public void setItemNameResourceId(int itemNameResourceId)
    {
        this.itemNameResourceId = itemNameResourceId;
    }

    public int getItemImageResourceId()
    {
        return itemImageResourceId;
    }

    public void setItemImageResourceId(int itemImageResourceId)
    {
        this.itemImageResourceId = itemImageResourceId;
    }

}
