package panda.li.navigation.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import panda.li.navigation.R;
import panda.li.navigation.activity.ActivityQcode;
import panda.li.navigation.activity.BusLineActivity;
import panda.li.navigation.base.MyApplication;
import panda.li.navigation.callback.BusLineCallBack;
import panda.li.navigation.constant.Constant;
import panda.li.navigation.entity.BusLine;
import panda.li.navigation.utils.ToastUtil;

public class MineFragment extends Fragment {

    private static int QCODE = 1;

    private Constant constant;

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        constant = Constant.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.set_head, R.id.set_name, R.id.ll_city_change, R.id.ll_sao, R.id.ll_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_head:
                break;
            case R.id.set_name:
                break;
            case R.id.ll_city_change:
                break;
            case R.id.ll_sao:
                Intent intent = new Intent(getActivity(), ActivityQcode.class);
                startActivityForResult(intent, QCODE);
                break;
            case R.id.ll_set:
                break;
        }
    }

    //解析车站
    private void searchBusLineByStationName(String text) {
        OkHttpUtils.get().url(Constant.LOAD_BUS_LINE_BY_STATION)
                .addParams
                        ("BusStationName", text)
                .build()
                .execute(new BusLineCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show(MyApplication.getInstance(), e.getMessage());
                    }

                    @Override
                    public void onResponse(BusLine response, int id) {
                        if (response.getStatus().equals("成功")) {
                            if (response.getBusLines() != null) {
                                List<BusLine.BusLinesBean> list = response.getBusLines();
                                if (list.size() > 0) {
                                    Intent intent = new Intent(getActivity(), BusLineActivity
                                            .class);
                                    intent.putExtra("list", (Serializable) list);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            ToastUtil.show(getActivity(), "无查询结果");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QCODE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    constant.setStationName(result);
                    searchBusLineByStationName(result);
                    ToastUtil.show(getActivity(), result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.show(getActivity(), "解析二维码失败");
                }
            }
        }
    }
}
