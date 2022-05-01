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
    private static double [][] Rx;
    private static double [][] Ry;
    private static double [][] Rz;
    private static double [][] RzRy;
    private static double [][] RzRyRx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative_transformation_matrices);

        start_btn = (Button) findViewById(R.id.start_measurement_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Relative Transformation Matrix here ");
                read_data_orientation_z();
                action_textview = (TextView)findViewById(R.id.RZRYRX_matrix);
                //action_textview.setText();///////////////////////////////////////NEED Attention

            }
        });
    }

    public void read_data_orientation_z(){
        try{
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
                Cell x = s.getCell(2,i);
                x_read = x.getContents();
                ang_x.add(Double.parseDouble(x_read));

                Cell y = s.getCell(3,i);
                y_read = y.getContents();
                ang_y.add(Double.parseDouble(y_read));

                Cell z = s.getCell(4,i);
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


            for (int j = 0; j < sin_ang_x.size(); j++) {
                Rx = new double[][]{{1, 0, 0}, {0, cos_ang_x.get(j), (-1 * sin_ang_x.get(j))}, {0, sin_ang_x.get(j), cos_ang_x.get(j)}};
                Ry = new double[][]{{cos_ang_y.get(j),0,sin_ang_y.get(j)},{0,1,0},{(-1*sin_ang_y.get(j)),0,cos_ang_y.get(j)}};
                Rz = new double[][]{{cos_ang_z.get(j),(-1*sin_ang_z.get(j)),0},{sin_ang_z.get(j),cos_ang_z.get(j),0},{0,0,1}};


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
                            RzRy[i][k] +=Rz[i][l] * Ry[l][j];
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
                            RzRyRx[i][k] +=RzRy[i][l] * Ry[l][j];
                        }
                        System.out.print(df.format(RzRyRx[i][k]) + " ");
                    }
                    System.out.println();
                }
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
            }

        } catch (Exception e){

        }
    }
}