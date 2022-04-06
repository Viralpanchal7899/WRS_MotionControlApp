package com.example.motioncontrolcalibration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class pelvis_calibration extends AppCompatActivity {

    private TextView action_textview;
    private Button z_axis_btn;
    private Button y_axis_btn;
    private Button x_axis_btn;
    private Button start_measurement_btn;
    private Button stop_measurement_btn;
    private Button thigh_calibration_btn;
    private static double [][] Eigen_vectors_1;
    private static double [][] Eigen_vectors_2;
    private int eig_index_1;
    private int eig_index_2;
    private static double cross_P_i;
    private static double cross_P_j;
    private static double cross_P_z;
    private static String axis1;
    private static String axis2;
    private static String axis3;
    private static String R_IMU_Pelvis;
    private int count = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelvis_calibration);

        z_axis_btn = (Button) findViewById(R.id.z_axis_btn);
        z_axis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    pelvis_calibration_z_dialog pelvis_calibration_z_dialog = new pelvis_calibration_z_dialog();
                    pelvis_calibration_z_dialog.show(getSupportFragmentManager(),"pelvis_begin_dialog");
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Press Start to initiate ");
                    count = 1;
            }
        });

        y_axis_btn = (Button) findViewById(R.id.y_axis_btn);
        y_axis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    pelvis_calibration_y_dialog pelvis_calibration_y_dialog = new pelvis_calibration_y_dialog();
                    pelvis_calibration_y_dialog.show(getSupportFragmentManager(),"pelvis_calibration_y_dialog");
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Press Start to initiate");
                    count = 2;
            }
        });

        x_axis_btn = (Button) findViewById(R.id.x_axis_btn);
        x_axis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    pelvis_calibration_x_dialog pelvis_calibration_x_dialog = new pelvis_calibration_x_dialog();
                    pelvis_calibration_x_dialog.show(getSupportFragmentManager(), "pelvis_calibration_x_dialog");
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Press Start to initiate ");
                    count = 3;
            }
        });

        start_measurement_btn = (Button) findViewById(R.id.start_measurement_btn);
        start_measurement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 1){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Measuring for Pelvis Z Axis");
                }
                else if (count == 2){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Measuring for Pelvis Y Axis");
                }
                else if (count == 3) {
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Pelvis X Axis determined.");
                    CrossProduct();
                    action_textview = (TextView) findViewById(R.id.axis_3);
                    action_textview.setText(axis3);
                }

            }
        });

        stop_measurement_btn = (Button) findViewById(R.id.stop_measurement_btn);
        stop_measurement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 1) {
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Pelvis Z axis determined");
                    read_data_1();
                    action_textview = (TextView) findViewById(R.id.axis_1);
                    action_textview.setText(axis1);
                }
                else if (count == 2) {
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Pelvis Y axis determined");
                    read_data_2();
                    action_textview = (TextView) findViewById(R.id.axis_2);
                    action_textview.setText(axis2);
                }
                else if (count == 3) {
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Pelvis X Axis determined");
                    R_IMU_Pelvis();
                    action_textview = (TextView) findViewById(R.id.r_imu_pelvis);
                    action_textview.setText(R_IMU_Pelvis);
                    thigh_calibration_btn = (Button) findViewById(R.id.thigh_calibration_btn);
                    thigh_calibration_btn.setVisibility(View.VISIBLE);
                }
            }
        });

        thigh_calibration_btn = (Button) findViewById(R.id.thigh_calibration_btn);
        thigh_calibration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openThigh_Calibration_Activity();
            }
        });
    }

    public void openThigh_Calibration_Activity(){
        Intent intent2 = new Intent(this, thigh_calibration.class);
        startActivity(intent2);
    }

        public void read_data_1() {
            try {
                AssetManager am = getAssets();
                InputStream is = am.open("rotation_along_Xaxis.xls");
                Workbook wb = Workbook.getWorkbook(is);
                Sheet s = wb.getSheet(0);

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
                Matrix Eig_c = new Matrix(C_matrix);
                EigenvalueDecomposition eigen = Eig_c.eig();
                double [] realPart = eigen.getRealEigenvalues();

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
                Eigen_vectors_1 = evectors.getArray();
                System.out.println("Eigen Vector: " + df.format(Eigen_vectors_1[0][eig_index_1]) + " i + " + df.format(Eigen_vectors_1[1][eig_index_1])+ " j + " +  df.format(Eigen_vectors_1[2][eig_index_1])+ "k ");
                axis1 = "Vector 1: " + df.format(Eigen_vectors_1[0][eig_index_1]) + " i + " + df.format(Eigen_vectors_1[1][eig_index_1])+ " j + " +  df.format(Eigen_vectors_1[2][eig_index_1])+ "k ";
                String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
                String done_command = "Axis 1 achieved";
//                display1(done_command);
//                display2(c_matrix);
//                display4(axis1);
            } catch (Exception e){

            }
        }

        public void read_data_2(){
            try{
                AssetManager am = getAssets();
                InputStream is = am.open("rotation_along_Yaxis.xls");
                Workbook wb = Workbook.getWorkbook(is);
                Sheet s = wb.getSheet(0);

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
                Matrix Eig_c = new Matrix(C_matrix);
                EigenvalueDecomposition eigen = Eig_c.eig();
                double [] realPart = eigen.getRealEigenvalues();

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
                Eigen_vectors_2 = evectors.getArray();
                System.out.println("Eigen Vector: " + df.format(Eigen_vectors_2[0][eig_index_2]) + " i + " + df.format(Eigen_vectors_2[1][eig_index_2])+ " j + " +  df.format(Eigen_vectors_2[2][eig_index_2])+ "k ");
                axis2 = "Vector 2: " + df.format(Eigen_vectors_2[0][eig_index_2]) + " i + " + df.format(Eigen_vectors_2[1][eig_index_2])+ " j + " +  df.format(Eigen_vectors_2[2][eig_index_2])+ "k ";
                String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
                String done_command = "Axis 2 achieved";
//                display1(done_command);
//                display3(c_matrix);
//                display5(axis2);
            } catch (Exception e){

            }
        }

        /////////////////////////////// Performing cross product
    public void CrossProduct(){
        cross_P_i = (Eigen_vectors_1[1][eig_index_1]*Eigen_vectors_2[2][eig_index_2] - Eigen_vectors_1[2][eig_index_1]*Eigen_vectors_2[1][eig_index_2]);
        cross_P_j = (Eigen_vectors_1[2][eig_index_1]*Eigen_vectors_2[0][eig_index_2] - Eigen_vectors_1[0][eig_index_1]*Eigen_vectors_2[2][eig_index_2]);
        cross_P_z = (Eigen_vectors_1[0][eig_index_1]*Eigen_vectors_2[1][eig_index_2] - Eigen_vectors_1[1][eig_index_1]*Eigen_vectors_2[0][eig_index_2]);
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("Vector 3: " + df.format(cross_P_i) + " i + " + df.format(cross_P_j) + " j + " + df.format(cross_P_z) + " z ");
        axis3 = "Vector 3: " + df.format(cross_P_i) + " i + " + df.format(cross_P_j) + " j + " + df.format(cross_P_z) + " z ";
//        display6(axis3);
    }

    public void R_IMU_Pelvis(){
        final DecimalFormat df = new DecimalFormat("0.0000");
        R_IMU_Pelvis = "R = [ " + df.format(Eigen_vectors_1[0][eig_index_1]) + "   " + df.format(Eigen_vectors_1[1][eig_index_1]) + "   " + df.format(Eigen_vectors_1[2][eig_index_1]) + " \n " + "     " +  df.format(Eigen_vectors_1[2][eig_index_1]) + "   " + df.format(Eigen_vectors_2[1][eig_index_2]) + "   " + df.format(Eigen_vectors_2[2][eig_index_2]) + " \n " + "     " + df.format(cross_P_i) + "   " + df.format(cross_P_j) + "   " + df.format(cross_P_z) + " ]";
    }

    public void openDialog(){
        pelvis_calibration_z_dialog pelvis_calibration_z_dialog = new pelvis_calibration_z_dialog();
        pelvis_calibration_z_dialog.show(getSupportFragmentManager(),"pelvis_begin_dialog");
    }

}