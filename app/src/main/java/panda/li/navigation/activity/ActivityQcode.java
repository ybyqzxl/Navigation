package panda.li.navigation.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import panda.li.navigation.R;
import panda.li.navigation.base.BaseActivity;

/**
 * Created by xueli on 2016/8/17.
 */
public class ActivityQcode extends BaseActivity {
    @Bind(R.id.second_button1)
    ImageView iv_flash;

    private boolean isFlash = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_qcode);
        ButterKnife.bind(this);
        /**
         * 执行扫面Fragment的初始化操作
         */
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        // CodeUtils.setFragmentArgs(captureFragment, com.uuzuche.lib_zxing.R.layout.camera);

        captureFragment.setAnalyzeCallback(analyzeCallback);
        /**
         * 替换我们的扫描控件
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container,
                captureFragment).commit();
    }

    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {


        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            ActivityQcode.this.setResult(RESULT_OK, resultIntent);
            ActivityQcode.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            ActivityQcode.this.setResult(RESULT_OK, resultIntent);
            ActivityQcode.this.finish();
        }
    };

    @OnClick(R.id.second_button1)
    public void onClick() {

        if (!isFlash) {
            CodeUtils.isLightEnable(true);
            isFlash = true;
            iv_flash.setImageResource(R.drawable.light_open);
        } else {
            CodeUtils.isLightEnable(false);
            isFlash = false;
            iv_flash.setImageResource(R.drawable.light_off);
        }


    }
}
