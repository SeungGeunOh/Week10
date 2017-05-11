package com.example.oh.week10;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    WebView webView;
    ListView listView;
    ArrayList<Bookmark> bookmarkArrayList = new ArrayList<>();
    ListAdapter adapter;
    LinearLayout linearLayout;
    EditText url;
    ProgressDialog progressDialog;
    Handler handler = new Handler();
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MyWeb");
        webView = (WebView)findViewById(R.id.webView);
        listView = (ListView)findViewById(R.id.listview);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        progressDialog = new ProgressDialog(this);
        url = (EditText)findViewById(R.id.editText);
        url.setOnClickListener(this);

        adapter = new ListAdapter(this, bookmarkArrayList);
        listView.setAdapter(adapter);

        webView.addJavascriptInterface(new JavaScriptMethods(), "ADDstr");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = adapter.getUrl(position);
                listView.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                final int num = position;
                dlg.setTitle("정말로")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("삭제 하시겠습니까 ?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bookmarkArrayList.remove(num);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "삭제 되었습니다.",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("아니요", null)
                        .show();
                return false;
            }
        });


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.setMessage("Loading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) progressDialog.dismiss();
            }
        });
    }

    public void onClick(View v){
        if (v.getId() == R.id.button) {
            String str = url.getText().toString();
            if (str.substring(0, 7) != "http://")
                str = "http://" + str;
            webView.loadUrl(str);
            url.setText("");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmark, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add){
            listView.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl("file:///android_asset/urladd.html");

            animation = AnimationUtils.loadAnimation(this, R.anim.anim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    linearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            linearLayout.setAnimation(animation);
            animation.start();

            return true;
        }
        else if (item.getItemId() == R.id.content){
            listView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class JavaScriptMethods {
        JavaScriptMethods() {}

        @JavascriptInterface
        public void siteAdd(final String name, final String url){
            handler.post(new Runnable(){
                @Override
                public void run() {
                    if (adapter.findUrl(url)) {
                        bookmarkArrayList.add(new Bookmark(name, url));
                        adapter = new ListAdapter(getApplicationContext(), bookmarkArrayList);
                        listView.setAdapter(adapter);
                        Toast.makeText(getApplicationContext(), "목록이 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else
                        webView.loadUrl("javascript: displayMsg()");
                }
            });
        }

        @JavascriptInterface
        public void behind(){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
