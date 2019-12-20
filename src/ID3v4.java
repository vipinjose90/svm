
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class ID3v4{

    public static int maxDepth=1000000;
    
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
    int [] labelCountT = new int[2];
    String [] labelsT = new String[2];
    String prediction[];
    float splitTrain[];
    Node startRoot;
    int k;

    ID3v4(String [][] subTrain, int k) throws FileNotFoundException {
        trainSize=subTrain.length;
        columnSize=subTrain[0].length;
        train=new String[trainSize][columnSize];
        for(int i=0;i<trainSize;i++){
            for(int j=0;j<columnSize;j++){
                train[i][j]=subTrain[i][j];
            }
        }
        this.k=k;
        splitTrain= new float[columnSize-1];
    }


    
    class Node{
        String [][] data;
        float [] iG;
        int feature;
        String mapping;
        String isLeaf="No";
        int nodeCol=0;
        int nodeRow=0;
        String leaf;
        int [] labelCount = new int[2];
        String [] labels = new String[2];
        LinkedList fV=new LinkedList();
        List <Node> children;

        
        Node(String [][] data,String mapping,int nodeRow){
            this.nodeCol=columnSize;
            this.data = new String[nodeRow][this.nodeCol];
            iG = new float[this.nodeCol-1];
            this.feature=feature;
            this.mapping=mapping;
            this.nodeRow=nodeRow;
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
                    noOfVals=0;
                    valValue[0]=data[0][i];
                    for(int j=0;j<nodeRow;j++){
                        for(int k=0;k<j;k++){
                            if(data[k][i].equals(data[j][i])){
                                flag=true;
                            }

                        }
                        if(!flag){
                            valValue[noOfVals++]=data[j][i];
                        }
                        else
                            flag=false;
                    }
                    for(int k=0;k<noOfVals;k++){
                        valCount[k]=0;
                    }

                    for(int j=0;j<nodeRow;j++){
                        for(int k=0;k<noOfVals;k++){
                            if((data[j][i]).equals(valValue[k])){
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
                            if(data[j][i].equals(valValue[k])){
                                if(data[j][nodeCol-1].equals(labels[0]))
                                    labelForFeat[0]+=1;
                                else
                                    if(data[j][nodeCol-1].equals(labels[1]))
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
                    iG[i]=labelEntropy-entropyPart;
                }
                else
                    iG[i]=-1;     
            }
        }
    }
    
    void startTree(){
        transformTrain();
        startRoot = new Node(train,null,trainSize);
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
                    //   System.out.print(newData[rowSize][s]);
                   }
                //   System.out.println();
                   rowSize++;
               }
            }
                   
            Node child=new Node(newData,llNext,rowSize);
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
                if(tempNode.mapping.equals(test[i][root.feature])){
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
         System.out.print(root.leaf);
         if(tempDepth>depth)
             depth=tempDepth;
         return;
     }
     else{
         Node tempNode;
         for(int j=0;j<root.children.size();j++){
             tempNode=root.children.get(j);
             if(tempNode.mapping.equals(test[i][root.feature])){
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
    

    

    void transformTrain(){
        for(int i=0;i<trainSize;i++){
            for(int j=0;j<columnSize-1;j++){
                train[i][j]=String.valueOf(Math.floor(Math.sqrt(Float.parseFloat(train[i][j]))));
            }
        }
    }
    
    void transformTest(){
        for(int i=0;i<testRow;i++){
            for(int j=0;j<testCol-1;j++){
                test[i][j]=String.valueOf(Math.floor(Math.sqrt(Float.parseFloat(test[i][j]))));
            }
        }
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
        transformTest();
    //For printing path
    //    printPath();
    } 
}