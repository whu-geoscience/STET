package datesystem;

import org.w3c.dom.CDATASection;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import  java.lang.Math;
import java.util.Date;

/**
 * @author lyx
 */

public class DateSystem2Update implements Serializable {
    //开始日期
    private long startDate;

    //周期
    private double period = 0;
    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    private List<Long> allDate = new ArrayList<>();

    public DateSystem2Update(long startDate){
        this.startDate=startDate;
    }

    //添加数据日期
    public void addDate(long date){
        allDate.add(date);
    }

    //日期处理，得到周期
    public void dateProcess(){
        //计算数据一阶差分
        int w = allDate.size();
        int h = 0;
        long[] firstOrderD = new long[w*(w-1)/2];
        allDate.sort(Comparator.comparingLong(Long::longValue));
        for (int i=0;i<w-1;i++){
            for(int j=0;j<=i;j++){
                firstOrderD[h++] = allDate.get(i+1)-allDate.get(j);
            }
        }
        //离散傅里叶变换
        //实部
        double[] real = new double[h];
        //虚部
        double[] imag = new double[h];
        double[] a = new double[h];
        double max = 0;
        for (int k =0;k<h;k++){
            real[k] = 0;
            imag[k] = 0;
            for(int n=0; n<h; n++)
            {
                real[k] = real[k] + firstOrderD[n] * Math.cos(2*Math.PI*k*n/h);
                imag[k] = imag[k] - firstOrderD[n] * Math.sin(2*Math.PI*k*n/h);
            }
            a[k] = Math.sqrt(real[k]*real[k]+imag[k]*imag[k]);
            if (max < a[k]){
                max = a[k];
                period = a[k]/(w*(w-1));
            }
        }
    }

    //获得该日期词
    public String getString(long date){
        long minus = date - startDate;
        int i = (int) (minus/period);
        return "Date:"+i;
    }


    //Test
    public static void main(String[] args) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startDate="2014-12-12 08:23:21";
        Date sdate = df.parse(startDate);
        long startD = sdate.getTime();

        String Date1="2015-12-12 08:23:21";
        Date date1 = df.parse(Date1);
        long D1 = date1.getTime();

        String Date2="2016-12-12 08:23:21";
        Date date2 = df.parse(Date2);
        long D2 = date2.getTime();

        String Date3="2017-12-12 08:23:21";
        Date date3 = df.parse(Date3);
        long D3 = date3.getTime();

        String Date4="2018-12-12 08:23:21";
        Date date4 = df.parse(Date4);
        long D4 = date4.getTime();


        DateSystem2Update dateSystem2 = new DateSystem2Update(startD);
        //添加时间
        dateSystem2.addDate(startD);
        dateSystem2.addDate(D1);
        dateSystem2.addDate(D2);
        dateSystem2.addDate(D3);
        dateSystem2.addDate(D4);
        //时间处理
        dateSystem2.dateProcess();
        //输出
        double period = dateSystem2.getPeriod();
        double date = (period /1000/60/60/24);
        System.out.println(period);
        System.out.println(date);
        for (int i=0;i<dateSystem2.allDate.size();i++){
            System.out.println(dateSystem2.getString(dateSystem2.allDate.get(i)));
        }
    }



}
