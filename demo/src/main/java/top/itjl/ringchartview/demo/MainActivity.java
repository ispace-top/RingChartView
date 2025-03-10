package top.itjl.ringchartview.demo;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import top.itjl.ringchartview.RingChartView;
import top.itjl.ringchartview.demo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupRingChartView2();
    }

    private void setupRingChartView2() {
        RingChartView ringChartView2 = binding.ringChartView2;
        ringChartView2.setMultiProgress(true);

        List<RingChartView.ProgressNode> nodeList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nodeList.add(new RingChartView.ProgressNode(10, getColor(R.color.chart_green)));
            nodeList.add(new RingChartView.ProgressNode(20, getColor(R.color.chart_blue)));
            nodeList.add(new RingChartView.ProgressNode(50, getColor(R.color.chart_red)));
            nodeList.add(new RingChartView.ProgressNode(10, getColor(R.color.chart_yellow)));
        }
        ringChartView2.setProgressNodes(nodeList);
        ringChartView2.setMaxValue(100);
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.ringChartView1.playAnimation();
        binding.ringChartView2.playAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.ringChartView1.stopAnimation();
        binding.ringChartView2.stopAnimation();
    }
}