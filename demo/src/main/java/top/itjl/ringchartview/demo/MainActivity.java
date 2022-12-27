package top.itjl.ringchartview.demo;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import top.itjl.ringchartview.RingChartView;

public class MainActivity extends AppCompatActivity {

    private RingChartView ringChartView1;
    private RingChartView ringChartView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ringChartView1=findViewById(R.id.ring_chart_view1);

        ringChartView2=new RingChartView(this);
        ringChartView2.setMaxValue(200);
        ringChartView2.setProgress(150);
        ringChartView2.setPaintWidth(100);
        ringChartView2.setPadding(40,40,40,40);

        findViewById(R.id.btn).setOnClickListener(view -> {
            LinearLayout rootView = (LinearLayout) ringChartView1.getParent();
            rootView.addView(ringChartView2);
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        ringChartView1.playAnimation();
    }
}