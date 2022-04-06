package com.example.motioncontrolcalibration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import Jama.*;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    public static double [][] Eigen_vectors_1;
    public static double [][] Eigen_vectors_2;
    public static double cross_P;
    public static int eig_index_1;
    public static int eig_index_2;
    public static double cross_P_i;
    public static double cross_P_j;
    public static double cross_P_z;
    private Button pelvis_calibration_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pelvis_calibration_btn = (Button) findViewById(R.id.pelvis_calibration_btn);
        pelvis_calibration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPelvis_Calibration_Activity();
            }
        });
    }
    public void openPelvis_Calibration_Activity(){
        Intent intent = new Intent(this, pelvis_calibration.class);
        startActivity(intent);
    }
    public void read_data_1(View v) {
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("rotation_along_Xaxis.xls");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
//            int row = 3505;
//            int col = 9;

            ArrayList<Double> ang_vel_x;
            ArrayList<Double> ang_vel_y;
            ArrayList<Double> ang_vel_z;

            ang_vel_x = new ArrayList<Double>();
            ang_vel_y = new ArrayList<Double>();
            ang_vel_z = new ArrayList<Double>();

            String wx_read = "";
            String wy_read = "";
            String wz_read = "";

            for (int i = 11; i < s.getRows(); i++) {
                Cell x = s.getCell(6, i);
                wx_read = x.getContents();
                ang_vel_x.add(Double.parseDouble(wx_read));

                Cell y = s.getCell(7, i);
                wy_read = y.getContents();
                ang_vel_y.add(Double.parseDouble(wy_read));

                Cell z = s.getCell(8, i);
                wz_read = z.getContents();
                ang_vel_z.add(Double.parseDouble(wz_read));
            }

            double sum_x = 0;
            double sum_y = 0;
            double sum_z = 0;

            /// Getting average for X Y Z
            for (int m = 0; m < ang_vel_x.size(); m++) {
                sum_x = sum_x + ang_vel_x.get(m);
                sum_y = sum_y + ang_vel_y.get(m);
                sum_z = sum_z + ang_vel_z.get(m);
            }
            double mean_wx = sum_x / ang_vel_x.size();
            double mean_wy = sum_y / ang_vel_y.size();
            double mean_wz = sum_z / ang_vel_z.size();

            System.out.println("Ave_x:" + mean_wx);
            System.out.println("Ave_y:" + mean_wy);
            System.out.println("Ave_z:" + mean_wz);
            ///////////////////////////////////////////////////////////////////////////////////////
            /// Getting var for wx wy wz
            ArrayList<Double> wx_num_diff;
            ArrayList<Double> wy_num_diff;
            ArrayList<Double> wz_num_diff;

            wx_num_diff = new ArrayList<Double>();
            wy_num_diff = new ArrayList<Double>();
            wz_num_diff = new ArrayList<Double>();

            double wx_diff = 0;
            double wy_diff = 0;
            double wz_diff = 0;

            for (int i = 0; i < ang_vel_x.size(); i++) {
                wx_diff = Math.pow(ang_vel_x.get(i) - (mean_wx), 2);
                wx_num_diff.add(wx_diff);
                wy_diff = Math.pow(ang_vel_y.get(i) - (mean_wy), 2);
                wy_num_diff.add(wy_diff);
                wz_diff = Math.pow(ang_vel_z.get(i) - (mean_wz), 2);
                wz_num_diff.add(wz_diff);
            }

            double sum_wx_num_diff = 0;
            double sum_wy_num_diff = 0;
            double sum_wz_num_diff = 0;

            for (int i = 0; i < ang_vel_x.size(); i++) {
                sum_wx_num_diff = sum_wx_num_diff + wx_num_diff.get(i);
                sum_wy_num_diff = sum_wy_num_diff + wy_num_diff.get(i);
                sum_wz_num_diff = sum_wz_num_diff + wz_num_diff.get(i);
            }
            double var_wx = sum_wx_num_diff / wx_num_diff.size();
            double var_wy = sum_wy_num_diff / wy_num_diff.size();
            double var_wz = sum_wz_num_diff / wz_num_diff.size();

            System.out.println("Var_wx:" + var_wx);
            System.out.println("Var_wy:" + var_wy);
            System.out.println("Var_wz:" + var_wz);
            /////////////////////////////////////////////////////////////////////////////////////////

            /// Getting the cov for (wx,wy) (wy,wz) (wx,wz)
            ArrayList<Double> wx_wy_num_diff;
            ArrayList<Double> wy_wz_num_diff;
            ArrayList<Double> wx_wz_num_diff;

            wx_wy_num_diff = new ArrayList<>();
            wy_wz_num_diff = new ArrayList<>();
            wx_wz_num_diff = new ArrayList<>();

            double wx_wy_diff = 0;
            double wy_wz_diff = 0;
            double wx_wz_diff = 0;

            for (int i = 0; i < ang_vel_x.size(); i++) {
                wx_wy_diff = (ang_vel_x.get(i) - (mean_wx)) * (ang_vel_y.get(i) - (mean_wy));
                wx_wy_num_diff.add(wx_wy_diff);
                wy_wz_diff = (ang_vel_y.get(i) - (mean_wy)) * (ang_vel_z.get(i) - (mean_wz));
                wy_wz_num_diff.add(wy_wz_diff);
                wx_wz_diff = (ang_vel_x.get(i) - (mean_wx)) * (ang_vel_z.get(i) - (mean_wz));
                wx_wz_num_diff.add(wx_wz_diff);
            }

            double sum_wx_wy_num_diff = 0;
            double sum_wy_wz_num_diff = 0;
            double sum_wx_wz_num_diff = 0;

            for (int i = 0; i < wx_wy_num_diff.size(); i++) {
                sum_wx_wy_num_diff = sum_wx_wy_num_diff + wx_wy_num_diff.get(i);
                sum_wy_wz_num_diff = sum_wy_wz_num_diff + wy_wz_num_diff.get(i);
                sum_wx_wz_num_diff = sum_wx_wz_num_diff + wx_wz_num_diff.get(i);
            }

            double cov_wx_wy = sum_wx_wy_num_diff / wx_wy_num_diff.size();
            double cov_wy_wz = sum_wy_wz_num_diff / wy_wz_num_diff.size();
            double cov_wx_wz = sum_wx_wz_num_diff / wx_wz_num_diff.size();

            System.out.println("Cov_wx_wy:" + cov_wx_wy);
            System.out.println("Cov_wy_wz:" + cov_wy_wz);
            System.out.println("Cov_wx_wz:" + cov_wx_wz);
            ///////////////////////////////////////////////////////////////////////////////////////
            /// Getting the 2D "C" Matrix

            double [][] C_matrix = {{var_wx, cov_wx_wy, cov_wx_wz}, {cov_wx_wy, var_wy, cov_wy_wz}, {cov_wx_wz, cov_wy_wz, var_wz}};
            System.out.println("C_matrix:");
            for (int i = 0; i < C_matrix.length; i++) {
                for (int j = 0; j < C_matrix.length; j++) {
                    System.out.print(C_matrix[i][j] + "   ");
                }
                System.out.print("\n");
            }
            ///////////////////////////////////////////////////////////////////////////////////////
            final DecimalFormat df = new DecimalFormat("0.0000");
//         EigenVectors(Eig_c);


            Matrix Eig_c = new Matrix(C_matrix);
            EigenvalueDecomposition eigen = Eig_c.eig();
//            EigenVectors(Eig_c);
            double [] realPart = eigen.getRealEigenvalues();
            //double [] imagPart = eigen.getImagEigenvalues();
            //display3(realPart.toString());

            for (int i = 0; i < realPart.length; i++) {
                System.out.println("Eigen Value " + i + " is " +
                        "[" + realPart[i] + " ] ");
            }
            /// Now getting the largest eigen value

            double max_eig = 0;
            eig_index_1 = 0;
            for (int i = 0; i < realPart.length; i++) {
                if (realPart[i] > max_eig){
                    max_eig = realPart[i];
                    eig_index_1 = i;
                }
            }
            System.out.println("Max_eig_val:" + max_eig);
            System.out.println("Max_eig_index:" + eig_index_1);

            /// Now printing the eigen vector of the maximum eigen value index

            Matrix evectors = eigen.getV();
            evectors.print(3,3);
//            double [][] Eigen_vectors = evectors.getArray();
            Eigen_vectors_1 = evectors.getArray();
            System.out.println("Eigen Vector: " + df.format(Eigen_vectors_1[0][eig_index_1]) + " i + " + df.format(Eigen_vectors_1[1][eig_index_1])+ " j + " +  df.format(Eigen_vectors_1[2][eig_index_1])+ "k ");
            String axis1 = "Eigen Vector 1: " + df.format(Eigen_vectors_1[0][eig_index_1]) + " i + " + df.format(Eigen_vectors_1[1][eig_index_1])+ " j + " +  df.format(Eigen_vectors_1[2][eig_index_1])+ "k ";
//            TextView z= (TextView) findViewById(R.id.textView_angular_velocity_z);
//            z.setText("Eigen Vector: " + df.format(Eigen_vectors[0][eig_index]) + " i + " + df.format(Eigen_vectors[1][eig_index])+ " j + " +  df.format(Eigen_vectors[2][eig_index])+ "k ");
//            System.out.println("evectors:"+ evectors);
            String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
            String done_command = "Axis 1 achieved";
            display1(done_command);
            display2(c_matrix);
            //display3(wz_read);
            display4(axis1);
        } catch (Exception e){

        }
    }

    //////// Starting for the second axis
    public void read_data_2(View v){
        try{
            AssetManager am = getAssets();
            InputStream is = am.open("rotation_along_Yaxis.xls");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
//            int row = ; ///// Add rows over here
//            int col = ; ///// Add cols here

            ArrayList<Double> ang_vel_x;
            ArrayList<Double> ang_vel_y;
            ArrayList<Double> ang_vel_z;

            ang_vel_x = new ArrayList<Double>();
            ang_vel_y = new ArrayList<Double>();
            ang_vel_z = new ArrayList<Double>();

            String wx_read = "";
            String wy_read = "";
            String wz_read = "";

            for (int i = 11; i <s.getRows(); i++) {
                Cell x = s.getCell(6, i);
                wx_read = x.getContents();
                ang_vel_x.add(Double.parseDouble(wx_read));

                Cell y = s.getCell(7, i);
                wy_read = y.getContents();
                ang_vel_y.add(Double.parseDouble(wy_read));

                Cell z = s.getCell(8, i);
                wz_read = z.getContents();
                ang_vel_z.add(Double.parseDouble(wz_read));
            }
            double sum_x = 0;
            double sum_y = 0;
            double sum_z = 0;

            for (int m = 0; m < ang_vel_x.size(); m++) {
                sum_x = sum_x + ang_vel_x.get(m);
                sum_y = sum_y + ang_vel_y.get(m);
                sum_z = sum_z + ang_vel_z.get(m);
            }
            double mean_wx = sum_x / ang_vel_x.size();
            double mean_wy = sum_y / ang_vel_y.size();
            double mean_wz = sum_z / ang_vel_z.size();

            System.out.println("Ave_x:" + mean_wx);
            System.out.println("Ave_y:" + mean_wy);
            System.out.println("Ave_z:" + mean_wz);


            /// Getting var for wx wy wz
            ArrayList<Double> wx_num_diff;
            ArrayList<Double> wy_num_diff;
            ArrayList<Double> wz_num_diff;

            wx_num_diff = new ArrayList<Double>();
            wy_num_diff = new ArrayList<Double>();
            wz_num_diff = new ArrayList<Double>();

            double wx_diff = 0;
            double wy_diff = 0;
            double wz_diff = 0;

            for (int i = 0; i < ang_vel_x.size(); i++) {
                wx_diff = Math.pow(ang_vel_x.get(i) - (mean_wx), 2);
                wx_num_diff.add(wx_diff);
                wy_diff = Math.pow(ang_vel_y.get(i) - (mean_wy), 2);
                wy_num_diff.add(wy_diff);
                wz_diff = Math.pow(ang_vel_z.get(i) - (mean_wz), 2);
                wz_num_diff.add(wz_diff);
            }

            double sum_wx_num_diff = 0;
            double sum_wy_num_diff = 0;
            double sum_wz_num_diff = 0;

            for (int i = 0; i < ang_vel_x.size(); i++) {
                sum_wx_num_diff = sum_wx_num_diff + wx_num_diff.get(i);
                sum_wy_num_diff = sum_wy_num_diff + wy_num_diff.get(i);
                sum_wz_num_diff = sum_wz_num_diff + wz_num_diff.get(i);
            }
            double var_wx = sum_wx_num_diff / wx_num_diff.size();
            double var_wy = sum_wy_num_diff / wy_num_diff.size();
            double var_wz = sum_wz_num_diff / wz_num_diff.size();

            System.out.println("Var_wx:" + var_wx);
            System.out.println("Var_wy:" + var_wy);
            System.out.println("Var_wz:" + var_wz);

            /////////////////////////////////////////////////////////////////////////////////////////

            /// Getting the cov for (wx,wy) (wy,wz) (wx,wz)
            ArrayList<Double> wx_wy_num_diff;
            ArrayList<Double> wy_wz_num_diff;
            ArrayList<Double> wx_wz_num_diff;

            wx_wy_num_diff = new ArrayList<>();
            wy_wz_num_diff = new ArrayList<>();
            wx_wz_num_diff = new ArrayList<>();

            double wx_wy_diff = 0;
            double wy_wz_diff = 0;
            double wx_wz_diff = 0;

            for (int i = 0; i < ang_vel_x.size(); i++) {
                wx_wy_diff = (ang_vel_x.get(i) - (mean_wx)) * (ang_vel_y.get(i) - (mean_wy));
                wx_wy_num_diff.add(wx_wy_diff);
                wy_wz_diff = (ang_vel_y.get(i) - (mean_wy)) * (ang_vel_z.get(i) - (mean_wz));
                wy_wz_num_diff.add(wy_wz_diff);
                wx_wz_diff = (ang_vel_x.get(i) - (mean_wx)) * (ang_vel_z.get(i) - (mean_wz));
                wx_wz_num_diff.add(wx_wz_diff);
            }

            double sum_wx_wy_num_diff = 0;
            double sum_wy_wz_num_diff = 0;
            double sum_wx_wz_num_diff = 0;

            for (int i = 0; i < wx_wy_num_diff.size(); i++) {
                sum_wx_wy_num_diff = sum_wx_wy_num_diff + wx_wy_num_diff.get(i);
                sum_wy_wz_num_diff = sum_wy_wz_num_diff + wy_wz_num_diff.get(i);
                sum_wx_wz_num_diff = sum_wx_wz_num_diff + wx_wz_num_diff.get(i);
            }

            double cov_wx_wy = sum_wx_wy_num_diff / wx_wy_num_diff.size();
            double cov_wy_wz = sum_wy_wz_num_diff / wy_wz_num_diff.size();
            double cov_wx_wz = sum_wx_wz_num_diff / wx_wz_num_diff.size();

            System.out.println("Cov_wx_wy:" + cov_wx_wy);
            System.out.println("Cov_wy_wz:" + cov_wy_wz);
            System.out.println("Cov_wx_wz:" + cov_wx_wz);
            ///////////////////////////////////////////////////////////////////////////////////////
            /// Getting the 2D "C" Matrix

            double [][] C_matrix = {{var_wx, cov_wx_wy, cov_wx_wz}, {cov_wx_wy, var_wy, cov_wy_wz}, {cov_wx_wz, cov_wy_wz, var_wz}};
            System.out.println("C_matrix:");
            for (int i = 0; i < C_matrix.length; i++) {
                for (int j = 0; j < C_matrix.length; j++) {
                    System.out.print(C_matrix[i][j] + "   ");
                }
                System.out.print("\n");
            }
            ///////////////////////////////////////////////////////////////////////////////////////
            final DecimalFormat df = new DecimalFormat("0.0000");
//         EigenVectors(Eig_c);

            Matrix Eig_c = new Matrix(C_matrix);
            EigenvalueDecomposition eigen = Eig_c.eig();
//            EigenVectors(Eig_c);
            double [] realPart = eigen.getRealEigenvalues();
            //double [] imagPart = eigen.getImagEigenvalues();
            //display3(realPart.toString());

            for (int i = 0; i < realPart.length; i++) {
                System.out.println("Eigen Value " + i + " is " +
                        "[" + realPart[i] + " ] ");
            }
            /// Now getting the largest eigen value

            double max_eig = 0;
            eig_index_2 = 0;
            for (int i = 0; i < realPart.length; i++) {
                if (realPart[i] > max_eig){
                    max_eig = realPart[i];
                    eig_index_2 = i;
                }
            }
            System.out.println("Max_eig_val:" + max_eig);
            System.out.println("Max_eig_index:" + eig_index_2);

            /// Now printing the eigen vector of the maximum eigen value index

            Matrix evectors = eigen.getV();
            evectors.print(3,3);
//            double [][] Eigen_vectors_2 = evectors.getArray();
            Eigen_vectors_2 = evectors.getArray();
            System.out.println("Eigen Vector: " + df.format(Eigen_vectors_2[0][eig_index_2]) + " i + " + df.format(Eigen_vectors_2[1][eig_index_2])+ " j + " +  df.format(Eigen_vectors_2[2][eig_index_2])+ "k ");
            String axis2 = "Eigen Vector 2: " + df.format(Eigen_vectors_2[0][eig_index_2]) + " i + " + df.format(Eigen_vectors_2[1][eig_index_2])+ " j + " +  df.format(Eigen_vectors_2[2][eig_index_2])+ "k ";
//            TextView z= (TextView) findViewById(R.id.textView_angular_velocity_z);
//            z.setText("Eigen Vector: " + df.format(Eigen_vectors_2[0][eig_index]) + " i + " + df.format(Eigen_vectors_2[1][eig_index])+ " j + " +  df.format(Eigen_vectors_2[2][eig_index])+ "k ");
//            System.out.println("evectors:"+ evectors);
            String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
            String done_command = "Axis 2 achieved";
            display1(done_command);
//           display2(c_matrix);
            display3(c_matrix);
            display5(axis2);
        } catch (Exception e){

        }
    }

    /////////////////////////////// Performing cross product
    public void CrossProduct(View v){
        cross_P_i = (Eigen_vectors_1[1][eig_index_1]*Eigen_vectors_2[2][eig_index_2] - Eigen_vectors_1[2][eig_index_1]*Eigen_vectors_2[1][eig_index_2]);
        cross_P_j = (Eigen_vectors_1[2][eig_index_1]*Eigen_vectors_2[0][eig_index_2] - Eigen_vectors_1[0][eig_index_1]*Eigen_vectors_2[2][eig_index_2]);
        cross_P_z = (Eigen_vectors_1[0][eig_index_1]*Eigen_vectors_2[1][eig_index_2] - Eigen_vectors_1[1][eig_index_1]*Eigen_vectors_2[0][eig_index_2]);
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("Axis 3: " + df.format(cross_P_i) + " i + " + df.format(cross_P_j) + " j + " + df.format(cross_P_z) + " z ");
        String axis3 = "Axis 3: " + df.format(cross_P_i) + " i + " + df.format(cross_P_j) + " j + " + df.format(cross_P_z) + " z ";
        display6(axis3);
    }



    public void EigenVectors(Matrix InputMatrix)
    {
        EigenvalueDecomposition somematrix = new EigenvalueDecomposition(InputMatrix);
        Matrix S = somematrix.getV();
        System.out.println("V = " + S);
    }

    public void display1(String value) //reading the excel file and displaying
    {
        TextView x= (TextView) findViewById(R.id.textView_angular_velocity_x);
        x.setText(value);
    }
    public void display2(String value) //reading the excel file and displaying
    {
        TextView y= (TextView) findViewById(R.id.textview_angular_velocity_y);
        y.setText(value);
    }
    public void display3(String value) //reading the excel file and displaying
    {
        TextView z= (TextView) findViewById(R.id.textView_angular_velocity_z);
        z.setText(value);
    }
    public void display4(String value) //reading the excel file and displaying
    {
        TextView x= (TextView) findViewById(R.id.axis_1);
        x.setText(value);
    }
    public void display5(String value) //reading the excel file and displaying
    {
        TextView x= (TextView) findViewById(R.id.axis_2);
        x.setText(value);
    }
    public void display6(String value) //reading the excel file and displaying
    {
        TextView x= (TextView) findViewById(R.id.axis_3);
        x.setText(value);
    }
}