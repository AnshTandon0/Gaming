package com.gaming.community.flexster.welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gaming.community.flexster.phoneAuth.GenerateOTPActivity;
import com.google.android.material.tabs.TabLayout;
import com.gaming.community.flexster.R;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button next;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //ini view
        next = findViewById(R.id.next);
        tabIndicator = findViewById(R.id.indicator);

        //Fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Flexing","Flex with your followers and\n"+"buddies in gaming clubs\n"+"by sharing proud moments.",R.drawable.ic_one));
        mList.add(new ScreenItem("Connect","Club group chat, Private chat,\n"+"Live voice & video chat\n"+"with buddies.",R.drawable.ic_two));
        mList.add(new ScreenItem("Explore","Find new gaming buddies,\n"+"Leaderboard for most\n"+"skilled and highest ranking\n"+"players.",R.drawable.ic_three));
        mList.add(new ScreenItem("Challenge","Create or join in Club vs Club,\n"+"Club scrim or Personal fight\n"+"challenges.",R.drawable.ic_four));
        mList.add(new ScreenItem("Ranks & Badges","Join & Save fight results,\n"+"Get engaged with your\n"+"followers and gaming clubs\n"+"to level up.",R.drawable.ic_five));
        mList.add(new ScreenItem("","Ready to flex?",R.drawable.ic_six));

        //Setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        //setup tabLayout with pagerView
        tabIndicator.setupWithViewPager(screenPager);

        //Next btn click
        next.setOnClickListener(view -> {

            position = screenPager.getCurrentItem();
            if (position < mList.size()){
                position++;
                screenPager.setCurrentItem(position);
            }
            //When reached last
            if (position == mList.size()-1) {

                //loadLastScreen();

            }
        });

        //tabLayout last
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1){
                    //loadLastScreen();
                    next.setText("Get INSIDE");
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadLastScreen();
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void loadLastScreen() {
        Intent intent = new Intent(getApplicationContext(), GenerateOTPActivity.class );
        startActivity(intent);
        finish();
    }

}