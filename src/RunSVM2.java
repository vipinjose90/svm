
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

class RunSVM2{
    
    //*******************CROSS-VALIDATION SETTINGS************START************//
    //Mention the Training File without extension
    public static String trainName="data/madelon/madelon_train";    
    //Mention the Test File without extension
    public static String testName="data/madelon/madelon_test";
    //Mention Output Directory for Cross Validation FIles
    public static String outFolder="data/madelon/crossvalidation/";
    //Mention the value of learning rate to start with
    float initY=(float) 1;
    //Mention the value of C to start with
    float initC=(float) 2;
    //Mention the number of values of learning rate to be tested
    int yCount=3;
    //Mention the number of values of C to be tested
    int cCount=6;

    //*******************CROSS-VALIDATION SETTINGS************START************//
    
    
    //Mention "k" for k-fold cross-validation
    int crossValidation=5;
       
    String trainFile=trainName+".data";
    String trainLabelFile=trainName+".labels";
    int trainRow=0, trainCol=0;
    String [] trainTemp;
    float [][] train;
    int [] trainLabels;
    float avgAccuracyTrain=0, avgAccuracyTest=0;


    RunSVM2() throws FileNotFoundException {        
        Scanner trainIn = new Scanner (new File (trainFile));
        Scanner trainIn1 = new Scanner (new File (trainFile));
        Scanner trainLab = new Scanner (new File (trainLabelFile));

        
        
        int v=0; 
        while (trainIn.hasNextLine()){
            v++;
            trainIn.nextLine();
        }
        trainRow=v;
                                
        v=0;
        trainTemp=new String[trainRow];
        while (trainIn1.hasNextLine()){
            trainTemp[v++]=trainIn1.nextLine();
        }

        
        v=0;
        trainLabels=new int[trainRow];
        while (trainLab.hasNextLine()){
            trainLabels[v++]=Integer.parseInt(trainLab.nextLine());
        }
        
    }
    
    void startCrossValidation() throws FileNotFoundException{
        splitFiles();
        float Y=initY;
        float C=initC; 
        float [] allY=new float[yCount*cCount];
        float [] allC=new float[yCount*cCount];
        float [] allTrainAccuracy=new float[yCount*cCount];
        float [] allTestAccuracy=new float[yCount*cCount];
        System.out.println("CROSS VALIDATION BEGINS.............");
        for(int i=0;i<cCount;i++){
            for(int j=0;j<yCount;j++){
                RunSVM2x1.rate=Y;
                RunSVM2x1.C=C;
                mergeAndRunCross(); 
                allY[(i*yCount)+j]=RunSVM2x1.rate;
                allC[(i*yCount)+j]=RunSVM2x1.C;

                allTrainAccuracy[(i*yCount)+j]=avgAccuracyTrain;
                allTestAccuracy[(i*yCount)+j]=avgAccuracyTest;
            //    System.out.println(((i*yCount)+j)+" "+Y+"\t\t\t\t"+C+"\t\t\t"+avgAccuracyTrain+"%\t\t\t\t"+avgAccuracyTest+"%");
                Y=Y/10;
            }
            Y=initY;
            C=C/2;
        }
        System.out.println("\n\n\n*****************************************************************************************************************");
        System.out.println("****************************************CROSS VALIDATION RESULTS*************************************************");
        System.out.println("*****************************************************************************************************************");
        System.out.println("Initial Learning Rate(Yo)\tHyperparameter C\tAccuracy on Training Data\t\t\t\tAccuracy on Test Data");
        for(int i=0;i<yCount*cCount;i++){
            System.out.println((String.format("%.6f", allY[i]))+"\t\t\t\t"+(String.format("%.8f", allC[i]))+"\t\t\t"+allTrainAccuracy[i]*100+"%\t\t\t\t\t\t\t"+allTestAccuracy[i]*100+"%");
        }
        System.out.println("*****************************************************************************************************************");
    }
    
    void splitFiles() throws FileNotFoundException{
        
        int splitSize=trainRow/crossValidation;
        PrintWriter pw,pwl;
        
        for(int i=0;i<crossValidation;i++){
            pw=new PrintWriter(outFolder+"/train"+i+".data");
            pwl=new PrintWriter(outFolder+"/trainlabels"+i+".data");
            for(int j=i*splitSize;j<(i+1)*splitSize;j++){
                pw.println(trainTemp[j]);
                pw.flush();
                pwl.println(trainLabels[j]);
                pwl.flush();
            }
        }
    } 
    
    void mergeAndRunCross() throws FileNotFoundException{
                
        avgAccuracyTrain=0; avgAccuracyTest=0;
        
        
        for(int i=0;i<crossValidation;i++){
 
            System.out.println("\n\n\nITERATION "+(i+1)+">>>>>>>>>\n");
            String file0=outFolder+"train"+((i)%crossValidation)+".data";
            String file1=outFolder+"train"+((i+1)%crossValidation)+".data";
            String file2=outFolder+"train"+((i+2)%crossValidation)+".data";
            String file3=outFolder+"train"+((i+3)%crossValidation)+".data";
            String file4=outFolder+"train"+((i+4)%crossValidation)+".data";
            String fileLabel0=outFolder+"trainlabels"+((i)%crossValidation)+".data";
            String fileLabel1=outFolder+"trainlabels"+((i+1)%crossValidation)+".data";
            String fileLabel2=outFolder+"trainlabels"+((i+2)%crossValidation)+".data";
            String fileLabel3=outFolder+"trainlabels"+((i+3)%crossValidation)+".data";
            String fileLabel4=outFolder+"trainlabels"+((i+4)%crossValidation)+".data";

            String fileout=outFolder+"merge"+i+".data";
            String fileoutlabel=outFolder+"mergelabel"+i+".data";
            System.out.println("Training on "+fileout);
            System.out.println("Testing on "+file4);

            PrintWriter writer = new PrintWriter(fileout);
            PrintWriter writer1 = new PrintWriter(fileoutlabel);
            ArrayList arr=new ArrayList();

            Scanner fileIn0 = new Scanner (new File (file0));
            Scanner fileIn1 = new Scanner (new File (file1));
            Scanner fileIn2 = new Scanner (new File (file2));
            Scanner fileIn3 = new Scanner (new File (file3));
            Scanner fileIn4 = new Scanner (new File (file4));
            Scanner fileInLabel0 = new Scanner (new File (fileLabel0));
            Scanner fileInLabel1 = new Scanner (new File (fileLabel1));
            Scanner fileInLabel2 = new Scanner (new File (fileLabel2));
            Scanner fileInLabel3 = new Scanner (new File (fileLabel3));
            Scanner fileInLabel4 = new Scanner (new File (fileLabel4));


            while (fileIn0.hasNextLine()){
                writer.println(fileIn0.nextLine());
                writer.flush();
            }
            while (fileIn1.hasNextLine()){
                writer.println(fileIn1.nextLine());
                writer.flush();
            }
            while (fileIn2.hasNextLine()){
                writer.println(fileIn2.nextLine());
                writer.flush();
            }
            while (fileIn3.hasNextLine()){
                writer.println(fileIn3.nextLine());
                writer.flush();
            } 
            while (fileInLabel0.hasNextLine()){
                writer1.println(fileInLabel0.nextLine());
                writer1.flush();
            }
            while (fileInLabel1.hasNextLine()){
                writer1.println(fileInLabel1.nextLine());
                writer1.flush();
            }
            while (fileInLabel2.hasNextLine()){
                writer1.println(fileInLabel2.nextLine());
                writer1.flush();
            }
            while (fileInLabel3.hasNextLine()){
                writer1.println(fileInLabel3.nextLine());
                writer1.flush();
            }

            RunSVM2x1.trainFile=fileout;
            RunSVM2x1.testFile=file4;

            RunSVM2x1.trainLabelFile=fileoutlabel;
            RunSVM2x1.testLabelFile=fileLabel4;

            RunSVM2x1 rs = new RunSVM2x1();
            rs.startSVM();
            avgAccuracyTrain+= RunSVM2x1.accuracyTrain;
            avgAccuracyTest+= RunSVM2x1.accuracy;
        }
        avgAccuracyTrain=avgAccuracyTrain/(float)crossValidation;
        avgAccuracyTest=avgAccuracyTest/(float)crossValidation;
        System.out.println("\n\n\n**********************AVERAGE ACCURACY ON CROSS VALIDATION********************");
        System.out.println("Rate (Yo) = "+RunSVM2x1.rate);
        System.out.println("C = "+RunSVM2x1.C);
        System.out.println("Average accuracy on Training Data = "+avgAccuracyTrain*100+"%");
        System.out.println("Average accuracy on Test Data = "+avgAccuracyTest*100+"%");
        System.out.println("******************************************************************************");
        
    }
}

class RunSVM2x1{
    
    //*******************************SVM SETTINGS************START************//
    //Mention the Training File without extension
    public static String trainName="data/handwriting/train";    
    //Mention the Test File here without extension
    public static String testName="data/handwriting/test";
    //Mention the starting value of rate(Yo)
    public static float rate=(float) 0.01;
    //Mention the value of C
    public static float C=(float) 1;
    //Mention the value of "number of epochs"
    public static int epochs= 10;
    //*******************************SVM SETTINGS*************END***********//
    
    
    public static float accuracy, accuracyTrain;
    public static float sd,sdTrain;
    public static int mistakesTrain;
    public static float avgMistakes;    
    public static String trainFile=trainName+".data";
    public static String testFile=testName+".data";
    public static String trainLabelFile=trainName+".labels";
    public static String testLabelFile=testName+".labels";
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


    RunSVM2x1() throws FileNotFoundException {        
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
    }
    
    void startSVM(){
        preProcess();
        trainSVM();
        predictSVMonTrain();
        predictSVMonTest();
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
        
        /*Initializing the weights and bias*/
        weightSize=trainCol;
        weights=new float[weightSize];
        for(int i=0;i<weightSize;i++){
            weights[i]=0;
        }

        prediction=new int[testRow];
        
//        for(int i=0;i<trainRow;i++){
//            for(int j=0;j<trainCol;j++){
//                System.out.print(train[i][j]+"\t");   
//            }
//            System.out.println();
//        }
        
        
    }
    
    void trainSVM(){
        mistakeCounter=0;
        float Y=rate;
        int t=1;
        for(int e=0;e<epochs;e++){
            shuffle();
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
        }
        accuracyTrain=((trainRow-mistakeCounter)/(float)trainRow);
        printTestStatsonTrain();
    }
    
    void predictSVMonTest(){
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
        }
        accuracy=((testRow-mistakeCounter)/(float)testRow);
        mistakesTrain=mistakeCounter;
        printTestStatsonTest();
    }

    
    void printTrainStats(){
        System.out.print("\n\n***** Training on data : "+trainFile+" *****");
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
        System.out.print("\n***** Testing on Train data : "+trainFile+" *****");
        System.out.print("\nValue of C used= "+C);
        System.out.print("\nInitial Value of Learning Rate(Y) = "+rate);
        System.out.print("\nNumber of epochs used= "+epochs);
        System.out.print("\nNumber of incorrect predictions = "+mistakeCounter);
        System.out.print("\nTotal input = "+trainRow);
        System.out.println("\nAccuracy = "+accuracyTrain*100+" %");        
    }
    
    void printTestStatsonTest(){    
        System.out.print("\n***** Testing on Test data : "+testFile+" *****");
        System.out.print("\nValue of C used= "+C);
        System.out.print("\nInitial Value of Learning Rate(Y) = "+rate);
        System.out.print("\nNumber of epochs used= "+epochs);
        System.out.print("\nNumber of incorrect predictions = "+mistakeCounter);
        System.out.print("\nTotal input = "+testRow);
        System.out.println("\nAccuracy = "+accuracy*100+" %");
        
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

