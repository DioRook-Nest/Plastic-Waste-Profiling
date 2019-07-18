package com.spearow.deepblue.Modul;

public class Candidates {
    private Geometry geometry;

    public Geometry getGeometry ()
    {
        return geometry;
    }

    public void setGeometry (Geometry geometry)
    {
        this.geometry = geometry;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [geometry = "+geometry+"]";
    }
}
