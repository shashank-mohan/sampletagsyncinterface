package com.tagbox.samplegatewayinterface;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class TempBreach implements Parcelable,Comparable {

    private float maxTemp;
    private float minTemp;
    private float currTemp;

    public TempBreach(){

    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getCurrTemp() {
        return currTemp;
    }

    public void setCurrTemp(float currTemp) {
        this.currTemp = currTemp;
    }

    public TempBreach(Parcel source) {
       maxTemp = source.readFloat();
       minTemp = source.readFloat();
       currTemp = source.readFloat();
    }

    public int describeContents() {
        return this.hashCode();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(maxTemp);
        dest.writeFloat(minTemp);
        dest.writeFloat(currTemp);
    }

    public static final Creator CREATOR
            = new Creator() {
        public TempBreach createFromParcel(Parcel in) {
            return new TempBreach(in);
        }

        public TempBreach[] newArray(int size) {
            return new TempBreach[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}

