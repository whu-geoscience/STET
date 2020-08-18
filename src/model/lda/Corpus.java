package edu.whu.cgf.geoportal.util.lda;

import edu.whu.cgf.geoportal.service.ImageElementService;
import src.model.spatial.SpatialEmbedding;
import src.config.Model1Constant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * a set of documents
 *
 * @author lyx
 */

public class Corpus
{

    @Autowired
    private ImageElementService imageElementService;

    private int orderNum = Model1Constant.orderNum;



    List<int[]> documentList;
    Vocabulary vocabulary;

    /**
    * Initialize two lists and apply for space
    *
    */
    public Corpus()
    {
        documentList = new LinkedList<int[]>();
        vocabulary = new Vocabulary();
    }


    public int[] addDocument(List<String> document)
    {
        int[] doc = new int[document.size()];
        int i = 0;
        for (String word : document)
        {
            doc[i++] = vocabulary.getId(word, true);
        }
        documentList.add(doc);
        return doc;
    }

    public int[][] toArray()
    {
        return documentList.toArray(new int[0][]);
    }

    public int getVocabularySize()
    {
        return vocabulary.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for (int[] doc : documentList)
        {
            sb.append(Arrays.toString(doc)).append("\n");
        }
        sb.append(vocabulary);
        return sb.toString();
    }

    public Corpus loadLineFromDataBase(String userName, GridSystem gridSystem) throws Exception
    {
        Corpus corpus = new Corpus();
        imageElementService.init(userName);
        List<List<String>> lineList=imageElementService.getTraningSet(gridSystem);
        lineList = lineList.subList(0, Math.min(orderNum, lineList.size()));
        if (lineList.size()==0) {
            return null;
        }
        for (List<String> wordList:
                lineList) {
            corpus.addDocument(wordList);
        }

        if (corpus.getVocabularySize() == 0) {
            return null;
        }
        return corpus;
    }

    /**
     * Get all the data, specify the number
      * @param full	
    * @param vocabulary
    * @param gridSystem
     * @return java.util.List<int[]>
     * @exception
     * @see
     */
    public List<int[]> loadFullSetDataBase(ImageElementService full, Vocabulary vocabulary, GridSystem gridSystem, Integer down, Integer up) throws Exception{
        List<List<String>> lineList=full.getAllDocument(down,up,gridSystem);

        if (lineList == null){
            return null;
        }

        List<int[]> result = new ArrayList<>();

        for(List<String> wordList:lineList){
            result.add(words2Indexes(wordList,vocabulary));
        }

        return result;

    }


    public int[] loadSetDataBase(ImageElementService full, String productCode, Vocabulary vocabulary, GridSystem gridSystem) throws Exception{
        List<String> wordList=full.getDocumentById(productCode, gridSystem);
        int[] result = words2Indexes(wordList,vocabulary);

        return result;
    }




    public Vocabulary getVocabulary()
    {
        return vocabulary;
    }

    public int[][] getDocument()
    {
        return toArray();
    }


    public int[] words2Indexes(List<String> wordList,Vocabulary vocabulary){
        Integer id;
        List<Integer> indexList = new ArrayList<>();

        for(int i=0;i<wordList.size();i++){
            String word = wordList.get(i);
            id = vocabulary.getId(word);
            if(id!=null) {
                indexList.add(id);
            }
            id = null;
        }

        int[] result = new int[indexList.size()];
        int i = 0;
        for (int j=0;j<indexList.size();j++)
        {
            int integer = indexList.get(j);
            result[i++] = integer;
        }
        return result;
    }



}
