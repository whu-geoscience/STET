package edu.whu.cgf.geoportal.util.lda;

import src.config.Model1Constant;
import edu.whu.cgf.geoportal.entity.recommenddata;
import edu.whu.cgf.geoportal.service.ImageElementService;

import java.util.*;


/**
 * @author lyx
 */
public class LdaUtil
{
    /**
     * To translate a LDA matrix to readable result
     * @param phi the LDA model
     * @param vocabulary
     * @param i_limit limit of max words in a topic
     * @return a map array
     */
    public static Map<String, Double>[] translate(double[][] phi, edu.whu.cgf.geoportal.util.lda.Vocabulary vocabulary, int i_limit)
    {
        int limit = Math.min(i_limit, phi[0].length);
        Map<String, Double>[] result = new Map[phi.length];
        for (int k = 0; k < phi.length; k++)
        {
            Map<Double, String> rankMap = new TreeMap<Double, String>(Collections.reverseOrder());
            for (int i = 0; i < phi[k].length; i++)
            {
                rankMap.put(phi[k][i], vocabulary.getWord(i));
            }
            Iterator<Map.Entry<Double, String>> iterator = rankMap.entrySet().iterator();
            result[k] = new LinkedHashMap<String, Double>();
            for (int i = 0; i < limit; ++i)
            {
                if(iterator.hasNext()){
                    Map.Entry<Double, String> entry = iterator.next();
                    result[k].put(entry.getValue(), entry.getKey());
                }

            }
        }
        return result;
    }

    public static Map<String, Double> translate(double[] tp, double[][] phi, Vocabulary vocabulary, int limit)
    {
        Map<String, Double>[] topicMapArray = translate(phi, vocabulary, limit);

        String temp = getTopTopic(tp);

        int topic = Integer.parseInt(temp.split(",")[0]);
        return topicMapArray[topic];
    }

    public static String getTopTopic(double[] tp)
    {
        // The sum of the probabilities of each topic is 1
        double p = -1.0;
        int t = -1;
        for (int k = 0; k < tp.length; k++)
        {
            if (tp[k] > p)
            {
                p = tp[k];
                t = k;
            }
        }
        return t+","+p;
    }

    public static List<recommenddata> runFullSet(List<int[]> testSet, double[][] phi, ImageElementService full){
        double[] tp;
        List<recommenddata> result=new ArrayList<>();
        for(int i=0;i<testSet.size();i++){
            tp = edu.whu.cgf.geoportal.util.lda.LdaGibbsSampler.inference(phi,testSet.get(i));
            recommenddata recommendData =new recommenddata();
            if(LdaUtil.recommend(tp,recommendData)){
                recommendData.setUserName(full.getUserName());
                recommendData.setProductCode(full.getIdList().get(i));
                recommendData.setDataType("raster");
                result.add(recommendData);
            }
        }
        return result;

    }




    public static boolean recommend(double[] tp, recommenddata recommendData){
        String[] temp = getTopTopic(tp).split(",");
        double p = Double.parseDouble(temp[1]);
        recommendData.setScore(p);
        if(p>= Model1Constant.threshold){
            return true;
        }else{
            return false;
        }
    }



    public static Double recommendScore(int[] testSet, double[][] phi){
        double[] tp;
        double p = 0.0;

        tp = edu.whu.cgf.geoportal.util.lda.LdaGibbsSampler.inference(phi, testSet);
        recommenddata recommendData = new recommenddata();
        String[] temp = getTopTopic(tp).split(",");
        p = Double.parseDouble(temp[1]);
        recommendData.setScore(p);

        return p;
    }

    /**
     * To print the result in a well formatted form
     * @param result
     */
    public static void explain(Map<String, Double>[] result)
    {
        int i = 0;
        for (Map<String, Double> topicMap : result)
        {
            System.out.printf("topic %d :\n", i++);
            explain(topicMap);
            System.out.println();
        }
    }

    public static void explain(Map<String, Double> topicMap)
    {
        for (Map.Entry<String, Double> entry : topicMap.entrySet())
        {
            System.out.println(entry);
        }
    }
}
