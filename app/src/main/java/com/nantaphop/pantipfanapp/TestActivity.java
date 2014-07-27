package com.nantaphop.pantipfanapp;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

import static com.nantaphop.pantipfanapp.service.PantipRestClient.*;

@EActivity(R.layout.activity_test)
public class TestActivity extends Activity {
    @Bean
    PantipRestClient client;

    @AfterViews
    void test(){


    }

    @Click
    void login(){
        client.login("ป๋าตุ๊กกับหนูแพรว","12498000", new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                for(Header h: headers){
                    Log.d("header",h.getName()+"("+h.getValue().length()+") : "+h.getValue());
                }
                try {
                    Log.d(("body"), new String(bytes, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
        Toast.makeText(this, "ssss", Toast.LENGTH_SHORT).show();
    }

    @Click
    void comment(){
        client.comment("30012560", "Test", new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                for(Header h: headers){
                    Log.d("header",h.getName()+"("+h.getValue().length()+") : "+h.getValue());
                }
                try {
                    Log.d(("body"), new String(bytes, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    @Click
    void getForum(){
        client.getForum("mbk", ForumType.Room, TopicType.All_Except_Sell, 1, "0", true, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                for(Header h: headers){
                    Log.d("header",h.getName()+"("+h.getValue().length()+") : "+h.getValue());
                }
                try {
                    Log.d(("body"), new String(bytes, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }


}
