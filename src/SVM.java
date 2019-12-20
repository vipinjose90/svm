/**
 *
 * @author Vipin Jose
 */
import java.io.*;
import java.util.*;

public class SVM {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws FileNotFoundException { 
        
        RunSVM rs=new RunSVM();
        RunSVM2 rs2=new RunSVM2();
        RunSVM3 rs3=new RunSVM3();
        RandomForest  rf=new RandomForest();
        RandomForest2  rf2=new RandomForest2();
        RandomForest2v3  rf2v3=new RandomForest2v3();
    
/* 3.1.1  Runs basic SVM on Handwriting Dataset   */
        rs.startSVM();
        
/* 3.1.2  Runs SVM on Madelon Dataset using cross validation   */
        rs2.startCrossValidation();
        
/* 3.1.2  Runs basic SVM on Madelon Dataset on Train and Test to report accuracy, recall, precision and F-score  */
        rs3.startSVM();

 /* 3.2.1  Runs basic SVM on Handwriting Dataset   */
        rf.startRandomForest();

 /* 3.2.2  Runs basic SVM on Madelon DataSet using first method of discretization   */
    //    rf2.startRandomForest();
        
    
/* 3.2.2  Runs basic SVM on Handwriting Dataset(with the final method of discretization)   */
        rf2v3.startRandomForest();
   
        

    }
}
    
