package com.rs.pricetrigger;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final Context myApp = this;
    final String filename = "targets";
    final String fileContents = "Hello world!";
    List<String> triggers = new ArrayList<String>();
    public void readtriggers(){
        FileInputStream fis = null;
        try {
            fis = this.openFileInput(filename);
        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(this,"You do not have any triggers set up!",Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
            Toast toast = Toast.makeText(this,"You do not have any triggers set up!",Toast.LENGTH_LONG);
            toast.show();
        } finally {
            String contents = stringBuilder.toString();
        }
    }
    public void writetriggers(){
        try (FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(triggers.toString().getBytes());
            //fileContents.getBytes()
        }catch (Exception e){
            Toast toast = Toast.makeText(this,"An unknown error ocurred!",Toast.LENGTH_LONG );
            toast.show();
        }
    }
    public void getPrices(String html){
        Document doc = Jsoup.parse(html);
        //Elements containers = doc.getElementsByClass("pla-unit-container");
        //Not anymore! These are shopping cards
        //Elements containers = doc.getElementsByClass("sh-pr__product-result");
        Elements containers = doc.getElementsByAttributeValueContaining("class","sh-pr__product-result");
        Log.d("how many",""+containers.toArray().length);
        //go through each pla-unit-container "e"
        for (Element container:containers){
            //within each shopping card
            final String productname = container.getElementsByClass("sh-ti__ltitle-link").text();
            Log.d("product",productname);
            String costcontainer = container.getElementsByClass("sh-pr__secondary-content").text();
            //Log.d("full text",cost);

            String cost = costcontainer.split(" ")[0];
            /*costcontainer = costcontainer.replace(cost,"");
            costcontainer = costcontainer.replace("Was","");
            costcontainer = costcontainer.replace("now","");
            //costcontainer = costcontainer.replace("formonths","");
            costcontainer = costcontainer.replaceAll("[^a-zA-Z]","");
            costcontainer = costcontainer.replace("formonths","");
            final String store = costcontainer;*/
            final String store2 = container.getElementsByClass("sh-pr__secondary-content").get(0).children().get(1).text();
            //Elements sec = container.getElementsByClass("sh-pr__secondary-content");

            //String store = container.getElementsByClass("sh-pr__secondary-content")
            //final String store = cost.split(" ")[1];

            Log.d("item",""+productname+" costs "+ cost);
            final String finalCost = cost;
            runOnUiThread(new Runnable() //run on ui thread
            {
                public void run()
                {
                    LinearLayout linlay = findViewById(R.id.mlinearlayout);
                    final TextView rowTextView = new TextView(myApp);
                    linlay.removeAllViews();
                    // set some properties of rowTextView or something
                    rowTextView.setText(""+productname+" costs "+ finalCost+" at "+store2);

                    // add the textview to the linearlayout
                    linlay.addView(rowTextView);

                    // save a reference to the textview for later
                    //myTextViews[i] = rowTextView;
                }
            });

        }
        //TextView result = findViewById(R.id.textView);
        //result.setText("There are "+elements.toArray().length+" offers of this product");
    }
    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html)
        {
            // process the html as needed by the app
            getPrices(html);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView browser = (WebView)findViewById(R.id.browser);
        /* JavaScript must be enabled if you want it to work, obviously */
        browser.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = browser.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        /* Register a new JavaScript interface called HTMLOUT */
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        /* WebViewClient must be set BEFORE calling loadUrl! */
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                /* This call inject JavaScript into the page which just finished loading. */
                //browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                browser.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }
        });
        //browser.loadUrl("http://lexandera.com/files/jsexamples/gethtml.html");

    }

//public  WebView browser = findViewById(R.id.browser);
    /* load a web page */
    public void loadproduct(String url){
        final WebView browser = (WebView)findViewById(R.id.browser);
        /* JavaScript must be enabled if you want it to work, obviously */
        //browser.getSettings().setJavaScriptEnabled(true);

        /* Register a new JavaScript interface called HTMLOUT */
        //browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        browser.loadUrl(url);
    }

    public void clickbutton(View v){
        EditText urlbox = findViewById(R.id.website);
        String query = urlbox.getText().toString();
        query = query.replace(" ","+");
        //String url = "https://www.google.com/search?ei=ZIxZX5GnIKLAytMP7YqEkA8&q="+query+"&oq="+query+"&gs_lcp=CgZwc3ktYWIQAzIHCAAQsQMQQzICCAAyAggAMgIIADICCAAyBQgAELEDMgIIADICCAAyAggAMgIIADoHCAAQRxCwAzoECAAQQ0oFCCYSAW5KBQgnEgExUMIUWJgdYPMeaABwAHgAgAE4iAG7ApIBATaYAQCgAQGqAQdnd3Mtd2l6wAEB&sclient=psy-ab&ved=0ahUKEwiR6eOzwt3rAhUioHIEHW0FAfIQ4dUDCA0&uact=5";
        String url = "https://www.google.com/search?sa=X&biw=1517&bih=697&tbm=shop&ei=GUBlX7uhIMia_QaPhJ6oDA&q="+query+"&oq="+query+"&gs_lcp=Cg5tb2JpbGUtc2gtc2VycBADMgIIADICCAAyAggAMgIIADIECAAQGDIECAAQGDIECAAQGDIECAAQGDoICAAQsQMQgwFQgDhY_ztgqD9oAHAAeACAAXGIAdECkgEDNC4xmAEAoAEBqgESbW9iaWxlLXNoLXdpei1zZXJwwAEB&sclient=mobile-sh-serp";
        url = "https://www.google.com/search?sa=X&biw=1517&bih=697&tbm=shop&ei=GUBlX7uhIMia_QaPhJ6oDA&q="+query+"&oq="+query+"&gs_lcp=CgZwc3ktYWIQAzIHCAAQsQMQQzICCAAyAggAMgIIADICCAAyBQgAELEDMgIIADICCAAyAggAMgIIADoHCAAQRxCwAzoECAAQQ0oFCCYSAW5KBQgnEgExUMIUWJgdYPMeaABwAHgAgAE4iAG7ApIBATaYAQCgAQGqAQdnd3Mtd2l6wAEB&sclient=psy-ab&ved=0ahUKEwiR6eOzwt3rAhUioHIEHW0FAfIQ4dUDCA0&uact=5";
        loadproduct(url);
    }

}
//https://www.google.com/search?sa=X&biw=1517&bih=697&tbm=shop&ei=GUBlX7uhIMia_QaPhJ6oDA&q="+query+"&oq="+query+"&gs_lcp=Cg5tb2JpbGUtc2gtc2VycBADMgIIADICCAAyAggAMgIIADIECAAQGDIECAAQGDIECAAQGDIECAAQGDoICAAQsQMQgwFQgDhY_ztgqD9oAHAAeACAAXGIAdECkgEDNC4xmAEAoAEBqgESbW9iaWxlLXNoLXdpei1zZXJwwAEB&sclient=mobile-sh-serp