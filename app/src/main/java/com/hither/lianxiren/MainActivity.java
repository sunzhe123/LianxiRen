package com.hither.lianxiren;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hither.lianxiren.adapter.SortAdapter;
import com.hither.lianxiren.model.ContactMember;
import com.hither.lianxiren.model.SortModel;
import com.hither.lianxiren.util.GetPersion;
import com.hither.lianxiren.util.PinYinKit;
import com.hither.lianxiren.util.PinyinComparator;
import com.hither.lianxiren.views.SearchEditText;
import com.hither.lianxiren.views.SideBar;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public PinyinComparator comparator = new PinyinComparator();
    private TextView userListNumTxt;
    private String userListNumStr;
    private SideBar sideBar;
    private ListView sortListView;
    private TextView dialogTxt;
    private SearchEditText mSearchEditText;
    private SortAdapter adapter;
    private List<SortModel> sortModelList;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        userListNumTxt = (TextView) findViewById(R.id.txt_user_list_user_num);
        sideBar = (SideBar) findViewById(R.id.sild_bar);
        dialogTxt = (TextView) findViewById(R.id.txt_dialog);
        sideBar.setmTextDialog(dialogTxt);

        // on touching listener of side bar
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            public void onTouchingLetterChanged(String str) {
                int position = adapter.getPositionForSection(str.charAt(0));
                if (position != -1)
                    sortListView.setSelection(position);
            }
        });

        sortListView = (ListView) findViewById(R.id.list_view_user_list);
        sortListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Toast.makeText(getApplicationContext(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
            }
        });


        // call filledData to get datas
        try {
//            sortModelList = filledData(getResources().getStringArray(R.array.date));
            ArrayList<ContactMember> contact = GetPersion.getContact(this);
            sortModelList = filledData(contact);
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }

        userListNumTxt.setText("全部：" + "\t" + sortModelList.size() + "个联系人");
        List<SortModel> sortModels = downloadData(sortModelList);
        Log.i("Main", "===>sortModels:" + sortModels);
        // sort by a-z
        Collections.sort(sortModelList, comparator);
        adapter = new SortAdapter(getApplicationContext(), sortModelList);
        sortListView.setAdapter(adapter);


        // search
        mSearchEditText = (SearchEditText) findViewById(R.id.txt_filter_edit);

        // filter
        mSearchEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
                try {
                    filerData(str.toString());
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    private List<SortModel> filledData(ArrayList<ContactMember> contact) throws BadHanyuPinyinOutputFormatCombination {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < contact.size(); i++) {
            SortModel sortModel = new SortModel();
            String contact_name = contact.get(i).getContact_name();
            sortModel.setName(contact_name);
            //汉字转换成拼音
            String pinyin = PinYinKit.getPingYin(contact_name);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            sortModel.setInfo(contact.get(i).getContact_phone());
            mSortList.add(sortModel);
        }
        return mSortList;

    }

    private void filerData(String str) throws BadHanyuPinyinOutputFormatCombination {
        List<SortModel> fSortModels = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(str))
            fSortModels = sortModelList;
        else {
            fSortModels.clear();
            for (SortModel sortModel : sortModelList) {
                String name = sortModel.getName();
                if (name.indexOf(str.toString()) != -1 ||
                        PinYinKit.getPingYin(name).startsWith(str.toString()) || PinYinKit.getPingYin(name).startsWith(str.toUpperCase().toString())) {
                    fSortModels.add(sortModel);
                }
            }
        }
        Collections.sort(fSortModels, comparator);
        adapter.updateListView(fSortModels);
    }

    public void changeDatas(List<SortModel> mSortList, String str) {
        userListNumTxt.setText(str + "：" + "\t" + mSortList.size() + "个联系人");

        Collections.sort(mSortList, comparator);
        adapter.updateListView(mSortList);
    }


    public List<SortModel> downloadData(final List<SortModel> list) {
        final List<SortModel> listUSER = new ArrayList<>();
        StringRequest stringRequest = null;
        for (int i = 0; i < list.size(); i++) {
            String phones = list.get(i).getInfo().toString();
            final String number = phones.replace(" ", "");
            final String display_name = list.get(i).getName();
            final String sortKey = list.get(i).getSortLetters();
            stringRequest = new StringRequest(Request.Method.GET, "http://peng.hither.com.cn/hqhb/personInt/find?name=" + number, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        userid = jsonObject.getString("easemobName");
                        SortModel sortModel = new SortModel();
                        sortModel.setName(display_name);
                        sortModel.setInfo(number);
                        sortModel.setSortLetters(sortKey);
                        if ("null".equals(userid)) {
                            sortModel.setInvite("邀请");
                            sortModel.setUserid("null");
                        } else {
                            sortModel.setInvite("添加");
                            sortModel.setUserid(userid);
                        }
                        listUSER.add(sortModel);
                        //  Log.i("Mian", "==>listUSER:" + listUSER);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "网络无响应",
                            Toast.LENGTH_SHORT).show();
                }
            });

        }
        Log.i("Mian", "==>listUSER:" + listUSER);
        MyApplication.requestQueue.add(stringRequest);
        return listUSER;
    }
}
