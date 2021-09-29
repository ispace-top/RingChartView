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
        nodes.add(new RingChartView.ProgressNode(61.28f, Color.RED));
        nodes.add(new RingChartView.ProgressNode(20.44f, Color.YELLOW));
        nodes.add(new RingChartView.ProgressNode(13.91f, Color.GREEN));
        nodes.add(new RingChartView.ProgressNode(3.51f, Color.CYAN));
        nodes.add(new RingChartView.ProgressNode(0.85f, Color.YELLOW));
        nodes.add(new RingChartView.ProgressNode(0.01f, Color.GREEN));
        ringChartView.setProgressNodes(nodes);
    }

}