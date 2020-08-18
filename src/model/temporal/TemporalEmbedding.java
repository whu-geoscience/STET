package datesystem;

/**
 * @author lyx
 */

public class TemporalEmbedding {
    //startDate
    private long startDate;
    //interval
    private long interval;

    public TemporalEmbedding(long startDate,long interval){
        this.startDate=startDate;
        this.interval=interval;
    }

    public long getIndex(long date){
        long minus = date - startDate;
        long i = minus/1000/60/60/24/interval;
        return i;
    }

    public String getString(long date){
        return "Date:"+getIndex(date);
    }
}
