package panda.li.navigation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.view.KeyEvent;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import panda.li.navigation.R;
import panda.li.navigation.base.BaseActivity;
import panda.li.navigation.fragment.MineFragment;
import panda.li.navigation.fragment.MyMapFragment;
import panda.li.navigation.fragment.NearFragment;
import panda.li.navigation.fragment.NewMainFragment;
import panda.li.navigation.service.LocationService;
import panda.li.navigation.utils.ToastUtil;

public class MainActivity extends BaseActivity {


    @Bind(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;

    //主界面
    private NewMainFragment mainFragment;
    //地图界面
    private MyMapFragment myMapFragment;
    //我的界面
    private MineFragment mineFragment;
    //附近界面
    private NearFragment nearFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initBottomNavigationBar();
    }

    private void initBottomNavigationBar() {
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.main, "首页")
                .setActiveColorResource(R.color.tabTextColor)).addItem(new BottomNavigationItem(R
                .drawable.mymap, "地图").setActiveColorResource(R.color.tabTextColor)).addItem(new
                BottomNavigationItem(R.drawable.near, "附近").setActiveColorResource(R.color
                .tabTextColor)).addItem(new BottomNavigationItem(R.drawable.mine, "我的")
                .setActiveColorResource(R.color.tabTextColor)).setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (position) {
                    case 0:
                        if (mainFragment == null) {
                            mainFragment = NewMainFragment.newInstance();
                        }
                        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
                        break;
                    case 1:
                        if (myMapFragment == null) {
                            myMapFragment = MyMapFragment.newInstance();
                        }
                        fragmentTransaction.replace(R.id.fragment_container, myMapFragment);
                        break;
                    case 2:
                        if (nearFragment == null) {
                            nearFragment = NearFragment.newInstance();
                        }
                        fragmentTransaction.replace(R.id.fragment_container, nearFragment);
                        break;
                    case 3:
                        if (mineFragment == null) {
                            mineFragment = MineFragment.newInstance();
                        }
                        fragmentTransaction.replace(R.id.fragment_container, mineFragment);
                        break;
                }
                fragmentTransaction.commit();
            }

            @Override
            public void onTabUnselected(int position) {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (position) {
                    case 0:
                        if (mainFragment != null) {
                            fragmentTransaction.remove(mainFragment);
                        }
                        break;
                    case 1:
                        if (myMapFragment != null) {
                            fragmentTransaction.remove(myMapFragment);
                        }
                        break;
                    case 2:
                        if (nearFragment != null) {
                            fragmentTransaction.remove(nearFragment);
                        }
                        break;
                    case 3:
                        if (mineFragment != null) {
                            fragmentTransaction.remove(mineFragment);
                        }
                        break;
                }
                fragmentTransaction.commit();
            }

            @Override
            public void onTabReselected(int position) {

            }
        });
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mainFragment = NewMainFragment.newInstance();
        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();
    }

    /**
     * 双击返回退出
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.show(this, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                Intent stopService = new Intent(this, LocationService.class);
                stopService(stopService);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
