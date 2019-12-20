
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;

class RandomForestSVM{
    
    //*******************************SVM SETTINGS************START************//
    //Mention the starting value of rate(Yo)
    public static float rate=(float) 0.01;
    //Mention the value of C
    public static float C=(float) 1;
    //Mention the value of "number of epochs"
    public static int epochs= 30;
    //*******************************SVM SETTINGS*************END***********//
    
    
    public static float accuracy, accuracyTrain,precisionTrain,precisionTest,recallTrain,recallTest;
    public static float avgAccuracy,avgAccuracyTrain;
    public static float sd,sdTrain;
    public static int mistakesTrain;
    public static float avgMistakes;    

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
    int prediction[];

    
    
    void startSVMTrain(float [][] trainData, int trainDataLabels[]){
        
        trainRow=trainData.length;
        trainCol=trainData[0].length;
        preProcess();
        train=new float[trainRow][trainCol];
        trainLabels=new int[trainRow];
        for(int i=0;i<trainRow;i++){
            for(int j=0;j<trainCol;j++){
                train[i][j]=trainData[i][j];
            }
            trainLabels[i]=trainDataLabels[i];
        }
             
        trainSVM();
        
    }
    
    void startSVMPredict(float [][] testData, int testDataLabels[]){
        
        testRow=testData.length;
        testCol=testData[0].length;
        test=new float[testRow][testCol];
        testLabels=new int[testRow];
        for(int i=0;i<testRow;i++){
            for(int j=0;j<testCol;j++){
                test[i][j]=testData[i][j];
            }
            testLabels[i]=testDataLabels[i];
        }
        predictSVMonTrain();
        predictSVMonTest();
        
    }
    
    void preProcess(){   
        /*Initializing the weights and bias*/
        weightSize=trainCol;
        weights=new float[weightSize];
        for(int i=0;i<weightSize;i++){
            weights[i]=0;
        }

        prediction=new int[testRow];     
    }
    
    void trainSVM(){
        mistakeCounter=0;
        float Y=rate;
        int t=1;
        for(int e=0;e<epochs;e++){
         //   shuffle();
            for(int i=0;i<trainRow;i++){
                Y=rate/(1+((rate*(t++))/C));
                float yp=0;
                for(int j=0;j<trainCol;j++){
                    yp=yp+(weights[j]*train[i][j]);
                }
                if(((float)(trainLabels[i])*yp)<=1){
                    mistakeCounter++;
                    for(int w=0;w<trainCol;w++){
                        weights[w]=((1-Y)*weights[w])+(Y*C*trainLabels[i]*train[i][w]);
                    }
                }
                else{
                    for(int w=0;w<trainCol;w++){
                        weights[w]=((1-Y)*weights[w]);
                    }
                }
            }
        }
        mistakesTrain=mistakeCounter;
        //printTrainStats();
    }
    
    void predictSVMonTrain(){
        
        
        int TP=0,FP=0,FN=0;
        mistakeCounter=0;
        int yp;
        for(int i=0;i<trainRow;i++){
            float ytemp=0;
            for(int j=0;j<weightSize;j++){
                ytemp=ytemp+(weights[j]*train[i][j]);
            }
            yp=sgn(ytemp);
            if(yp!=trainLabels[i]){
                mistakeCounter++;
            }
            if(trainLabels[i]==+1 && yp==+1){
                TP++;
            }
            if(trainLabels[i]==-1 && yp==+1){
                FP++;
            }
            if(trainLabels[i]==+1 && yp==-1){
                FN++;
            }
        }
        accuracyTrain=((trainRow-mistakeCounter)/(float)trainRow);
        precisionTrain=(float)TP/(float)(TP+FP);
        recallTrain=(float)TP/(float)(TP+FN);
        printTestStatsonTrain();
    }
    
    void predictSVMonTest(){
        int TP=0,FP=0,FN=0;
        mistakeCounter=0;
        int yp;
        for(int i=0;i<testRow;i++){
            float ytemp=0;
            for(int j=0;j<weightSize;j++){
                ytemp=ytemp+(weights[j]*test[i][j]);
            }
            yp=sgn(ytemp);
            if(yp!=testLabels[i]){
                mistakeCounter++;
            }
            if(testLabels[i]==+1 && yp==+1){
                TP++;
            }
            if(testLabels[i]==-1 && yp==+1){
                FP++;
            }
            if(testLabels[i]==+1 && yp==-1){
                FN++;
            }
        }
        accuracy=((testRow-mistakeCounter)/(float)testRow);
        mistakesTrain=mistakeCounter;
        precisionTest=(float)TP/(float)(TP+FP);
        recallTest=(float)TP/(float)(TP+FN);
        printTestStatsonTest();
    }

    
    void printTrainStats(){
    //    System.out.print("\n\n***** Training on data : "+trainFile+" *****");
        System.out.print("\nRate used(r) = "+rate);
        System.out.print("\nC used = "+C);
        System.out.print("\nWeight vector(w):\n");
        for(int i=0;i<weightSize;i++){
            System.out.print(weights[i]+"  ");
        }
        System.out.print("\nBias(b) = "+bias);
        System.out.print("\nNumber of mistakes made in training = "+mistakeCounter);
        System.out.print("\nTotal input = "+trainRow);
        System.out.println("\nAccuracy = "+((trainRow-mistakeCounter)/(float)trainRow)*100+" %");
    }    
    
    void printTestStatsonTrain(){    
        System.out.print("\n***** Testing on Train data ******** ");
        System.out.print("\nValue of C used= "+C);
        System.out.print("\nInitial Value of Learning Rate(Y) = "+rate);
        System.out.print("\nNumber of epochs used= "+epochs);
        System.out.print("\nNumber of incorrect predictions = "+mistakeCounter);
        System.out.print("\nTotal input = "+trainRow);
        System.out.println("\nAccuracy = "+accuracyTrain*100+" %");
        System.out.println("Precision = "+precisionTrain);
        System.out.println("Recall = "+recallTrain);
        System.out.println("F-score = "+(((float)2*precisionTrain*precisionTrain)/(precisionTrain+precisionTrain)));
    }
    
    void printTestStatsonTest(){    
        System.out.print("\n***** Testing on Test data *****");
        System.out.print("\nValue of C used= "+C);
        System.out.print("\nInitial Value of Learning Rate(Y) = "+rate);
        System.out.print("\nNumber of epochs used= "+epochs);
        System.out.print("\nNumber of incorrect predictions = "+mistakeCounter);
        System.out.print("\nTotal input = "+testRow);
        System.out.println("\nAccuracy = "+accuracy*100+" %");   
        System.out.println("Precision = "+precisionTest);
        System.out.println("Recall = "+recallTest);
        System.out.println("F-score = "+(((float)2*precisionTest*precisionTest)/(precisionTest+precisionTest)));
        
    }
    
    int sgn(float n){
        if((n)<0)
            return (-1);
        else
            return (+1);
    }
    
    void shuffle(){
        float temp;
        int templ;
        for (int i=0; i<trainRow; i++) {
            Random rnum = new Random();
            int ranPos = rnum.nextInt(trainRow);
            for(int j=0;j<trainCol;j++){
                temp = train[i][j];
                templ=trainLabels[i];
                train[i][j] = train[ranPos][j];
                trainLabels[i]=trainLabels[ranPos];                
                train[ranPos][j] = temp;
                trainLabels[ranPos]=templ;
            }
        }
    }
}
