package com.tagbox.samplegatewayinterface;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class HumidityBreach implements Parcelable,Comparable {

    private float maxHumidity;
    private float minHumidity;
    private float currHumidity;

    public HumidityBreach(){
    }

    public HumidityBreach(Parcel source) {
        maxHumidity = source.readFloat();
        minHumidity = source.readFloat();
        currHumidity = source.readFloat();
    }

    public float getMinHumidity() {
        return minHumidity;
    }

    public void setMinHumidity(float minHumidity) {
        this.minHumidity = minHumidity;
    }

    public float getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(float maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public float getCurrHumidity() {
        return currHumidity;
    }

    public void setCurrHumidity(float currHumidity) {
        this.currHumidity = currHumidity;
    }

    public int describeContents() {
        return this.hashCode();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(maxHumidity);
        dest.writeFloat(minHumidity);
        dest.writeFloat(currHumidity);
    }

    public static final Creator CREATOR
            = new Creator() {
        public HumidityBreach createFromParcel(Parcel in) {
            return new HumidityBreach(in);
        }

        public HumidityBreach[] newArray(int size) {
            return new HumidityBreach[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }


}

