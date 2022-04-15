package com.example.motioncontrolcalibration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;

import java.io.InputStream;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class RelativeTransformationMatrices extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative_transformation_matrices);
    }

    public void read_data_orientation_z(){
        try{
            AssetManager am = getAssets();
            InputStream is = am.open("Xsens Dot_Pelvis_Orientation.xls");
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
                Cell x = s.getCell(3,i);
                x_read = x.getContents();
                ang_x.add(Double.parseDouble(x_read));

                Cell y = s.getCell(4,i);
                y_read = y.getContents();
                ang_y.add(Double.parseDouble(y_read));

                Cell z = s.getCell(5,i);
                z_read = z.getContents();
                ang_z.add(Double.parseDouble(z_read));
            }

            ArrayList<Double> sin_ang_x;
            ArrayList<Double> cos_ang_x;

            sin_ang_x = new ArrayList<Double>();
            cos_ang_x = new ArrayList<Double>();

            double sin_x = 0;
            double cos_x = 0;

            for (int i = 0; i < ang_x.size(); i++) {
                sin_x = Math.sin(Math.toRadians(ang_x.get(i)));
                sin_ang_x.add(sin_x);
                cos_x = Math.cos(Math.toRadians(ang_x.get(i)));
                cos_ang_x.add(cos_x);
                cos_ang_x.add(cos_x);


            }
        }catch (Exception e){

        }
    }
}