package nanodegree.annekenl.walk360.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SingleDayTotals
{
    //private String dayofWeek;
    private String date_str;
    private long max_walk;   /* long and float used in shared prefs */
    private long max_sit;
    private float water_total;

    public SingleDayTotals(){}

    public SingleDayTotals(String date_str, long max_walk,
                           long max_sit, float water_total)
    {
        this.date_str = date_str;
        this.max_walk = max_walk;
        this.max_sit = max_sit;
        this.water_total = water_total;
    }

    /** BELOW is Parcel Implementation Code; pass an object into another Activity easily **/
    public int describeContents() {
        return 0;
    }

    private SingleDayTotals(Parcel in)
    {
        this.date_str = in.readString();
        this.max_walk = in.readLong();
        this.max_sit = in.readLong();
        this.water_total = in.readFloat();
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(date_str);
        out.writeFloat(max_walk);
        out.writeFloat(max_sit);
        out.writeFloat(water_total);
    }

    public static final Parcelable.Creator<SingleDayTotals> CREATOR
            = new Parcelable.Creator<SingleDayTotals>() {
        public SingleDayTotals createFromParcel(Parcel in){
            return new SingleDayTotals(in);
        }
        public SingleDayTotals[] newArray(int size){
            return new SingleDayTotals[size];
        }
    };

    public String getDate_str() {
        return date_str;
    }

    public void setDate_str(String date_str) {
        this.date_str = date_str;
    }

    public long getMax_walk() {
        return max_walk;
    }

    public void setMax_walk(long max_walk) {
        this.max_walk = max_walk;
    }

    public long getMax_sit() {
        return max_sit;
    }

    public void setMax_sit(long max_sit) {
        this.max_sit = max_sit;
    }

    public float getWater_total() {
        return water_total;
    }

    public void setWater_total(float water_total) {
        this.water_total = water_total;
    }
}
