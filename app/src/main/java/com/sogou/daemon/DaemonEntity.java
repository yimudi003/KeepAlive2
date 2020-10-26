package com.sogou.daemon;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class DaemonEntity implements Parcelable {
    public static final Creator<DaemonEntity> CREATOR = new Creator<DaemonEntity>() {
        /* renamed from: a */
        public DaemonEntity createFromParcel(Parcel parcel) {
            return new DaemonEntity(parcel);
        }

        /* renamed from: a */
        public DaemonEntity[] newArray(int i) {
            return new DaemonEntity[i];
        }
    };
    public String[] a;
    public String b;

    /* renamed from: c  reason: collision with root package name */
    public Intent f4740c;
    public Intent d;
    public Intent e;

    public int describeContents() {
        return 0;
    }

    public DaemonEntity() {
    }

    protected DaemonEntity(Parcel parcel) {
        this.a = parcel.createStringArray();
        this.b = parcel.readString();
        if (parcel.readInt() != 0) {
            this.f4740c = (Intent) Intent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.d = (Intent) Intent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.e = (Intent) Intent.CREATOR.createFromParcel(parcel);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(this.a);
        parcel.writeString(this.b);
        if (this.f4740c == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            this.f4740c.writeToParcel(parcel, i);
        }
        if (this.d == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            this.d.writeToParcel(parcel, i);
        }
        if (this.e == null) {
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(1);
        this.e.writeToParcel(parcel, i);
    }

    public static DaemonEntity create(String str) {
        byte[] decode = Base64.decode(str, 2);
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(decode, 0, decode.length);
        obtain.setDataPosition(0);
        return CREATOR.createFromParcel(obtain);
    }

    public String toString() {
        Parcel obtain = Parcel.obtain();
        writeToParcel(obtain, 0);
        String encodeToString = Base64.encodeToString(obtain.marshall(), 2);
        obtain.recycle();
        return encodeToString;
    }
}
