package top.itjl.ringchartview.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import top.itjl.ringchartview.RingChartView;

public class MainActivity extends AppCompatActivity {

    private RingChartView ringChartView1,ringChartView2,ringChartView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ringChartView1=findViewById(R.id.ring_chart_view1);
        ringChartView2=findViewById(R.id.ring_chart_view2);
        ringChartView3=findViewById(R.id.ring_chart_view3);

        ringChartView2.setMultiProgress(true);
        List<RingChartView.ProgressNode> nodeList=new ArrayList<>();
        nodeList.add(new RingChartView.ProgressNode(10, Color.GREEN));
        nodeList.add(new RingChartView.ProgressNode(20, Color.BLUE));
        nodeList.add(new RingChartView.ProgressNode(50, Color.RED));
        nodeList.add(new RingChartView.ProgressNode(10, Color.YELLOW));
        ringChartView2.setProgressNodes(nodeList);
        ringChartView2.setMaxValue(100);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        ringChartView1.playAnimation();
    }
}