package com.example.motioncontrolcalibration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class RelativeTransformationMatrices extends AppCompatActivity {

    private TextView action_textview;
    private Button start_btn;
    private Button next_iteration_btn;
    private static double[][] Rx;
    private static double[][] Ry;
    private static double[][] Rz;
    private static double[][] RzRy;
    private static double[][] RzRyRx;
    private static double[][] A;
    private static double[][] B;
    private static double[][] r_imu_pelvis;
    private static double[][] r_imu_RT;
    private static double[][] r_imu_LT;
    private static double[][] r_imu_pelvis_global;
    private static double[][] r_pelvis_global_transpose;
    private static double[][] r_imu_rt_global;
    private static double[][] r_imu_lt_global;
    private static double[][] r_pelvis_global;
    private static double[][] r_rt_global;
    private static double[][] r_lt_global;
    private static double[][] r_imu_pelvis_global_transpose;
    private static double[][] r_imu_rt_global_transpose;
    private static double[][] r_imu_lt_global_transpose;
    double pelvis_rt_theta_x;
    double pelvis_rt_theta_y;
    double pelvis_rt_theta_z;
    double pelvis_lt_theta_x;
    double pelvis_lt_theta_y;
    double pelvis_lt_theta_z;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative_transformation_matrices);

        start_btn = (Button) findViewById(R.id.start_angle_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Relative Transformation Matrix here ");
                read_pelvis_data_orientation_z();
                read_RT_data_orientation_z();
                read_LT_data_orientation_z();
                R_PELVIS_RT();
                print_pelvis_rt_angle();
                R_PELVIS_LT();
                print_pelvis_lt_angle();
                action_textview = (TextView) findViewById(R.id.pelvis_RT_angles);
                action_textview.setText("Pelvis - RT angles");
                action_textview = (TextView) findViewById(R.id.pelvis_LT_angles);
                action_textview.setText("Pelvis - LT angles");

            }
        });

        next_iteration_btn = (Button) findViewById(R.id.next_iteration);
        next_iteration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                read_pelvis_data_orientation_z();
                read_RT_data_orientation_z();
                read_LT_data_orientation_z();
                R_PELVIS_RT();
                print_pelvis_rt_angle();
                R_PELVIS_LT();
                print_pelvis_lt_angle();
                action_textview = (TextView) findViewById(R.id.pelvis_RT_angles);
                action_textview.setText("Pelvis - RT angles");
                action_textview = (TextView) findViewById(R.id.pelvis_LT_angles);
                action_textview.setText("Pelvis - LT angles");

            }
        });
    }


    int p = 0;
    int rt = 0;
    int lt = 0;

    public int iteration_pelvis() {
        p = p + 1;
        return p;
    }

    public int iteration_RT(){
        rt = rt + 1;
        return rt;
    }

    public int iteration_LT(){
        lt = lt + 1;
        return lt;
    }

    public void get_r_imu_pelvis() {
        r_imu_pelvis = pelvis_calibration.send_r_imu_pelvis();
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("R_IMU_PELVIS:");
        for (int i = 0; i < r_imu_pelvis.length; i++) {
            for (int k = 0; k < r_imu_pelvis[i].length; k++) {
                System.out.print(df.format(r_imu_pelvis[i][k]) + " ");
            }
            System.out.println();
        }
    }

    public void get_r_imu_RT(){
        r_imu_RT = thigh_calibration.send_r_imu_RT();
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("R_IMU_RT:");
        for (int i = 0; i < r_imu_RT.length; i++) {
            for (int k = 0; k < r_imu_RT[i].length; k++) {
                System.out.print(df.format(r_imu_RT[i][k]) + " ");
            }
            System.out.println();
        }
    }

    public void get_r_imu_LT(){
        r_imu_LT = thigh_calibration.send_r_imu_LT();
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("R_IMU_LT:");
        for (int i = 0; i < r_imu_LT.length; i++) {
            for (int k = 0; k < r_imu_LT[i].length; k++) {
                System.out.print(df.format(r_imu_LT[i][k]) + " ");
            }
            System.out.println();
        }
    }

    public void get_r_imu_pelvis_global() {
        r_imu_pelvis_global = global_vectors.send_r_imu_pelvis_global();
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("R_IMU_Pelvis_Global_transpose:");
        for (int i = 0; i < r_imu_pelvis_global.length; i++) {
            for (int j = 0; j < r_imu_pelvis_global[i].length; j++) {
                //r_imu_pelvis_global_transpose[j][i] = r_imu_pelvis_global[i][j];
                System.out.print(df.format(r_imu_pelvis_global[j][i] + " "));
            }
            System.out.println();
        }
    }

    public void get_r_imu_rt_global(){
        r_imu_rt_global = global_vectors.send_r_imu_rt_global();
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("R_IMU_RT_Global_transpose:");
        for (int i = 0; i < r_imu_rt_global.length; i++) {
            for (int j = 0; j < r_imu_rt_global[i].length; j++) {
                //r_imu_pelvis_global_transpose[j][i] = r_imu_pelvis_global[i][j];
                System.out.print(df.format(r_imu_rt_global[j][i] + " "));
            }
            System.out.println();
        }
    }

    public void get_r_imu_lt_global(){
        r_imu_lt_global = global_vectors.send_r_imu_lt_global();
        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("R_IMU_LT_Global_transpose:");
        for (int i = 0; i < r_imu_lt_global.length; i++) {
            for (int j = 0; j < r_imu_lt_global[i].length; j++) {
                //r_imu_pelvis_global_transpose[j][i] = r_imu_pelvis_global[i][j];
                System.out.print(df.format(r_imu_lt_global[j][i] + " "));
            }
            System.out.println();
        }
    }

    public void read_pelvis_data_orientation_z() {
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens DOT_Pelvis_Orientation.xls");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);

            ArrayList<Double> ang_x;
            ArrayList<Double> ang_y;
            ArrayList<Double> ang_z;

            ang_x = new ArrayList<Double>();
            ang_y = new ArrayList<Double>();
            ang_z = new ArrayList<Double>();

            String x_read = "";
            String y_read = "";
            String z_read = "";

            for (int i = 11; i < s.getRows(); i++) {
                Cell x = s.getCell(2, i);
                x_read = x.getContents();
                ang_x.add(Double.parseDouble(x_read));

                Cell y = s.getCell(3, i);
                y_read = y.getContents();
                ang_y.add(Double.parseDouble(y_read));

                Cell z = s.getCell(4, i);
                z_read = z.getContents();
                ang_z.add(Double.parseDouble(z_read));
            }

            ArrayList<Double> sin_ang_x;
            ArrayList<Double> cos_ang_x;
            ArrayList<Double> sin_ang_y;
            ArrayList<Double> cos_ang_y;
            ArrayList<Double> sin_ang_z;
            ArrayList<Double> cos_ang_z;

            sin_ang_x = new ArrayList<Double>();
            cos_ang_x = new ArrayList<Double>();
            sin_ang_y = new ArrayList<Double>();
            cos_ang_y = new ArrayList<Double>();
            sin_ang_z = new ArrayList<Double>();
            cos_ang_z = new ArrayList<Double>();

            double sin_x = 0;
            double cos_x = 0;
            double sin_y = 0;
            double cos_y = 0;
            double sin_z = 0;
            double cos_z = 0;


            for (int i = 0; i < ang_x.size(); i++) {
                //// FOR X
                sin_x = Math.sin(Math.toRadians(ang_x.get(i)));
                sin_ang_x.add(sin_x);
                cos_x = Math.cos(Math.toRadians(ang_x.get(i)));
                cos_ang_x.add(cos_x);
                //// FOR Y
                sin_y = Math.sin(Math.toRadians(ang_y.get(i)));
                sin_ang_y.add(sin_y);
                cos_y = Math.cos(Math.toRadians(ang_y.get(i)));
                cos_ang_y.add(cos_y);
                //// FOR Z
                sin_z = Math.sin(Math.toRadians(ang_z.get(i)));
                sin_ang_z.add(sin_z);
                cos_z = Math.cos(Math.toRadians(ang_z.get(i)));
                cos_ang_z.add(cos_z);
            }


            get_r_imu_pelvis();
            get_r_imu_pelvis_global();
            int j = iteration_pelvis();

            //for (int j = 0; j < sin_ang_x.size(); j++) {
            Rx = new double[][]{{1, 0, 0}, {0, cos_ang_x.get(j), (-1 * sin_ang_x.get(j))}, {0, sin_ang_x.get(j), cos_ang_x.get(j)}};
            Ry = new double[][]{{cos_ang_y.get(j), 0, sin_ang_y.get(j)}, {0, 1, 0}, {(-1 * sin_ang_y.get(j)), 0, cos_ang_y.get(j)}};
            Rz = new double[][]{{cos_ang_z.get(j), (-1 * sin_ang_z.get(j)), 0}, {sin_ang_z.get(j), cos_ang_z.get(j), 0}, {0, 0, 1}};


            final DecimalFormat df = new DecimalFormat("0.0000");
            System.out.println("Rx:");
            for (int i = 0; i < Rx.length; i++) {
                for (int k = 0; k < Rx[i].length; k++) {
                    System.out.print(df.format(Rx[i][k]) + " ");
                }
                System.out.println();
            }

            System.out.println("Ry:");
            for (int i = 0; i < Ry.length; i++) {
                for (int k = 0; k < Ry[i].length; k++) {
                    System.out.print(df.format(Ry[i][k]) + " ");
                }
                System.out.println();
            }

            System.out.println("Rz:");
            for (int i = 0; i < Rz.length; i++) {
                for (int k = 0; k < Rz[i].length; k++) {
                    System.out.print(df.format(Rz[i][k]) + " ");
                }
                System.out.println();
            }

            /// FOR RX*RY
            double[][] RzRy = new double[3][3];
            System.out.println("RzRy: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    RzRy[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        RzRy[i][k] += Rz[i][l] * Ry[l][k];
                    }
                    System.out.print(df.format(RzRy[i][k]) + " ");
                }
                System.out.println();
            }

//                for (int i = 0; i < 3; i++) {
//                        RzRy[i][0] = (Rz[i][0] * Ry[0][i]) + (Rz[i][1] * Ry[1][i]) + (Rz[i][2] * Ry[2][i]);
//                        RzRy[i][1] = (Rz[i][0] * Ry[i][1]) + (Rz[i][1] * Ry[1][1]) + (Rz[i][2] * Ry[2][2]);
//                        RzRy[i][2] = (Rz[i][0] * Ry[0][2]) + (Rz[i][1] * Ry[1][2]) + (Rz[i][2] * Ry[2][2]);
//
//                    for (int k = 0; k < 3; k++) {
//                        System.out.print(RzRy[i][k] + " ");
//                    }
//                    System.out.print("\n");
//                }
            System.out.println("Data read");

            /// FOR RX*RY*RZ
            double[][] RzRyRx = new double[3][3];
            System.out.println("RzRyRx: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    RzRyRx[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        RzRyRx[i][k] += RzRy[i][l] * Rx[l][k];
                    }
                    System.out.print(df.format(RzRyRx[i][k]) + " ");
                }
                System.out.println();
            }

            // Multiplying R_imu_pelvis_global_transpose * RZRYRX * R_imu_pelvis
            // A = R_IMU_Pelvis_global_transpose * RZRYRX
            // B = A * R_IMU_Pelvis
            // B = R_global_pelvis
            double[][] A = new double[3][3];
            System.out.println("R_IMU_PELVIS_GLOBAL_TRANSPOSE * RzRyRx: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    A[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        A[i][k] += r_imu_pelvis_global_transpose[i][l] * RzRyRx[l][k];
                    }
                    System.out.print(df.format(A[i][k]) + " ");
                }
                System.out.println();
            }

            double[][] B = new double[3][3];
            System.out.println("A * R_IMU_PELVIS_GLOBAL: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    B[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        B[i][k] += A[i][l] * r_imu_pelvis_global[l][k];
                    }
                    System.out.print(df.format(B[i][k]) + " ");
                }
                System.out.println();
            }

            r_pelvis_global = B;

//                for (int i = 0; i < 3; i++) {
//                    RzRyRx[i][0] = (RzRy[i][0] * Rx[0][i]) + (RzRy[i][1] * Rx[1][i]) + (RzRy[i][2] * Rx[2][i]);
//                    RzRyRx[i][1] = (RzRy[i][0] * Rx[i][1]) + (RzRy[i][1] * Rx[1][1]) + (RzRy[i][2] * Rx[2][2]);
//                    RzRyRx[i][2] = (RzRy[i][0] * Rx[0][2]) + (RzRy[i][1] * Rx[1][2]) + (RzRy[i][2] * Rx[2][2]);
//
//                    for (int k = 0; k < 3; k++) {
//                        System.out.print(RzRyRx[j][k] + " ");
//                    }
//                    System.out.print("\n");
//                }
            Thread.sleep(2000);
            //}


        }catch (Exception e) {

        }
    }

    public void read_RT_data_orientation_z() {
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens DOT_Pelvis_Orientation.xls"); // NEEDS RT DATA INPUT HERE
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);

            ArrayList<Double> ang_x;
            ArrayList<Double> ang_y;
            ArrayList<Double> ang_z;

            ang_x = new ArrayList<Double>();
            ang_y = new ArrayList<Double>();
            ang_z = new ArrayList<Double>();

            String x_read = "";
            String y_read = "";
            String z_read = "";

            for (int i = 11; i < s.getRows(); i++) {
                Cell x = s.getCell(2, i);
                x_read = x.getContents();
                ang_x.add(Double.parseDouble(x_read));

                Cell y = s.getCell(3, i);
                y_read = y.getContents();
                ang_y.add(Double.parseDouble(y_read));

                Cell z = s.getCell(4, i);
                z_read = z.getContents();
                ang_z.add(Double.parseDouble(z_read));
            }

            ArrayList<Double> sin_ang_x;
            ArrayList<Double> cos_ang_x;
            ArrayList<Double> sin_ang_y;
            ArrayList<Double> cos_ang_y;
            ArrayList<Double> sin_ang_z;
            ArrayList<Double> cos_ang_z;

            sin_ang_x = new ArrayList<Double>();
            cos_ang_x = new ArrayList<Double>();
            sin_ang_y = new ArrayList<Double>();
            cos_ang_y = new ArrayList<Double>();
            sin_ang_z = new ArrayList<Double>();
            cos_ang_z = new ArrayList<Double>();

            double sin_x = 0;
            double cos_x = 0;
            double sin_y = 0;
            double cos_y = 0;
            double sin_z = 0;
            double cos_z = 0;


            for (int i = 0; i < ang_x.size(); i++) {
                //// FOR X
                sin_x = Math.sin(Math.toRadians(ang_x.get(i)));
                sin_ang_x.add(sin_x);
                cos_x = Math.cos(Math.toRadians(ang_x.get(i)));
                cos_ang_x.add(cos_x);
                //// FOR Y
                sin_y = Math.sin(Math.toRadians(ang_y.get(i)));
                sin_ang_y.add(sin_y);
                cos_y = Math.cos(Math.toRadians(ang_y.get(i)));
                cos_ang_y.add(cos_y);
                //// FOR Z
                sin_z = Math.sin(Math.toRadians(ang_z.get(i)));
                sin_ang_z.add(sin_z);
                cos_z = Math.cos(Math.toRadians(ang_z.get(i)));
                cos_ang_z.add(cos_z);
            }


            get_r_imu_RT();
            get_r_imu_rt_global();
            int j = iteration_RT();

            //for (int j = 0; j < sin_ang_x.size(); j++) {
            Rx = new double[][]{{1, 0, 0}, {0, cos_ang_x.get(j), (-1 * sin_ang_x.get(j))}, {0, sin_ang_x.get(j), cos_ang_x.get(j)}};
            Ry = new double[][]{{cos_ang_y.get(j), 0, sin_ang_y.get(j)}, {0, 1, 0}, {(-1 * sin_ang_y.get(j)), 0, cos_ang_y.get(j)}};
            Rz = new double[][]{{cos_ang_z.get(j), (-1 * sin_ang_z.get(j)), 0}, {sin_ang_z.get(j), cos_ang_z.get(j), 0}, {0, 0, 1}};


            final DecimalFormat df = new DecimalFormat("0.0000");
            System.out.println("Rx:");
            for (int i = 0; i < Rx.length; i++) {
                for (int k = 0; k < Rx[i].length; k++) {
                    System.out.print(df.format(Rx[i][k]) + " ");
                }
                System.out.println();
            }

            System.out.println("Ry:");
            for (int i = 0; i < Ry.length; i++) {
                for (int k = 0; k < Ry[i].length; k++) {
                    System.out.print(df.format(Ry[i][k]) + " ");
                }
                System.out.println();
            }

            System.out.println("Rz:");
            for (int i = 0; i < Rz.length; i++) {
                for (int k = 0; k < Rz[i].length; k++) {
                    System.out.print(df.format(Rz[i][k]) + " ");
                }
                System.out.println();
            }

            /// FOR RX*RY
            double[][] RzRy = new double[3][3];
            System.out.println("RzRy: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    RzRy[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        RzRy[i][k] += Rz[i][l] * Ry[l][k];
                    }
                    System.out.print(df.format(RzRy[i][k]) + " ");
                }
                System.out.println();
            }

//                for (int i = 0; i < 3; i++) {
//                        RzRy[i][0] = (Rz[i][0] * Ry[0][i]) + (Rz[i][1] * Ry[1][i]) + (Rz[i][2] * Ry[2][i]);
//                        RzRy[i][1] = (Rz[i][0] * Ry[i][1]) + (Rz[i][1] * Ry[1][1]) + (Rz[i][2] * Ry[2][2]);
//                        RzRy[i][2] = (Rz[i][0] * Ry[0][2]) + (Rz[i][1] * Ry[1][2]) + (Rz[i][2] * Ry[2][2]);
//
//                    for (int k = 0; k < 3; k++) {
//                        System.out.print(RzRy[i][k] + " ");
//                    }
//                    System.out.print("\n");
//                }
            System.out.println("Data read");

            /// FOR RX*RY*RZ
            double[][] RzRyRx = new double[3][3];
            System.out.println("RzRyRx: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    RzRyRx[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        RzRyRx[i][k] += RzRy[i][l] * Rx[l][k];
                    }
                    System.out.print(df.format(RzRyRx[i][k]) + " ");
                }
                System.out.println();
            }

            // Multiplying R_imu_pelvis_global_transpose * RZRYRX * R_imu_pelvis
            // A = R_IMU_Pelvis_global_transpose * RZRYRX
            // B = A * R_IMU_Pelvis
            // B = R_global_pelvis
            double[][] A = new double[3][3];
            System.out.println("R_IMU_RT_GLOBAL_TRANSPOSE * RzRyRx: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    A[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        A[i][k] += r_imu_rt_global_transpose[i][l] * RzRyRx[l][k];
                    }
                    System.out.print(df.format(A[i][k]) + " ");
                }
                System.out.println();
            }

            double[][] B = new double[3][3];
            System.out.println("A * R_IMU_RT_GLOBAL: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    B[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        B[i][k] += A[i][l] * r_imu_rt_global[l][k];
                    }
                    System.out.print(df.format(B[i][k]) + " ");
                }
                System.out.println();
            }

            r_rt_global = B;

//                for (int i = 0; i < 3; i++) {
//                    RzRyRx[i][0] = (RzRy[i][0] * Rx[0][i]) + (RzRy[i][1] * Rx[1][i]) + (RzRy[i][2] * Rx[2][i]);
//                    RzRyRx[i][1] = (RzRy[i][0] * Rx[i][1]) + (RzRy[i][1] * Rx[1][1]) + (RzRy[i][2] * Rx[2][2]);
//                    RzRyRx[i][2] = (RzRy[i][0] * Rx[0][2]) + (RzRy[i][1] * Rx[1][2]) + (RzRy[i][2] * Rx[2][2]);
//
//                    for (int k = 0; k < 3; k++) {
//                        System.out.print(RzRyRx[j][k] + " ");
//                    }
//                    System.out.print("\n");
//                }
            Thread.sleep(2000);
            //}


        } catch (Exception e) {

        }
    }
    public void read_LT_data_orientation_z() {
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens DOT_Pelvis_Orientation.xls"); /// NEEDS LT DATA HERE
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);

            ArrayList<Double> ang_x;
            ArrayList<Double> ang_y;
            ArrayList<Double> ang_z;

            ang_x = new ArrayList<Double>();
            ang_y = new ArrayList<Double>();
            ang_z = new ArrayList<Double>();

            String x_read = "";
            String y_read = "";
            String z_read = "";

            for (int i = 11; i < s.getRows(); i++) {
                Cell x = s.getCell(2, i);
                x_read = x.getContents();
                ang_x.add(Double.parseDouble(x_read));

                Cell y = s.getCell(3, i);
                y_read = y.getContents();
                ang_y.add(Double.parseDouble(y_read));

                Cell z = s.getCell(4, i);
                z_read = z.getContents();
                ang_z.add(Double.parseDouble(z_read));
            }

            ArrayList<Double> sin_ang_x;
            ArrayList<Double> cos_ang_x;
            ArrayList<Double> sin_ang_y;
            ArrayList<Double> cos_ang_y;
            ArrayList<Double> sin_ang_z;
            ArrayList<Double> cos_ang_z;

            sin_ang_x = new ArrayList<Double>();
            cos_ang_x = new ArrayList<Double>();
            sin_ang_y = new ArrayList<Double>();
            cos_ang_y = new ArrayList<Double>();
            sin_ang_z = new ArrayList<Double>();
            cos_ang_z = new ArrayList<Double>();

            double sin_x = 0;
            double cos_x = 0;
            double sin_y = 0;
            double cos_y = 0;
            double sin_z = 0;
            double cos_z = 0;


            for (int i = 0; i < ang_x.size(); i++) {
                //// FOR X
                sin_x = Math.sin(Math.toRadians(ang_x.get(i)));
                sin_ang_x.add(sin_x);
                cos_x = Math.cos(Math.toRadians(ang_x.get(i)));
                cos_ang_x.add(cos_x);
                //// FOR Y
                sin_y = Math.sin(Math.toRadians(ang_y.get(i)));
                sin_ang_y.add(sin_y);
                cos_y = Math.cos(Math.toRadians(ang_y.get(i)));
                cos_ang_y.add(cos_y);
                //// FOR Z
                sin_z = Math.sin(Math.toRadians(ang_z.get(i)));
                sin_ang_z.add(sin_z);
                cos_z = Math.cos(Math.toRadians(ang_z.get(i)));
                cos_ang_z.add(cos_z);
            }


            get_r_imu_LT();
            get_r_imu_lt_global();
            int j = iteration_LT();

            //for (int j = 0; j < sin_ang_x.size(); j++) {
            Rx = new double[][]{{1, 0, 0}, {0, cos_ang_x.get(j), (-1 * sin_ang_x.get(j))}, {0, sin_ang_x.get(j), cos_ang_x.get(j)}};
            Ry = new double[][]{{cos_ang_y.get(j), 0, sin_ang_y.get(j)}, {0, 1, 0}, {(-1 * sin_ang_y.get(j)), 0, cos_ang_y.get(j)}};
            Rz = new double[][]{{cos_ang_z.get(j), (-1 * sin_ang_z.get(j)), 0}, {sin_ang_z.get(j), cos_ang_z.get(j), 0}, {0, 0, 1}};


            final DecimalFormat df = new DecimalFormat("0.0000");
            System.out.println("Rx:");
            for (int i = 0; i < Rx.length; i++) {
                for (int k = 0; k < Rx[i].length; k++) {
                    System.out.print(df.format(Rx[i][k]) + " ");
                }
                System.out.println();
            }

            System.out.println("Ry:");
            for (int i = 0; i < Ry.length; i++) {
                for (int k = 0; k < Ry[i].length; k++) {
                    System.out.print(df.format(Ry[i][k]) + " ");
                }
                System.out.println();
            }

            System.out.println("Rz:");
            for (int i = 0; i < Rz.length; i++) {
                for (int k = 0; k < Rz[i].length; k++) {
                    System.out.print(df.format(Rz[i][k]) + " ");
                }
                System.out.println();
            }

            /// FOR RX*RY
            double[][] RzRy = new double[3][3];
            System.out.println("RzRy: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    RzRy[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        RzRy[i][k] += Rz[i][l] * Ry[l][k];
                    }
                    System.out.print(df.format(RzRy[i][k]) + " ");
                }
                System.out.println();
            }

//                for (int i = 0; i < 3; i++) {
//                        RzRy[i][0] = (Rz[i][0] * Ry[0][i]) + (Rz[i][1] * Ry[1][i]) + (Rz[i][2] * Ry[2][i]);
//                        RzRy[i][1] = (Rz[i][0] * Ry[i][1]) + (Rz[i][1] * Ry[1][1]) + (Rz[i][2] * Ry[2][2]);
//                        RzRy[i][2] = (Rz[i][0] * Ry[0][2]) + (Rz[i][1] * Ry[1][2]) + (Rz[i][2] * Ry[2][2]);
//
//                    for (int k = 0; k < 3; k++) {
//                        System.out.print(RzRy[i][k] + " ");
//                    }
//                    System.out.print("\n");
//                }
            System.out.println("Data read");

            /// FOR RX*RY*RZ
            double[][] RzRyRx = new double[3][3];
            System.out.println("RzRyRx: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    RzRyRx[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        RzRyRx[i][k] += RzRy[i][l] * Rx[l][k];
                    }
                    System.out.print(df.format(RzRyRx[i][k]) + " ");
                }
                System.out.println();
            }

            // Multiplying R_imu_pelvis_global_transpose * RZRYRX * R_imu_pelvis
            // A = R_IMU_Pelvis_global_transpose * RZRYRX
            // B = A * R_IMU_Pelvis
            // B = R_global_pelvis
            double[][] A = new double[3][3];
            System.out.println("R_IMU_LT_GLOBAL_TRANSPOSE * RzRyRx: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    A[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        A[i][k] += r_imu_lt_global_transpose[i][l] * RzRyRx[l][k];
                    }
                    System.out.print(df.format(A[i][k]) + " ");
                }
                System.out.println();
            }

            double[][] B = new double[3][3];
            System.out.println("A * R_IMU_LT_GLOBAL: ");
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    B[i][k] = 0;
                    for (int l = 0; l < 3; l++) {
                        B[i][k] += A[i][l] * r_imu_lt_global[l][k];
                    }
                    System.out.print(df.format(B[i][k]) + " ");
                }
                System.out.println();
            }

            r_lt_global = B;


//                for (int i = 0; i < 3; i++) {
//                    RzRyRx[i][0] = (RzRy[i][0] * Rx[0][i]) + (RzRy[i][1] * Rx[1][i]) + (RzRy[i][2] * Rx[2][i]);
//                    RzRyRx[i][1] = (RzRy[i][0] * Rx[i][1]) + (RzRy[i][1] * Rx[1][1]) + (RzRy[i][2] * Rx[2][2]);
//                    RzRyRx[i][2] = (RzRy[i][0] * Rx[0][2]) + (RzRy[i][1] * Rx[1][2]) + (RzRy[i][2] * Rx[2][2]);
//
//                    for (int k = 0; k < 3; k++) {
//                        System.out.print(RzRyRx[j][k] + " ");
//                    }
//                    System.out.print("\n");
//                }
            Thread.sleep(2000);
            //}


        } catch (Exception e) {

        }
    }
    double[][] R_PELVIS_RT = new double[3][3];
    public void R_PELVIS_RT(){
        // getting the transpose of r_pelvis_global first
        for (int i = 0; i < r_pelvis_global.length; i++) {
            for (int j = 0; j < r_pelvis_global[i].length; j++) {
                r_pelvis_global_transpose[i][j] = r_pelvis_global[j][i];
            }
        }

        final DecimalFormat df = new DecimalFormat("0.0000");
//        double[][] R_PELVIS_RT = new double[3][3];
        System.out.println("r_pelvis_global_transpose * r_rt_global: ");
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                R_PELVIS_RT[i][k] = 0;
                for (int l = 0; l < 3; l++) {
                    R_PELVIS_RT[i][k] += r_pelvis_global_transpose[i][l] * r_rt_global[l][k];
                }
                System.out.print(df.format(R_PELVIS_RT[i][k]) + " ");
            }
            System.out.println();
        }
    }

    public void print_pelvis_rt_angle(){
        pelvis_rt_theta_x = Math.toDegrees(Math.atan2(R_PELVIS_RT[3][2],R_PELVIS_RT[3][3]));
        pelvis_rt_theta_y = Math.toDegrees(Math.atan2((-1*R_PELVIS_RT[3][1]),Math.sqrt(Math.pow(R_PELVIS_RT[3][2],2)+Math.pow(R_PELVIS_RT[3][3],2))));
        pelvis_rt_theta_z = Math.toDegrees(Math.atan2(R_PELVIS_RT[2][1],R_PELVIS_RT[1][1]));

        System.out.println("Theta_X for Pelvis_RT = " + pelvis_rt_theta_x);
        System.out.println("Theta_Y for Pelvis_RT = " + pelvis_rt_theta_y);
        System.out.println("Theta_Z for Pelvis_RT = " + pelvis_rt_theta_z);

        action_textview = (TextView) findViewById(R.id.pelvis_RT_theta_x);
        action_textview.setText(Double.toString(pelvis_rt_theta_x));
        action_textview = (TextView) findViewById(R.id.pelvis_RT_theta_y);
        action_textview.setText(Double.toString(pelvis_rt_theta_y));
        action_textview = (TextView) findViewById(R.id.pelvis_RT_theta_z);
        action_textview.setText(Double.toString(pelvis_rt_theta_z));
    }

    double[][] R_PELVIS_LT = new double[3][3];
    public void R_PELVIS_LT(){
        // getting the transpose of r_pelvis_global first
        for (int i = 0; i < r_pelvis_global.length; i++) {
            for (int j = 0; j < r_pelvis_global[i].length; j++) {
                r_pelvis_global_transpose[i][j] = r_pelvis_global[j][i];
            }
        }

        final DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println("r_pelvis_global_transpose * r_lt_global: ");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                R_PELVIS_LT[i][j] = 0;
                for (int k = 0; k < 3; k++) {
                    R_PELVIS_LT[i][j] += r_pelvis_global_transpose[i][k] * r_lt_global[k][j];
                }
                System.out.print(df.format(R_PELVIS_LT[i][j]) + " ");
                }
            System.out.println();
        }
    }
    public void print_pelvis_lt_angle(){
        pelvis_lt_theta_x = Math.toDegrees(Math.atan2(R_PELVIS_LT[3][2],R_PELVIS_LT[3][3]));
        pelvis_lt_theta_y = Math.toDegrees(Math.atan2((-1*R_PELVIS_LT[3][1]),Math.sqrt(Math.pow(R_PELVIS_LT[3][2],2)+Math.pow(R_PELVIS_LT[3][3],2))));
        pelvis_lt_theta_z = Math.toDegrees(Math.atan2(R_PELVIS_LT[2][1],R_PELVIS_LT[1][1]));

        System.out.println("Theta_X for Pelvis_LT = " + pelvis_lt_theta_x);
        System.out.println("Theta_Y for Pelvis_LT = " + pelvis_lt_theta_y);
        System.out.println("Theta_Z for Pelvis_LT = " + pelvis_lt_theta_z);

        action_textview = (TextView) findViewById(R.id.pelvis_LT_theta_x);
        action_textview.setText(Double.toString(pelvis_lt_theta_x));
        action_textview = (TextView) findViewById(R.id.pelvis_LT_theta_y);
        action_textview.setText(Double.toString(pelvis_lt_theta_y));
        action_textview = (TextView) findViewById(R.id.pelvis_LT_theta_z);
        action_textview.setText(Double.toString(pelvis_lt_theta_z));

    }
}
