/**
 *
 * @author Vipin Jose
 */
import java.io.*;
import java.util.*;

public class RandomForest {
    
    //Mention the Training File without extension
    public static String trainName="data/handwriting/train";    
    //Mention the Test File here without extension
    public static String testName="data/handwriting/test";
    //Mention the value of N
    int N=5;
    //Mention the value of m
    int m=1000;
    //Mention the value of k
    int k=8;
    

    String trainFile=trainName+".data";
    String testFile=testName+".data";
    String trainLabelFile=trainName+".labels";
    String testLabelFile=testName+".labels";
    int trainRow=0, trainCol=0;
    int testRow=0, testCol=0;
    int depth=0;
    boolean firstIter=true;
    String[][] trainTemp;
    String [][] testTemp;
    float[][] train;
    float[][] test;
    int[] trainLabels;
    int[] testLabels;
    int weightSize=0;
    float weights[];
    float bias=0;
    int mistakeCounter=0;
    String prediction[];
    int featureSet[][];
    ID3 [] dt = new ID3 [N];
    float [][] treeTrainPred;
    float [][] treeTestPred;
    RandomForestSVM rf=new RandomForestSVM();
    
    RandomForest() throws FileNotFoundException {        
        Scanner trainIn = new Scanner (new File (trainFile));
        Scanner trainIn1 = new Scanner (new File (trainFile));
        Scanner trainLab = new Scanner (new File (trainLabelFile));
        Scanner testIn = new Scanner (new File (testFile));
        Scanner testIn1 = new Scanner (new File (testFile));
        Scanner testLab = new Scanner (new File (testLabelFile));
        
        
        int v=0; 
        while (trainIn.hasNextLine()){
            v++;
            trainIn.nextLine();
        }
        trainRow=v;
                                
        v=0;
        trainTemp=new String[trainRow][];
        while (trainIn1.hasNextLine()){
            trainTemp[v++]=trainIn1.nextLine().split("\\s+");
        }
        
        
        v=0;
        while (testIn.hasNextLine()){
            v++;
            testIn.nextLine();
        }
        testRow=v;
        
        v=0;
        testTemp=new String[testRow][];
        while (testIn1.hasNextLine()){
            testTemp[v++]=testIn1.nextLine().split("\\s+");
        }
        
        v=0;
        trainLabels=new int[trainRow];
        while (trainLab.hasNextLine()){
            trainLabels[v++]=Integer.parseInt(trainLab.nextLine());
        }
        
        v=0;      
        testLabels=new int[testRow];
        while (testLab.hasNextLine()){
            testLabels[v++]=Integer.parseInt(testLab.nextLine());
        }
        treeTrainPred=new float[trainRow][N+1];
        treeTestPred=new float[testRow][N+1];
    }

    void startRandomForest() throws FileNotFoundException {
        
        System.out.print("\n***** Training data : "+trainFile+" *****");
        System.out.print("\n***** Testing data : "+testFile+" *****\n\n");
        preProcess();
        createFeatureVector();
        createMultipleTrees();
        createTrainPredict();
        trainSVMforForest();
        createTestPredict();
        predictSVMforForest();
    }
    
    
    void preProcess(){
        
        trainCol=trainTemp[0].length+1;
        train=new float[trainRow][trainCol];
        for(int i=0;i<trainRow;i++){
            train[i][0]=1;
            for(int j=1;j<trainTemp[i].length+1;j++){
                train[i][j]=Float.parseFloat(trainTemp[i][j-1]);   
            }
        }
        
        testCol=testTemp[0].length+1;
        test=new float[testRow][testCol];
        for(int i=0;i<testRow;i++){
            test[i][0]=1;
            for(int j=1;j<testTemp[i].length+1;j++){
                test[i][j]=Float.parseFloat(testTemp[i][j-1]);   
            }
        }
        featureSet = new int[N][trainCol];
             
    }
    
    void createFeatureVector(){
        Random rnum = new Random();
        float temp;
        int templ;
        for (int i=0; i<N; i++) {
            for(int j=0; j<trainCol; j++){
                featureSet[i][j]=j;
            }
        }
    }
    
    void createMultipleTrees() throws FileNotFoundException{

        Random rnum = new Random();
        for(int i=0;i<N;i++){
            String [][] sData = new String [m][trainCol+1];
            for(int j=0;j<m;j++){                                
                int ranPos = rnum.nextInt(trainRow);
                for(int l=0;l<trainCol;l++){
                    sData[j][l]=String.valueOf(train[ranPos][featureSet[i][l]]);
                }
                sData[j][trainCol]=String.valueOf(trainLabels[ranPos]);
            }

            dt[i]=new ID3(sData,k);
            dt[i].startTree();
        }
        
    }
    
    void createTrainPredict(){
        for(int i=0;i<N;i++){
            String [][] sData = new String [trainRow][trainCol+1];
            for(int j=0;j<trainRow;j++){                                
                for(int l=0;l<trainCol;l++){
                    sData[j][l]=String.valueOf(train[j][featureSet[i][l]]);
                }
                sData[j][trainCol]=String.valueOf(trainLabels[j]);
            }
            dt[i].setTestData(sData);
            String [] nodePred=new String[trainRow];
            nodePred=dt[i].getPrediction();
            for(int v=0;v<trainRow;v++){
                treeTrainPred[v][i+1]=Float.parseFloat(nodePred[v]);
            }
        }
        
        for(int i=0;i<trainRow;i++){
            treeTrainPred[i][0]=1;
        }
        
        
//        for(int i=0;i<testRow;i++){
//            for(int j=0;j<N+1;j++){
//                System.out.print(treeTrainPred[i][j]+"\t");
//            }
//            System.out.println();
//        }  
    }
    void createTestPredict(){
        for(int i=0;i<N;i++){
            String [][] sData = new String [testRow][trainCol+1];
            for(int j=0;j<testRow;j++){                                
                for(int l=0;l<trainCol;l++){
                    sData[j][l]=String.valueOf(test[j][featureSet[i][l]]);
                }
                sData[j][trainCol]=String.valueOf(testLabels[j]);
            }
            dt[i].setTestData(sData);
            String [] nodePred=new String[testRow];
            nodePred=dt[i].getPrediction();
            for(int v=0;v<testRow;v++){
                treeTestPred[v][i+1]=Float.parseFloat(nodePred[v]);
            }
        }
        
        for(int i=0;i<testRow;i++){
            treeTestPred[i][0]=1;
        }
          
    }
    
    void trainSVMforForest() throws FileNotFoundException{
        rf.startSVMTrain(treeTrainPred, trainLabels);    
    }
    
    void predictSVMforForest() throws FileNotFoundException{
        rf.startSVMPredict(treeTestPred, testLabels);    
    }
    
}



    
