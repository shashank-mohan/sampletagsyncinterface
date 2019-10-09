package com.tagbox.samplegatewayinterface;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class JerkBreach implements Parcelable,Comparable {

    private int breachCount;

    public JerkBreach(){
    }

    public JerkBreach(Parcel source) {
        breachCount = source.readInt();
    }

    public int getBreachCount() {
        return breachCount;
    }

    public void setBreachCount(int breachCount) {
        this.breachCount = breachCount;
    }

    public int describeContents() {
        return this.hashCode();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(breachCount);
    }



    public static final Creator CREATOR
            = new Creator() {
        public JerkBreach createFromParcel(Parcel in) {
            return new JerkBreach(in);
        }

        public JerkBreach[] newArray(int size) {
            return new JerkBreach[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}


