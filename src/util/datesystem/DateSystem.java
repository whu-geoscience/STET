package datesystem;

/**
 * @author lyx
 */

public class DateSystem {
    //开始日期
    private long startDate;
    //聚类间隔
    private long interval;

    public DateSystem(long startDate,long interval){
        this.startDate=startDate;
        this.interval=interval;
    }

    //实验以毫秒为单位的情况
    public long getIndex(long date){
        long minus = date - startDate;
        long i = minus/1000/60/60/24/interval;
        return i;
    }

    public String getString(long date){
        return "Date:"+getIndex(date);
    }
}
