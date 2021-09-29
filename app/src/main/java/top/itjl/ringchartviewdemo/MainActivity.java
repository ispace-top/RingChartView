package top.itjl.ringchartviewdemo;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import top.itjl.ringchartview.RingChartView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RingChartView ringChartView = findViewById(R.id.ring_chart_view);

        List<RingChartView.ProgressNode> nodes = new ArrayList<>();
        nodes.add(new RingChartView.ProgressNode(15, Color.RED));
        nodes.add(new RingChartView.ProgressNode(25, Color.YELLOW));
        nodes.add(new RingChartView.ProgressNode(45, Color.GREEN));
        nodes.add(new RingChartView.ProgressNode(15, Color.CYAN));

        ringChartView.setProgressNodes(nodes);
    }

}