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

public class global_vectors extends AppCompatActivity{

    private TextView action_textview;
    private Button y_axis_btn;
    private Button start_measurement_btn;
    private Button RelativeTransformationMatrix_btn;
    private int eig_index_pelvis_y;
    private int eig_index_grt_y;
    private int eig_index_glt_y;
    private static int eig_index_pelvis_z;
    private static int eig_index_RT_z;
    private static int eig_index_LT_z;
    private static double [][] Eigen_vectors_pelvis_y;
    private static double [][] Eigen_vectors_grt_y;
    private static double [][] Eigen_vectors_glt_y;
    private static double [][] Eigen_vectors_pelvis_z;
    private static double [][] Eigen_vectors_RT_z;
    private static double [][] Eigen_vectors_LT_z;
    private static double cross_P_i_Gpelvis;
    private static double cross_P_j_Gpelvis;
    private static double cross_P_z_Gpelvis;
    private static double cross_P_i_Gpelvis_K;
    private static double cross_P_j_Gpelvis_K;
    private static double cross_P_z_Gpelvis_K;
    private static double cross_P_z_GRT_K;
    private static double cross_P_i_GRT_K;
    private static double cross_P_j_GRT_K;
    private static double cross_P_i_GLT_K;
    private static double cross_P_j_GLT_K;
    private static double cross_P_z_GLT_K;
    private static double cross_P_i_GRT;
    private static double cross_P_j_GRT;
    private static double cross_P_z_GRT;
    private static double cross_P_i_GLT;
    private static double cross_P_j_GLT;
    private static double cross_P_z_GLT;
    private static String axis_pelvis_y;
    private static String axis_grt_y;
    private static String axis_glt_y;
    private static String pelvis_z;
    private static String pelvis_global_i_vec;
    private static String pelvis_global_k_vec;
    private static String RT_global_k_vec;
    private static String LT_global_k_vec;
    private static String RT_global_i_vec;
    private static String LT_global_i_vec;
    private static String rt_global_i_vec;
    private static String lt_global_i_vec;
    private static String R_IMU_Pelvis_Global;
    private static double [][] r_imu_pelvis_global;
    private int count = 1;
    private boolean count_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_vectors);

        y_axis_btn = (Button) findViewById(R.id.z_axis_btn);
        y_axis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global_vectors_z_dialog global_vectors_z_dialog = new global_vectors_z_dialog();
                global_vectors_z_dialog.show(getSupportFragmentManager(), "global_vectors_z_dialog");
                action_textview = (TextView) findViewById(R.id.action_textview);
                action_textview.setText(" Press START to initiate");
                count = 1;
            }
        });

        start_measurement_btn = (Button) findViewById(R.id.start_measurement_btn);
        start_measurement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    counter();
                    if (count_status = true) {
                        action_textview = (TextView) findViewById(R.id.action_textview);
                        action_textview.setText(" Global z Axis for all the IMUs achieved.");
                        read_data_Gpelvis_y();
                        action_textview = (TextView) findViewById(R.id.axis_global_pelvis_Y);
                        action_textview.setText(axis_pelvis_y);
                        read_data_GRT_y();
                        action_textview = (TextView) findViewById(R.id.axis_global_RT_Y);
                        action_textview.setText(axis_grt_y);
                        read_data_GLT_y();
                        action_textview = (TextView) findViewById(R.id.axis_global_LT_Y);
                        action_textview.setText(axis_glt_y);
                        get_pelvis_z();
                        get_RT_z();
                        get_LT_z();

                        global_pelvis_CP_i_vec();
                        global_RT_CP_i_vec();
                        global_LT_CP_i_vec();

                        global_pelvis_k_vec();
                        action_textview = (TextView)findViewById(R.id.axis_global_pelvis_z);
                        action_textview.setText(pelvis_global_k_vec);
                        global_RT_k_vec();
                        action_textview = (TextView) findViewById(R.id.axis_global_RT_z);
                        action_textview.setText(RT_global_k_vec);
                        global_LT_k_vec();
                        action_textview = (TextView) findViewById(R.id.axis_global_LT_z);
                        action_textview.setText(LT_global_k_vec);
                    }
            }
        });
        RelativeTransformationMatrix_btn = (Button) findViewById(R.id.RTM_activity);
        RelativeTransformationMatrix_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRelativeTransformationMatrix_Activity();
            }
        });
    }
    public void openRelativeTransformationMatrix_Activity(){
        Intent intent = new Intent(this,RelativeTransformationMatrices.class);
        startActivity(intent);
    }
    public void read_data_Gpelvis_y(){
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens DOT_Gpelvis_Y.xls");
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
            eig_index_pelvis_y = 0;
            for (int i = 0; i < realPart.length; i++) {
                if (realPart[i] > max_eig){
                    max_eig = realPart[i];
                    eig_index_pelvis_y = i;
                }
            }
            System.out.println("Max_eig_val:" + max_eig);
            System.out.println("Max_eig_index:" + eig_index_pelvis_y);

            /// Now printing the eigen vector of the maximum eigen value index
            Matrix evectors = eigen.getV();
            evectors.print(3,3);
            Eigen_vectors_pelvis_y = evectors.getArray();
            System.out.println("Pelvis Z: " + df.format(-1 * Eigen_vectors_pelvis_y[0][eig_index_pelvis_y]) + " i + " + df.format(-1 * Eigen_vectors_pelvis_y[1][eig_index_pelvis_y])+ " j + " +  df.format(-1 * Eigen_vectors_pelvis_y[2][eig_index_pelvis_y])+ "k ");
            axis_pelvis_y = "Pelvis Z: " + df.format(-1* Eigen_vectors_pelvis_y[0][eig_index_pelvis_y]) + " i + " + df.format(-1 * Eigen_vectors_pelvis_y[1][eig_index_pelvis_y])+ " j + " +  df.format(-1 * Eigen_vectors_pelvis_y[2][eig_index_pelvis_y])+ "k ";
            String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
            String done_command = "Axis 1 achieved";
//                display1(done_command);
//                display2(c_matrix);
//                display4(axis1);
        } catch (Exception e){

        }
    }

    public void read_data_GRT_y() {
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens DOT_GRT_Y.xls");
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
            eig_index_grt_y = 0;
            for (int i = 0; i < realPart.length; i++) {
                if (realPart[i] > max_eig){
                    max_eig = realPart[i];
                    eig_index_grt_y = i;
                }
            }
            System.out.println("Max_eig_val:" + max_eig);
            System.out.println("Max_eig_index:" + eig_index_grt_y);

            /// Now printing the eigen vector of the maximum eigen value index
            Matrix evectors = eigen.getV();
            evectors.print(3,3);
            Eigen_vectors_grt_y = evectors.getArray();
            System.out.println("RT Z: " + df.format(-1 * Eigen_vectors_grt_y[0][eig_index_grt_y]) + " i + " + df.format(-1 * Eigen_vectors_grt_y[1][eig_index_grt_y])+ " j + " +  df.format(-1 * Eigen_vectors_grt_y[2][eig_index_grt_y])+ "k ");
            axis_grt_y = "RT Z: " + df.format(-1 * Eigen_vectors_grt_y[0][eig_index_grt_y]) + " i + " + df.format(-1 * Eigen_vectors_grt_y[1][eig_index_grt_y])+ " j + " +  df.format(-1 * Eigen_vectors_grt_y[2][eig_index_grt_y])+ "k ";
            String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
            String done_command = "Axis 1 achieved";
//                display1(done_command);
//                display2(c_matrix);
//                display4(axis1);
        } catch (Exception e){

        }
    }

    public void read_data_GLT_y(){
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens DOT_GLT_Y.xls");
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
            eig_index_glt_y = 0;
            for (int i = 0; i < realPart.length; i++) {
                if (realPart[i] > max_eig){
                    max_eig = realPart[i];
                    eig_index_glt_y = i;
                }
            }
            System.out.println("Max_eig_val:" + max_eig);
            System.out.println("Max_eig_index:" + eig_index_glt_y);

            /// Now printing the eigen vector of the maximum eigen value index
            Matrix evectors = eigen.getV();
            evectors.print(3,3);
            Eigen_vectors_glt_y = evectors.getArray();
            System.out.println("LT Z: " + df.format(-1 * Eigen_vectors_glt_y[0][eig_index_glt_y]) + " i + " + df.format(-1 * Eigen_vectors_glt_y[1][eig_index_glt_y])+ " j + " +  df.format(-1 * Eigen_vectors_glt_y[2][eig_index_glt_y])+ "k ");
            axis_glt_y = "LT Z: " + df.format(-1 * Eigen_vectors_glt_y[0][eig_index_glt_y]) + " i + " + df.format(-1 * Eigen_vectors_glt_y[1][eig_index_glt_y])+ " j + " +  df.format(-1 * Eigen_vectors_glt_y[2][eig_index_glt_y])+ "k ";
            String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
            String done_command = "Axis 1 achieved";
//                display1(done_command);
//                display2(c_matrix);
//                display4(axis1);
        } catch (Exception e){

        }
    }

    public void get_pelvis_z(){
        final DecimalFormat df = new DecimalFormat("0.0000");
        eig_index_pelvis_z = pelvis_calibration.send_pelvis_eig_index_z();
        Eigen_vectors_pelvis_z = pelvis_calibration.send_pelvis_z_vector();
        System.out.println("Pelvis Z: " + df.format(Eigen_vectors_pelvis_z[0][eig_index_pelvis_z]) + " i + " + df.format(Eigen_vectors_pelvis_z[1][eig_index_pelvis_z])+ " j + " +  df.format(Eigen_vectors_pelvis_z[2][eig_index_pelvis_z])+ "k ");
        pelvis_z = "Pelvis Z: " + df.format(Eigen_vectors_pelvis_z[0][eig_index_pelvis_z]) + " i + " + df.format(Eigen_vectors_pelvis_z[1][eig_index_pelvis_z])+ " j + " +  df.format(Eigen_vectors_pelvis_z[2][eig_index_pelvis_z])+ "k ";
    }

    public void global_pelvis_CP_i_vec(){
        cross_P_i_Gpelvis = (-1 * Eigen_vectors_pelvis_y[1][eig_index_pelvis_y]*Eigen_vectors_pelvis_z[2][eig_index_pelvis_z] + Eigen_vectors_pelvis_y[2][eig_index_pelvis_y]*Eigen_vectors_pelvis_z[1][eig_index_pelvis_z]);
        cross_P_j_Gpelvis = (-1 * Eigen_vectors_pelvis_y[2][eig_index_pelvis_y]*Eigen_vectors_pelvis_z[0][eig_index_pelvis_z] + Eigen_vectors_pelvis_y[0][eig_index_pelvis_y]*Eigen_vectors_pelvis_z[2][eig_index_pelvis_z]);
        cross_P_z_Gpelvis = (-1 * Eigen_vectors_pelvis_y[0][eig_index_pelvis_y]*Eigen_vectors_pelvis_z[1][eig_index_pelvis_z] + Eigen_vectors_pelvis_y[1][eig_index_pelvis_y]*Eigen_vectors_pelvis_z[0][eig_index_pelvis_z]);
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("Vector 3: " + df.format(cross_P_i_Gpelvis) + " i + " + df.format(cross_P_j_Gpelvis) + " j + " + df.format(cross_P_z_Gpelvis) + " z ");
        pelvis_global_i_vec = "Vector 3: " + df.format(cross_P_i_Gpelvis) + " i + " + df.format(cross_P_j_Gpelvis) + " j + " + df.format(cross_P_z_Gpelvis) + " z ";
    }

    public void R_IMU_Pelvis_Global(){
        final DecimalFormat df = new DecimalFormat("0.0000");
        R_IMU_Pelvis_Global = "R = [ " + df.format(-1* Eigen_vectors_pelvis_y[0][eig_index_pelvis_y]) + " " + df.format(-1 * Eigen_vectors_pelvis_y[1][eig_index_pelvis_y])+ " " +  df.format(-1 * Eigen_vectors_pelvis_y[2][eig_index_pelvis_y])+ " \n " + " " + df.format(Eigen_vectors_pelvis_z[0][eig_index_pelvis_z]) + " " + df.format(Eigen_vectors_pelvis_z[1][eig_index_pelvis_z])+ " " +  df.format(Eigen_vectors_pelvis_z[2][eig_index_pelvis_z])+ " \n " + " " + df.format(cross_P_i_Gpelvis) + " " + df.format(cross_P_j_Gpelvis) + " " + df.format(cross_P_z_Gpelvis) + " ]";
        r_imu_pelvis_global = new double[][]{{Double.valueOf(df.format(-1* Eigen_vectors_pelvis_y[0][eig_index_pelvis_y])), Double.valueOf(df.format(-1 * Eigen_vectors_pelvis_y[1][eig_index_pelvis_y])),Double.valueOf(df.format(-1 * Eigen_vectors_pelvis_y[2][eig_index_pelvis_y]))},{Double.valueOf(df.format(Eigen_vectors_pelvis_z[0][eig_index_pelvis_z])),Double.valueOf(df.format(Eigen_vectors_pelvis_z[1][eig_index_pelvis_z])),Double.valueOf(df.format(Eigen_vectors_pelvis_z[2][eig_index_pelvis_z]))},{Double.valueOf(df.format(cross_P_i_Gpelvis)),Double.valueOf(df.format(cross_P_j_Gpelvis)),Double.valueOf(df.format(cross_P_z_Gpelvis))}};
    }

    public static double[][] send_r_imu_pelvis_global(){
        return r_imu_pelvis_global;
    }

    public void get_RT_z(){
        final DecimalFormat df = new DecimalFormat("0.0000");
        eig_index_RT_z = thigh_calibration.send_RT_eig_index_z();
        Eigen_vectors_RT_z = thigh_calibration.send_RT_z_vector();
        System.out.println("Eigen Vector R: " + df.format(Eigen_vectors_RT_z[0][eig_index_RT_z]) + " i + " + df.format(Eigen_vectors_RT_z[1][eig_index_RT_z])+ " j + " +  df.format(Eigen_vectors_RT_z[2][eig_index_RT_z])+ "k ");
        rt_global_i_vec = "Eigen Vector R: " + df.format(Eigen_vectors_RT_z[0][eig_index_RT_z]) + " i + " + df.format(Eigen_vectors_RT_z[1][eig_index_RT_z])+ " j + " +  df.format(Eigen_vectors_RT_z[2][eig_index_RT_z])+ "k ";
    }

    public void get_LT_z(){
        final DecimalFormat df = new DecimalFormat("0.0000");
        eig_index_LT_z = thigh_calibration.send_LT_eig_index_z();
        Eigen_vectors_LT_z = thigh_calibration.send_LT_z_vector();
        System.out.println("pelvis_i_vec: " + df.format(Eigen_vectors_LT_z[0][eig_index_LT_z]) + " i + " + df.format(Eigen_vectors_LT_z[1][eig_index_LT_z])+ " j + " +  df.format(Eigen_vectors_LT_z[2][eig_index_LT_z])+ "k ");
        lt_global_i_vec = "pelvis_i_vec: " + df.format(Eigen_vectors_LT_z[0][eig_index_LT_z]) + " i + " + df.format(Eigen_vectors_LT_z[1][eig_index_LT_z])+ " j + " +  df.format(Eigen_vectors_LT_z[2][eig_index_LT_z])+ "k ";
    }

    public void global_RT_CP_i_vec(){
        cross_P_i_GRT = (-1 * Eigen_vectors_grt_y[1][eig_index_grt_y]*Eigen_vectors_RT_z[2][eig_index_RT_z] + Eigen_vectors_grt_y[2][eig_index_grt_y]*Eigen_vectors_RT_z[1][eig_index_RT_z]);
        cross_P_j_GRT = (-1 * Eigen_vectors_grt_y[2][eig_index_grt_y]*Eigen_vectors_RT_z[0][eig_index_RT_z] + Eigen_vectors_grt_y[0][eig_index_grt_y]*Eigen_vectors_RT_z[2][eig_index_RT_z]);
        cross_P_z_GRT = (-1 * Eigen_vectors_grt_y[0][eig_index_grt_y]*Eigen_vectors_RT_z[1][eig_index_RT_z] + Eigen_vectors_grt_y[1][eig_index_grt_y]*Eigen_vectors_RT_z[0][eig_index_RT_z]);
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("RT_i_vec: " + df.format(cross_P_i_GRT) + " i + " + df.format(cross_P_j_GRT) + " j + " + df.format(cross_P_z_GRT) + " z ");
        RT_global_i_vec = "RT_i_vec: " + df.format(cross_P_i_GRT) + " i + " + df.format(cross_P_j_GRT) + " j + " + df.format(cross_P_z_GRT) + " z ";
    }

    public void global_LT_CP_i_vec(){
        cross_P_i_GLT = (-1 * Eigen_vectors_glt_y[1][eig_index_glt_y]*Eigen_vectors_LT_z[2][eig_index_LT_z] + Eigen_vectors_glt_y[2][eig_index_glt_y]*Eigen_vectors_LT_z[1][eig_index_LT_z]);
        cross_P_j_GLT = (-1 * Eigen_vectors_glt_y[2][eig_index_glt_y]*Eigen_vectors_LT_z[0][eig_index_LT_z] + Eigen_vectors_glt_y[0][eig_index_glt_y]*Eigen_vectors_LT_z[2][eig_index_LT_z]);
        cross_P_z_GLT = (-1 * Eigen_vectors_glt_y[0][eig_index_glt_y]*Eigen_vectors_LT_z[1][eig_index_LT_z] + Eigen_vectors_glt_y[1][eig_index_glt_y]*Eigen_vectors_LT_z[0][eig_index_LT_z]);
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("LT_i_vec: " + df.format(cross_P_i_GLT) + " i + " + df.format(cross_P_j_GLT) + " j + " + df.format(cross_P_z_GLT) + " z ");
        LT_global_i_vec = "LT_i_vec: " + df.format(cross_P_i_GLT) + " i + " + df.format(cross_P_j_GLT) + " j + " + df.format(cross_P_z_GLT) + " z ";
    }

    public void global_pelvis_k_vec(){
        cross_P_i_Gpelvis_K = ((cross_P_j_Gpelvis * (-1) * (Eigen_vectors_pelvis_y[2][eig_index_pelvis_y])) - (cross_P_z_Gpelvis * (-1) * Eigen_vectors_pelvis_y[1][eig_index_pelvis_y]));
        cross_P_j_Gpelvis_K = ((cross_P_z_Gpelvis * (-1) * (Eigen_vectors_pelvis_y[0][eig_index_pelvis_y])) - (cross_P_i_Gpelvis * (-1) * Eigen_vectors_pelvis_y[2][eig_index_pelvis_y]));
        cross_P_z_Gpelvis_K = ((cross_P_i_Gpelvis * (-1) * (Eigen_vectors_pelvis_y[1][eig_index_pelvis_y])) - (cross_P_j_Gpelvis * (-1) * Eigen_vectors_pelvis_y[0][eig_index_pelvis_y]));
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("Gpelvis_k_vec: " + df.format(cross_P_i_Gpelvis_K) + " i + " + df.format(cross_P_j_Gpelvis_K) + " j + " + df.format(cross_P_z_Gpelvis_K) + " z ");
        pelvis_global_k_vec = "Gpelvis_k_vec: " + df.format(cross_P_i_Gpelvis_K) + " i + " + df.format(cross_P_j_Gpelvis_K) + " j + " + df.format(cross_P_z_Gpelvis_K) + " z ";
    }

   public void global_RT_k_vec(){
        cross_P_i_GRT_K = ((cross_P_j_GRT * (-1) * (Eigen_vectors_grt_y[2][eig_index_grt_y])) - (cross_P_z_GRT * (-1) * Eigen_vectors_grt_y[1][eig_index_grt_y]));
        cross_P_j_GRT_K = ((cross_P_z_GRT * (-1) * (Eigen_vectors_grt_y[0][eig_index_grt_y])) - (cross_P_i_GRT * (-1) * Eigen_vectors_grt_y[2][eig_index_grt_y]));
        cross_P_z_GRT_K = ((cross_P_i_GRT * (-1) * (Eigen_vectors_grt_y[1][eig_index_grt_y])) - (cross_P_j_GRT * (-1) * Eigen_vectors_grt_y[0][eig_index_grt_y]));
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("GRT_k_vec: " + df.format(cross_P_i_GRT_K) + " i + " + df.format(cross_P_j_GRT_K) + " j + " + df.format(cross_P_z_GRT_K) + " z ");
        RT_global_k_vec = "GRT_k_vec: " + df.format(cross_P_i_GRT_K) + " i + " + df.format(cross_P_j_GRT_K) + " j + " + df.format(cross_P_z_GRT_K) + " z ";
    }

    public void global_LT_k_vec(){
        cross_P_i_GLT_K = ((cross_P_j_GLT * (-1) * (Eigen_vectors_glt_y[2][eig_index_glt_y])) - (cross_P_z_GLT * (-1) * Eigen_vectors_glt_y[1][eig_index_glt_y]));
        cross_P_j_GLT_K = ((cross_P_z_GLT * (-1) * (Eigen_vectors_glt_y[0][eig_index_glt_y])) - (cross_P_i_GLT * (-1) * Eigen_vectors_glt_y[2][eig_index_glt_y]));
        cross_P_z_GLT_K = ((cross_P_i_GLT * (-1) * (Eigen_vectors_glt_y[1][eig_index_glt_y])) - (cross_P_j_GLT * (-1) * Eigen_vectors_glt_y[0][eig_index_glt_y]));
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("GLT_k_vec: " + df.format(cross_P_i_GLT_K) + " i + " + df.format(cross_P_j_GLT_K) + " j + " + df.format(cross_P_z_GLT_K) + " z ");
        LT_global_k_vec = "GLT_k_vec: " + df.format(cross_P_i_GLT_K) + " i + " + df.format(cross_P_j_GLT_K) + " j + " + df.format(cross_P_z_GLT_K) + " z ";
    }


    public void counter(){
        try {
            for (int i = 1; i < 6; i++) {
                action_textview = (TextView) findViewById(R.id.action_textview);
                action_textview.setText(String.valueOf(i));
                Thread.sleep(1000);
                if (i == 5){
                    count_status = true;
                }
                else {
                    count_status = false;
                }
            }
        }catch (Exception e){

        }
    }
}