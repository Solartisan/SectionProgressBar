package cc.solart.section.simple;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cc.solart.sectionbar.SectionProgressBar;

public class MainActivity extends AppCompatActivity {
    private SectionProgressBar mSectionBar1;
    private SectionProgressBar mSectionBar2;
    private String[] mLevels = {"铜卡", "银卡", "金卡", "白金卡", "钻卡"};
    private int[] mLevelValues = {0, 1000, 2000, 4000, 8000};

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSectionBar1 = (SectionProgressBar) findViewById(R.id.section_1);
        mSectionBar2 = (SectionProgressBar) findViewById(R.id.section_2);
        mSectionBar2.setLevels(mLevels);
        mSectionBar2.setLevelValues(mLevelValues);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSectionBar1.setCurrent(3000);
                mSectionBar2.setCurrent(3000);
            }
        }, 2000);
    }

}
