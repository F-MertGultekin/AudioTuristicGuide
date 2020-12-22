package com.example.currentplacedetailsonmap;

import android.media.MediaPlayer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Markers {
    private LatLng position;
    private String name;
    private MediaPlayer sound;
    private Marker marker;

    public Markers(LatLng position, String name, MediaPlayer sound,Marker marker) {
        this.position = position;
        this.name = name;
        this.sound = sound;
        this.marker=marker;
    }

    public MediaPlayer getSound() {
        return sound;
    }

    public void setSound(MediaPlayer sound) {
        this.sound = sound;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getPosition() {
        return position;
    }


    public void setPosition(LatLng position) {
        this.position = position;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
