
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class ID3v2{

    public static int maxDepth=100000;
    
    public static boolean crossValidation=false;
    public static int depthSize;
    public static int iterDeptCount=0;
    
    public static float accuracy;

    int trainSize=0, columnSize=0;
    int testRow=0, testCol=0;
    int depth=0;
    boolean firstIter=true;
    String maxLabel;
    String[][] train;
    String [][] test;
    String prediction[];
    Node startRoot;
    int k;

    ID3v2(String [][] subTrain, int k) throws FileNotFoundException {
        trainSize=subTrain.length;
        columnSize=subTrain[0].length;
        train=new String[trainSize][columnSize];
        for(int i=0;i<trainSize;i++){
            for(int j=0;j<columnSize;j++){
                train[i][j]=subTrain[i][j];
            }
        }
        this.k=k;

    }


    
    class Node{
        String [][] data;
        float [] iG;
        float [] splitVals;
        int feature;
        String mapping;
        String isLeaf="No";
        int nodeCol=0;
        int nodeRow=0;
        String leaf;
        float splitVal;
        float parentSplit;
        float splitValTemp;
        int [] labelCount = new int[2];
        String [] labels = new String[2];
        LinkedList fV=new LinkedList();
        List <Node> children;

        
        Node(String [][] data,String mapping,int nodeRow, float splitVal){
            this.nodeCol=columnSize;
            this.data = new String[nodeRow][this.nodeCol];
            iG = new float[this.nodeCol-1];
            splitVals = new float[this.nodeCol-1];
            this.feature=feature;
            this.mapping=mapping;
            this.nodeRow=nodeRow;
            this.splitVal=splitVal;
            for(int i=0;i<nodeRow;i++){
                for(int j=0;j<nodeCol;j++){
                    this.data[i][j]=data[i][j];
                }
            }
            children = new ArrayList<>();
        }
        
        void checkLabel(){
            int [] count = new int[2];
            labelCount[0]=0; labelCount[1]=0;
            String strLabel;
            strLabel=data[0][nodeCol-1];
            labels[0]=strLabel;
            for (int k=0;k<nodeRow;k++){
            //    System.out.println(data[k][nodeCol-1]);
                if (data[k][nodeCol-1].equals(strLabel)){
                    labelCount[0]+=1;
                }
                else{

                    if(labelCount[1]==0) 
                         labels[1]=data[k][nodeCol-1];
                    labelCount[1]+=1;
                }
            }
        }
        
        void calculateiG(){
            int[] labelForFeat=new int[2];
            boolean flag=false;
            int [] valCount = new int[nodeRow];
            String [] valValue = new String[nodeRow];
            int noOfVals,total;
            float labelEntropy;
            float labelTotal=labelCount[0]+labelCount[1];
            Random rnum=new Random();
            labelEntropy= 0;
            int availFeat=0;
            for(int i=0;i<nodeCol-1;i++){
                iG[i]=-1;
                if (!data[0][i].equals("Test#*#*")){
                    availFeat++;
                }
            }
            for(int i=0;i<=1;i++){
                if(labelCount[i]!=0)
                    labelEntropy = (float) ((labelEntropy + ( -1 * ((labelCount[i])/labelTotal)) * ((Math.log(labelCount[i]/labelTotal))/Math.log(2))));                 
            }
            int counter=k;
            while(counter>0 && availFeat>0){
                int i = rnum.nextInt(columnSize-1);
                if(!(data[0][i].equals("Test#*#*"))){
                    counter--;
                    availFeat--;
                    float maxiG=-20;
                    
                    String [][] sortedArray=new String[data.length][data[0].length];
                    for(int q=0;q<data.length;q++){
                        sortedArray[q][i]=data[q][i];
                        sortedArray[q][nodeCol-1]=data[q][nodeCol-1]; 
                    }
                    for(int d=0;d<data.length;d++){
                        float minVal=Float.parseFloat(sortedArray[d][i]);
                        int ind=d;
                        for(int f=d+1;f<data.length;f++){
                            if(Float.parseFloat(sortedArray[f][i])<minVal){
                                ind=f;
                                minVal=Float.parseFloat(sortedArray[f][i]);

                            }
                        }
                        String tempSwap1,tempSwap2;
                        tempSwap1=sortedArray[ind][i];
                        tempSwap2=sortedArray[ind][nodeCol-1];
                        sortedArray[ind][i]=sortedArray[d][i];
                        sortedArray[ind][nodeCol-1]=sortedArray[d][nodeCol-1];
                        sortedArray[d][i]=tempSwap1;
                        sortedArray[d][nodeCol-1]=tempSwap2;
                    }
                    
                    for(int u=0;u<nodeRow;u++){
                        if(u!=0){
                            if(sortedArray[u][i].equalsIgnoreCase(sortedArray[u-1][i]) && sortedArray[u][nodeCol-1].equalsIgnoreCase(sortedArray[u-1][nodeCol-1])){
                                continue;
                            }
                        }
                        String [][] tempData=new String[data.length][data[0].length];
                        for(int q=0;q<data.length;q++){
                            tempData[q][i]=sortedArray[q][i];
                            tempData[q][nodeCol-1]=sortedArray[q][nodeCol-1]; 
                        }
                        

                        float tempComp=Float.parseFloat(tempData[u][i]);
                        for(int q=0;q<tempData.length;q++){
                            if(Float.parseFloat(tempData[q][i])>=tempComp){
                                tempData[q][i]=">="+String.valueOf(tempComp);
                            }
                            else{
                                tempData[q][i]="<"+String.valueOf(tempComp);
                            }
                        }
                        noOfVals=0;
                        valValue[0]=tempData[0][i];
                        for(int j=0;j<nodeRow;j++){
                            for(int k=0;k<j;k++){
                                if(tempData[k][i].equals(tempData[j][i])){
                                    flag=true;
                                }

                            }
                            if(!flag){
                                valValue[noOfVals++]=tempData[j][i];
                            }
                            else
                                flag=false;
                        }
                        for(int k=0;k<noOfVals;k++){
                            valCount[k]=0;
                        }

                        for(int j=0;j<nodeRow;j++){
                            for(int k=0;k<noOfVals;k++){
                                if((tempData[j][i]).equals(valValue[k])){
                                    valCount[k]+=1;
                                }

                            }

                        }
                        total=0;
                        for(int k=0;k<noOfVals;k++){
                            total+=valCount[k];
                        }
                        //Calculation of Entropy
                        float entropy=0;
                        float entropyPart=0;
                        for(int k=0;k<noOfVals;k++){
                            labelForFeat[0]=0;
                            labelForFeat[1]=0;
                            for(int j=0;j<nodeRow;j++){
                                if(tempData[j][i].equals(valValue[k])){
                                //    System.out.print(tempData[j][nodeCol-1]+"\t"+labels[0]);
                                    if(tempData[j][nodeCol-1].equals(labels[0]))
                                        labelForFeat[0]+=1;
                                    else
                                        if(tempData[j][nodeCol-1].equals(labels[1]))
                                            labelForFeat[1]+=1;
                                }                    
                            }
                            int tempTotal=labelForFeat[0]+labelForFeat[1];
                            entropy=0;
                            for(int n=0;n<=1;n++){
                                if(labelForFeat[n]!=0)
                                    entropy = (float) ((entropy + ( -1 * (((float)labelForFeat[n])/(float)tempTotal)) * ((Math.log((float)labelForFeat[n]/(float)tempTotal))/(float)Math.log(2))));
                            }
                            entropyPart+=(float)((valCount[k]/(float)total) * entropy);
                        }
                        float runiG=labelEntropy-entropyPart;
//                        System.out.println(labelEntropy+" "+entropyPart);
//                        System.out.println(runiG+" "+tempComp);
//                        System.out.println();
                        if (runiG>maxiG){
                            maxiG=runiG;
                            splitValTemp=tempComp;
                        }                       
                    }
                 
                    iG[i]=maxiG;
                    splitVals[i]=splitValTemp;
                }
                else
                    iG[i]=-1;     
            }
        }
    }
    
    void startTree(){

        startRoot = new Node(train,null,trainSize,0);
        buildTree(startRoot,0);

        /*
       //For printing predictions
            for(int i=0;i<testRow;i++){
                for(int j=0;j<testCol;j++){
                    System.out.print(test[i][j]+" ");
                }
                System.out.println(prediction[i]);
            }
       */     
       //For printing accuracy
       // checkAccuracy();
    }
    
    void buildTree(Node root,int cDepth){
       root.checkLabel();
       if(firstIter){
           firstIter=false;
           if((root.labelCount[0])>(root.labelCount[1]))
               maxLabel=root.labels[0];
           else    
               maxLabel=root.labels[1];
       }
        
       if(root.labelCount[0]==0){
            root.isLeaf="Yes";
            root.leaf=root.labels[1];

            return;
        }
        if(root.labelCount[1]==0){
            root.isLeaf="Yes";
            root.leaf=root.labels[0];
            return;
        }
 
        if(cDepth==(maxDepth)){
            
            if((root.labelCount[0])>(root.labelCount[1])){
                root.isLeaf="Yes";
                root.leaf=root.labels[0];

                return;               
            }
            else{    
               root.isLeaf="Yes";
               root.leaf=root.labels[1];
                              
               return; 
            }
            
        }

        root.calculateiG();
        float max=-1;
        int maxFeat=0;
        for(int i=0;i<root.nodeCol-1;i++){ 
            if(root.iG[i]>max){
                max=root.iG[i]; 
                maxFeat=i;
            }
        }
        
        if(max==-1){
            if(root.labelCount[0]>=root.labelCount[1]){
                 root.isLeaf="Yes";
                 root.leaf=root.labels[0];           
                 return;
            }
            else{
                 root.isLeaf="Yes";
                 root.leaf=root.labels[1];
                 return;
            }
        }
        float iterSplit=root.splitVals[maxFeat];
        for(int q=0;q<root.nodeRow;q++){
            if(Float.parseFloat(root.data[q][maxFeat])>=iterSplit){
                root.data[q][maxFeat]= ">=" + String.valueOf(iterSplit);
            }
            else{
                root.data[q][maxFeat]= "<" + String.valueOf(iterSplit);
            }
        }
        root.feature=maxFeat;
        boolean flag=false;
        int [] valCount = new int[root.nodeRow];
        String [] valValue = new String[root.nodeRow];
        int noOfVals,total;
        noOfVals=0;
        valValue[0]=root.data[0][maxFeat];
        for(int j=0;j<root.nodeRow;j++){
            for(int k=0;k<j;k++){
                if(root.data[k][maxFeat].equals(root.data[j][maxFeat])){
                    flag=true;
                }

            }
            if(!flag){
                valValue[noOfVals++]=root.data[j][maxFeat];
            }
            else
                flag=false;
        }
        int k=0,rowSize;
        for(int i=0;i<noOfVals;i++){
            root.fV.add(valValue[i]);
        }
        while(!root.fV.isEmpty()){
            String llNext;
            
            
            llNext=(String) root.fV.pop();  
            String [][] tempData=new String [root.nodeRow][root.nodeCol];
            String [][] newData=new String [root.nodeRow][root.nodeCol];
            rowSize=0;
            for(int m=0;m<root.nodeRow;m++){
                k=0;
                for(int n=0;n<root.nodeCol;n++){
                    if(root.data[m][maxFeat].equals(llNext)){
                        if(maxFeat==n){
                            tempData[m][n]="Test#*#*";
                        }
                        else{
                            tempData[m][n]=root.data[m][n];                           
                        }
                    }

                }
            }
           for(int t=0;t<root.nodeRow;t++){
               if((tempData[t][0]!=null)||(tempData[t][2]!=null)){
                   for(int s=0;s<root.nodeCol;s++){
                       newData[rowSize][s]=tempData[t][s]; 
                //       System.out.print(newData[rowSize][s]);
                   }
                //   System.out.println();
                   rowSize++;
               }
            }
                   
            Node child=new Node(newData,llNext,rowSize,iterSplit);
            root.children.add(child);
            buildTree(child,cDepth+1);
        }
   
   }

 
    
    void predict(Node root,int i){
        if(root.isLeaf.equals("Yes")){
            prediction[i]=root.leaf;
            return;
        }
        else{
            Node tempNode;
            for(int j=0;j<root.children.size();j++){
                tempNode=root.children.get(j);
                String calcSplit;
                if(Float.parseFloat(test[i][root.feature])>=tempNode.splitVal){
                    calcSplit=">=" + String.valueOf(tempNode.splitVal);
                }
                else{
                    calcSplit="<" + String.valueOf(tempNode.splitVal);
                }

            //    System.out.print(tempNode.mapping+"\t");
            //    System.out.println(calcSplit);
                if(tempNode.mapping.equals(calcSplit)){             
                    predict(tempNode,i);
                    return;
                }

                    
  
            }

        }
        
    }
    
    //For printing the path 
    void printPath(){
        for(int i=0;i<testRow;i++){
            showPath(startRoot,i,0);
            System.out.println();
        }       
        System.out.println("Maximum Depth of the tree: "+depth);
        
    }
    
    
    void showPath(Node root,int i,int tempDepth){

     if(root.isLeaf.equals("Yes")){
        // System.out.println("Here"+root.leaf);
         System.out.print(root.leaf);
         if(tempDepth>depth)
             depth=tempDepth;
         return;
     }
     else{
         Node tempNode;
         for(int j=0;j<root.children.size();j++){
             tempNode=root.children.get(j);
                String calcSplit;
                if(Float.parseFloat(test[i][root.feature])>=tempNode.splitVal){
                    calcSplit=">=" + String.valueOf(tempNode.splitVal);
                }
                else{
                    calcSplit="<" + String.valueOf(tempNode.splitVal);
                }
            //    System.out.println("the mapping: "+tempNode.mapping);    
            //    System.out.println("the split: "+calcSplit);
             if(tempNode.mapping.equals(calcSplit)){                    
                 System.out.print(tempNode.mapping+"->");
                 tempDepth++;
                 showPath(tempNode,i,tempDepth); 
             } 
         }
      }  
    }
    
    void checkAccuracy(){
        float accuracyCount=0;
        float error=0;
        float accu=0;
        for(int i=0;i<testRow;i++){
            if(test[i][columnSize-1].equals(prediction[i])){
                accuracyCount++;
            }
        }
        error=(float)(testRow-accuracyCount)/(float)testRow;
        accu=accuracyCount/testRow;
        System.out.println("Error: "+(error*100)+"%");
        System.out.println("Accuracy: "+(accu*100)+"%");
        if(crossValidation){
            ID3.accuracy=accu;
        }
    }
    
    String [] getPrediction(){
        prediction=new String[testRow];
        for(int i=0;i<testRow;i++){
            predict(startRoot,i);
            if(prediction[i]==null)
                prediction[i]=maxLabel;
        }
    //    checkAccuracy();
        return prediction;
    }
    
    void setTestData(String [] [] testData){
        testRow=testData.length;
        testCol=testData[0].length;
        test=new String[testRow][testCol];
        for(int i=0;i<testRow;i++){
            for(int j=0;j<testCol;j++){
                test[i][j]=testData[i][j];
            }
        }
    //For printing path
    //    printPath();
    } 
}