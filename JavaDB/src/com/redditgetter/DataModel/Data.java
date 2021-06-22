package com.redditgetter.DataModel;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.redditgetter.DataModel.children.Children;

import java.util.ArrayList;

public class Data {

    @SerializedName("modhash")
    @Expose
    private String modhash;

    @SerializedName("children")
    @Expose
    private ArrayList <Children> children;

    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    public ArrayList<Children> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Children> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Data{" +
                "modhash='" + modhash + '\'' +
                ", children=" + children +
                '}';
    }
}
