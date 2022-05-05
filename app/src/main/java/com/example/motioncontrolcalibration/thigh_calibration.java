package com.example.motioncontrolcalibration;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Intent;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class thigh_calibration extends AppCompatActivity {

    private Button z_axis_btn;
    private Button xr_axis_btn;
    private Button xl_axis_btn;
    private Button y_axis_btn;
    private Button start_measurement_btn;
    private Button stop_measurement_btn;
    private TextView action_textview;
    private static double [][] Eigen_vectors_1_R;
    private static double [][] Eigen_vectors_1_L;
    private static double [][] Eigen_vectors_2_R;
    private static double [][] Eigen_vectors_2_L;
    private static double [][] r_imu_RT;
    private static double [][] r_imu_LT;
    private static int eig_index_1_r;
    private static int eig_index_1_l;
    private int eig_index_2_r;
    private int eig_index_2_l;
    private static double cross_P_i;
    private static double cross_P_j;
    private static double cross_P_z;
    private static String axis1r;
    private static String axis1l;
    private static String axis2r;
    private static String axis2l;
    private static String axis3r;
    private static String axis3l;
    private static String R_IMU_Pelvis_r;
    private static String R_IMU_Pelvis_l;
    private int count = 1;
    private Button global_vectors_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thigh_calibration);

        z_axis_btn = (Button) findViewById(R.id.z_axis_btn);
        z_axis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thigh_calibration_z_dialog thigh_calibration_z_dialog = new thigh_calibration_z_dialog();
                thigh_calibration_z_dialog.show(getSupportFragmentManager(),"thigh_calibration_z_dialog");
                action_textview = (TextView) findViewById(R.id.action_textview);
                action_textview.setText(" Press Start to Initiate");
                count = 1;
            }
        });

        xr_axis_btn  = (Button)findViewById(R.id.xr_axis_btn);
        xr_axis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                thigh_calibration_xr_dialog thigh_calibration_xr = new thigh_calibration_xr_dialog();
                thigh_calibration_xr.show(getSupportFragmentManager(),"thigh_calibration_xr_dialog");
                action_textview = (TextView) findViewById(R.id.action_textview);
                action_textview.setText(" Press Start to initiate");
                count = 2;
            }
        });

        xl_axis_btn = (Button) findViewById(R.id.xl_axis_btn);
        xl_axis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thigh_calibration_xl_dialog thigh_calibration_xl = new thigh_calibration_xl_dialog();
                thigh_calibration_xl.show(getSupportFragmentManager(),"thigh_calibration_xl_dialog");
                action_textview = (TextView) findViewById(R.id.action_textview);
                action_textview.setText(" Press Start to initiate");
                count = 3;
            }
        });

        y_axis_btn = (Button) findViewById(R.id.y_axis_btn);
        y_axis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thigh_calibration_y_dialog thigh_calibration_y = new thigh_calibration_y_dialog();
                thigh_calibration_y.show(getSupportFragmentManager(),"thigh_calibration_y_dialog");
                action_textview = (TextView) findViewById(R.id.action_textview);
                action_textview.setText(" Press Start to initiate");
                count = 4;
            }
        });

        start_measurement_btn = (Button) findViewById(R.id.start_measurement_btn);
        start_measurement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 1){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Measuring for Thigh Z axis");
                }
                else if (count == 2){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Measuring for Thigh XR axis");
                }
                else if (count == 3){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Measuring for Thigh XL axis");
                }
                else if (count == 4){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Measuring for Thigh Y axis");
                    CrossProduct_r();
                    CrossProduct_l();
                    action_textview = (TextView) findViewById(R.id.axis_3R);
                    action_textview.setText(axis3r);
                    action_textview = (TextView) findViewById(R.id.axis_3L);
                    action_textview.setText(axis3l);
                }
            }
        });

        stop_measurement_btn = (Button) findViewById(R.id.stop_measurement_btn);
        stop_measurement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 1){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Thigh Z axis determined");
                    read_data_1R();
                    read_data_1L();
                    action_textview = (TextView) findViewById(R.id.axis_1R);
                    action_textview.setText(axis1r);
                    action_textview = (TextView) findViewById(R.id.axis_1L);
                    action_textview.setText(axis1l);
                }
                else if (count == 2){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Thigh XR axis determined");
                    read_data_2r();
                    action_textview = (TextView) findViewById(R.id.axis_2R);
                    action_textview.setText(axis2r);
                }
                else if(count == 3){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Thigh XL axis determined");
                    read_data_2l();
                    action_textview = (TextView) findViewById(R.id.axis_2L);
                    action_textview.setText(axis2l);
                }
                else if (count == 4){
                    action_textview = (TextView) findViewById(R.id.action_textview);
                    action_textview.setText(" Thigh Y axis determined");
                    R_IMU_Pelvis_r();
                    R_IMU_Pelvis_l();
                    action_textview = (TextView) findViewById(R.id.r_imu_thigh_R);
                    action_textview.setText(R_IMU_Pelvis_r);
                    action_textview = (TextView) findViewById(R.id.r_imu_thigh_L);
                    action_textview.setText(R_IMU_Pelvis_l);
                    global_vectors_btn = (Button) findViewById(R.id.global_vectors_btn);
                    global_vectors_btn.setVisibility(view.VISIBLE);
                }
            }
        });

        global_vectors_btn = (Button) findViewById(R.id.global_vectors_btn);
        global_vectors_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGlobal_Vectors_Activity();
            }
        });
    }

    public void openGlobal_Vectors_Activity(){
        Intent intent = new Intent(this, global_vectors.class);
        startActivity(intent);
    }


        public void read_data_1R(){
            try {
                AssetManager am = getAssets();
                InputStream is = am.open("Xsens DOT_RT_Z.xls");
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
                eig_index_1_r = 0;
                for (int i = 0; i < realPart.length; i++) {
                    if (realPart[i] > max_eig){
                        max_eig = realPart[i];
                        eig_index_1_r = i;
                    }
                }
                System.out.println("Max_eig_val:" + max_eig);
                System.out.println("Max_eig_index:" + eig_index_1_r);

                /// Now printing the eigen vector of the maximum eigen value index
                Matrix evectors = eigen.getV();
                evectors.print(3,3);
                Eigen_vectors_1_R = evectors.getArray();
                System.out.println("Eigen Vector R: " + df.format(Eigen_vectors_1_R[0][eig_index_1_r]) + " i + " + df.format(Eigen_vectors_1_R[1][eig_index_1_r])+ " j + " +  df.format(Eigen_vectors_1_R[2][eig_index_1_r])+ "k ");
                axis1r = "Vector 1R: " + df.format(Eigen_vectors_1_R[0][eig_index_1_r]) + " i + " + df.format(Eigen_vectors_1_R[1][eig_index_1_r])+ " j + " +  df.format(Eigen_vectors_1_R[2][eig_index_1_r])+ "k ";
                String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
                String done_command = "Axis 1 achieved";
//                display1(done_command);
//                display2(c_matrix);
//                display4(axis1);
            } catch (Exception e){

            }
        }

        public static int send_RT_eig_index_z(){
        return eig_index_1_r;
        }

        public static double[][] send_RT_z_vector(){
        return Eigen_vectors_1_R;
        }

    public void read_data_1L(){
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens DOT_LT_Z.xls");
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
            eig_index_1_l = 0;
            for (int i = 0; i < realPart.length; i++) {
                if (realPart[i] > max_eig){
                    max_eig = realPart[i];
                    eig_index_1_l = i;
                }
            }
            System.out.println("Max_eig_val:" + max_eig);
            System.out.println("Max_eig_index:" + eig_index_1_l);

            /// Now printing the eigen vector of the maximum eigen value index
            Matrix evectors = eigen.getV();
            evectors.print(3,3);
            Eigen_vectors_1_L = evectors.getArray();
            System.out.println("Eigen Vector L: " + df.format(Eigen_vectors_1_L[0][eig_index_1_l]) + " i + " + df.format(Eigen_vectors_1_L[1][eig_index_1_l])+ " j + " +  df.format(Eigen_vectors_1_L[2][eig_index_1_l])+ "k ");
            axis1l = "Vector 1L: " + df.format(Eigen_vectors_1_L[0][eig_index_1_l]) + " i + " + df.format(Eigen_vectors_1_L[1][eig_index_1_l])+ " j + " +  df.format(Eigen_vectors_1_L[2][eig_index_1_l])+ "k ";
            String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
            String done_command = "Axis 1 achieved";
//                display1(done_command);
//                display2(c_matrix);
//                display4(axis1);
        } catch (Exception e){

        }
    }

    public static int send_LT_eig_index_z(){
        return eig_index_1_l;
    }

    public static double[][] send_LT_z_vector(){
        return Eigen_vectors_1_L;
    }

    public void read_data_2r(){
        try{
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens DOT_RT_X.xls");
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
            eig_index_2_r = 0;
            for (int i = 0; i < realPart.length; i++) {
                if (realPart[i] > max_eig){
                    max_eig = realPart[i];
                    eig_index_2_r = i;
                }
            }
            System.out.println("Max_eig_val:" + max_eig);
            System.out.println("Max_eig_index:" + eig_index_2_r);

            /// Now printing the eigen vector of the maximum eigen value index

            Matrix evectors = eigen.getV();
            evectors.print(3,3);
            Eigen_vectors_2_R = evectors.getArray();
            System.out.println("Eigen Vector R: " + df.format(Eigen_vectors_2_R[0][eig_index_2_r]) + " i + " + df.format(Eigen_vectors_2_R[1][eig_index_2_r])+ " j + " +  df.format(Eigen_vectors_2_R[2][eig_index_2_r])+ "k ");
            axis2r = "Vector 2R: " + df.format(Eigen_vectors_2_R[0][eig_index_2_r]) + " i + " + df.format(Eigen_vectors_2_R[1][eig_index_2_r])+ " j + " +  df.format(Eigen_vectors_2_R[2][eig_index_2_r])+ "k ";
            String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
            String done_command = "Axis 2R achieved";
//                display1(done_command);
//                display3(c_matrix);
//                display5(axis2);
        } catch (Exception e){

        }
    }

    public void read_data_2l(){
        try{
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens DOT_LT_X.xls");
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
            eig_index_2_l = 0;
            for (int i = 0; i < realPart.length; i++) {
                if (realPart[i] > max_eig){
                    max_eig = realPart[i];
                    eig_index_2_l = i;
                }
            }
            System.out.println("Max_eig_val:" + max_eig);
            System.out.println("Max_eig_index:" + eig_index_2_l);

            /// Now printing the eigen vector of the maximum eigen value index

            Matrix evectors = eigen.getV();
            evectors.print(3,3);
            Eigen_vectors_2_L = evectors.getArray();
            System.out.println("Eigen Vector L: " + df.format(Eigen_vectors_2_L[0][eig_index_2_l]) + " i + " + df.format(Eigen_vectors_2_L[1][eig_index_2_l])+ " j + " +  df.format(Eigen_vectors_2_L[2][eig_index_2_l])+ "k ");
            axis2l = "Vector 2L: " + df.format(Eigen_vectors_2_L[0][eig_index_2_l]) + " i + " + df.format(Eigen_vectors_2_L[1][eig_index_2_l])+ " j + " +  df.format(Eigen_vectors_2_L[2][eig_index_2_l])+ "k ";
            String c_matrix = "[ " + df.format(var_wx) + " " + df.format(cov_wx_wy) + " " + df.format(cov_wx_wz) +  "\n" + df.format(cov_wx_wy) + " " + df.format(var_wy) + " " + df.format(cov_wy_wz) + "\n" + df.format(cov_wx_wz) + " " + df.format(cov_wy_wz) + " " + df.format(var_wz) + " ]";
            String done_command = "Axis 2L achieved";
//                display1(done_command);
//                display3(c_matrix);
//                display5(axis2);
        } catch (Exception e){

        }
    }

    public void CrossProduct_r(){
        cross_P_i = (Eigen_vectors_1_R[1][eig_index_1_r]*Eigen_vectors_2_R[2][eig_index_2_r] - Eigen_vectors_1_R[2][eig_index_1_r]*Eigen_vectors_2_R[1][eig_index_2_r]);
        cross_P_j = (Eigen_vectors_1_R[2][eig_index_1_r]*Eigen_vectors_2_R[0][eig_index_2_r] - Eigen_vectors_1_R[0][eig_index_1_r]*Eigen_vectors_2_R[2][eig_index_2_r]);
        cross_P_z = (Eigen_vectors_1_R[0][eig_index_1_r]*Eigen_vectors_2_R[1][eig_index_2_r] - Eigen_vectors_1_R[1][eig_index_1_r]*Eigen_vectors_2_R[0][eig_index_2_r]);
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("Vector 3R: " + df.format(cross_P_i) + " i + " + df.format(cross_P_j) + " j + " + df.format(cross_P_z) + " z ");
        axis3r = "Vector 3R: " + df.format(cross_P_i) + " i + " + df.format(cross_P_j) + " j + " + df.format(cross_P_z) + " z ";
//        display6(axis3);
    }

    public void CrossProduct_l(){
        cross_P_i = (Eigen_vectors_1_L[1][eig_index_1_l]*Eigen_vectors_2_L[2][eig_index_2_l] - Eigen_vectors_1_L[2][eig_index_1_l]*Eigen_vectors_2_L[1][eig_index_2_l]);
        cross_P_j = (Eigen_vectors_1_L[2][eig_index_1_l]*Eigen_vectors_2_L[0][eig_index_2_l] - Eigen_vectors_1_L[0][eig_index_1_l]*Eigen_vectors_2_L[2][eig_index_2_l]);
        cross_P_z = (Eigen_vectors_1_L[0][eig_index_1_l]*Eigen_vectors_2_L[1][eig_index_2_l] - Eigen_vectors_1_L[1][eig_index_1_l]*Eigen_vectors_2_L[0][eig_index_2_l]);
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("Vector 3L: " + df.format(cross_P_i) + " i + " + df.format(cross_P_j) + " j + " + df.format(cross_P_z) + " z ");
        axis3l = "Vector 3L: " + df.format(cross_P_i) + " i + " + df.format(cross_P_j) + " j + " + df.format(cross_P_z) + " z ";
//        display6(axis3);
    }

    public void R_IMU_Pelvis_r(){
        final DecimalFormat df = new DecimalFormat("0.0000");
        R_IMU_Pelvis_r = "R_r = [ " + df.format(Eigen_vectors_1_R[0][eig_index_1_r]) + "   " + df.format(Eigen_vectors_1_R[1][eig_index_1_r]) + "   " + df.format(Eigen_vectors_1_R[2][eig_index_1_r]) + " \n " + "     " +  df.format(Eigen_vectors_1_R[2][eig_index_1_r]) + "   " + df.format(Eigen_vectors_2_R[1][eig_index_2_r]) + "   " + df.format(Eigen_vectors_2_R[2][eig_index_2_r]) + " \n " + "     " + df.format(cross_P_i) + "   " + df.format(cross_P_j) + "   " + df.format(cross_P_z) + " ]";
        r_imu_RT = new double[][]{{Double.valueOf(df.format(Eigen_vectors_1_R[0][eig_index_1_r])),Double.valueOf(df.format(Eigen_vectors_1_R[1][eig_index_1_r])),Double.valueOf(df.format(Eigen_vectors_1_R[2][eig_index_1_r]))},{Double.valueOf(df.format(Eigen_vectors_1_R[2][eig_index_1_r])),Double.valueOf(df.format(Eigen_vectors_2_R[1][eig_index_2_r])),Double.valueOf(df.format(Eigen_vectors_2_R[2][eig_index_2_r]))},{Double.valueOf(df.format(cross_P_i)),Double.valueOf(df.format(cross_P_j)),Double.valueOf(df.format(cross_P_z))}};
    }

    public static double[][] send_r_imu_RT(){
        return r_imu_RT;
    }

    public void R_IMU_Pelvis_l(){
        final DecimalFormat df = new DecimalFormat("0.0000");
        R_IMU_Pelvis_l = "R_l = [ " + df.format(Eigen_vectors_1_L[0][eig_index_1_l]) + "   " + df.format(Eigen_vectors_1_L[1][eig_index_1_l]) + "   " + df.format(Eigen_vectors_1_L[2][eig_index_1_l]) + " \n " + "     " +  df.format(Eigen_vectors_1_L[2][eig_index_1_l]) + "   " + df.format(Eigen_vectors_2_L[1][eig_index_2_l]) + "   " + df.format(Eigen_vectors_2_L[2][eig_index_2_l]) + " \n " + "     " + df.format(cross_P_i) + "   " + df.format(cross_P_j) + "   " + df.format(cross_P_z) + " ]";
        r_imu_LT = new double[][]{{Double.valueOf(df.format(Eigen_vectors_1_L[0][eig_index_1_l])),Double.valueOf(df.format(Eigen_vectors_1_L[1][eig_index_1_l])),Double.valueOf(df.format(Eigen_vectors_1_L[2][eig_index_1_l]))},{Double.valueOf(df.format(Eigen_vectors_1_L[2][eig_index_1_l])),Double.valueOf(df.format(Eigen_vectors_2_L[1][eig_index_2_l])),Double.valueOf(df.format(Eigen_vectors_2_L[2][eig_index_2_l]))},{Double.valueOf(df.format(cross_P_i)),Double.valueOf(df.format(cross_P_j)),Double.valueOf(df.format(cross_P_z))}};
    }

    public static double[][] send_r_imu_LT(){
        return r_imu_LT;
    }
}