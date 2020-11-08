package com.rs.pricetrigger;

import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
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
    ArrayList<String> triggers = new ArrayList<String>();
    CustomAdapter ca = new CustomAdapter(triggers,this);

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
                triggers.add(line);
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
        String testprice = "";
        String testshop = "";
        String link = "";
        Elements containers = doc.getElementsByAttributeValueContaining("class","sh-pr__product-result");
        Log.d("how many",""+containers.toArray().length);
        ArrayList<String> results = new ArrayList<>();
        //go through each pla-unit-container "e"
        for (Element container:containers){
            link = container.child(0).attr("href");
            //Log.d("link","The item is at google.com"+link);
            //get the container with the price/shop info
            Elements infocontainers = container.getElementsByClass("sh-pr__secondary-container");
            Log.d("there are",""+infocontainers.toArray().length);

            //first infocontainer > link > 2nd child > 2nd child
            //get link to product and price
            link = infocontainers.get(0).child(0).attr("href");
            testprice = infocontainers.get(0).child(0).child(1).text();
            testprice = testprice.split(" ")[0];
            testprice = testprice.replace("$","");

            if (Integer.parseInt(price.getText().toString()) >= Integer.parseInt(testprice)){
                if (!results.contains(""+testprice+",google.com/"+link)) {
                    results.add("" + testprice + ",google.com/" + link);
                }
            }

           // Log.d("item","It costs "+testprice+" at google.com/"+link);
        }
        if (!results.isEmpty()){
            //do something

        }
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
    ListView triggerlist;
    EditText webname;
    EditText price;
    Button addbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        triggerlist = findViewById(R.id.triggerlist);
        webname = findViewById(R.id.website);
        price = findViewById(R.id.editTextPrice);
        addbutton = findViewById(R.id.loadbutton);
        triggerlist.setAdapter(ca);

        //BROWSER THINGS
        final WebView browser = findViewById(R.id.browser);
        /* JavaScript must be enabled if you want it to work, obviously */
        browser.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
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
                browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                //browser.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }
        });
        //browser.loadUrl("http://lexandera.com/files/jsexamples/gethtml.html");

    }

    /* load a web page */
    public void loadproduct(String url){
        final WebView browser = findViewById(R.id.browser);
        browser.loadUrl(url);
    }

    /*public void clickbutton(View v){
        EditText urlbox = findViewById(R.id.website);
        String query = urlbox.getText().toString();
        query = query.replace(" ","+");
        //String url = "https://www.google.com/search?ei=ZIxZX5GnIKLAytMP7YqEkA8&q="+query+"&oq="+query+"&gs_lcp=CgZwc3ktYWIQAzIHCAAQsQMQQzICCAAyAggAMgIIADICCAAyBQgAELEDMgIIADICCAAyAggAMgIIADoHCAAQRxCwAzoECAAQQ0oFCCYSAW5KBQgnEgExUMIUWJgdYPMeaABwAHgAgAE4iAG7ApIBATaYAQCgAQGqAQdnd3Mtd2l6wAEB&sclient=psy-ab&ved=0ahUKEwiR6eOzwt3rAhUioHIEHW0FAfIQ4dUDCA0&uact=5";
        String url = "https://www.google.com/search?sa=X&biw=1517&bih=697&tbm=shop&ei=GUBlX7uhIMia_QaPhJ6oDA&q="+query+"&oq="+query+"&gs_lcp=Cg5tb2JpbGUtc2gtc2VycBADMgIIADICCAAyAggAMgIIADIECAAQGDIECAAQGDIECAAQGDIECAAQGDoICAAQsQMQgwFQgDhY_ztgqD9oAHAAeACAAXGIAdECkgEDNC4xmAEAoAEBqgESbW9iaWxlLXNoLXdpei1zZXJwwAEB&sclient=mobile-sh-serp";
        url = "https://www.google.com/search?sa=X&biw=1517&bih=697&tbm=shop&ei=GUBlX7uhIMia_QaPhJ6oDA&q="+query+"&oq="+query+"&gs_lcp=CgZwc3ktYWIQAzIHCAAQsQMQQzICCAAyAggAMgIIADICCAAyBQgAELEDMgIIADICCAAyAggAMgIIADoHCAAQRxCwAzoECAAQQ0oFCCYSAW5KBQgnEgExUMIUWJgdYPMeaABwAHgAgAE4iAG7ApIBATaYAQCgAQGqAQdnd3Mtd2l6wAEB&sclient=psy-ab&ved=0ahUKEwiR6eOzwt3rAhUioHIEHW0FAfIQ4dUDCA0&uact=5";
        loadproduct(url);
    }*/
    public void clickbutton(View v){
        EditText urlbox = findViewById(R.id.website);
        String query = urlbox.getText().toString();
        query = query.replace(" ","+");
        //String url = "https://www.google.com/search?ei=ZIxZX5GnIKLAytMP7YqEkA8&q="+query+"&oq="+query+"&gs_lcp=CgZwc3ktYWIQAzIHCAAQsQMQQzICCAAyAggAMgIIADICCAAyBQgAELEDMgIIADICCAAyAggAMgIIADoHCAAQRxCwAzoECAAQQ0oFCCYSAW5KBQgnEgExUMIUWJgdYPMeaABwAHgAgAE4iAG7ApIBATaYAQCgAQGqAQdnd3Mtd2l6wAEB&sclient=psy-ab&ved=0ahUKEwiR6eOzwt3rAhUioHIEHW0FAfIQ4dUDCA0&uact=5";
        String url = "https://www.google.com/search?sa=X&biw=1517&bih=697&tbm=shop&ei=GUBlX7uhIMia_QaPhJ6oDA&q="+query+"&oq="+query+"&gs_lcp=Cg5tb2JpbGUtc2gtc2VycBADMgIIADICCAAyAggAMgIIADIECAAQGDIECAAQGDIECAAQGDIECAAQGDoICAAQsQMQgwFQgDhY_ztgqD9oAHAAeACAAXGIAdECkgEDNC4xmAEAoAEBqgESbW9iaWxlLXNoLXdpei1zZXJwwAEB&sclient=mobile-sh-serp";
        url = "https://www.google.com/search?sa=X&biw=1517&bih=697&tbm=shop&ei=GUBlX7uhIMia_QaPhJ6oDA&q="+query+"&oq="+query+"&gs_lcp=CgZwc3ktYWIQAzIHCAAQsQMQQzICCAAyAggAMgIIADICCAAyBQgAELEDMgIIADICCAAyAggAMgIIADoHCAAQRxCwAzoECAAQQ0oFCCYSAW5KBQgnEgExUMIUWJgdYPMeaABwAHgAgAE4iAG7ApIBATaYAQCgAQGqAQdnd3Mtd2l6wAEB&sclient=psy-ab&ved=0ahUKEwiR6eOzwt3rAhUioHIEHW0FAfIQ4dUDCA0&uact=5";
        loadproduct(url);


        String item = webname.getText().toString();
        String pricevalue = price.getText().toString();
        item = item+","+pricevalue;
        triggers.add(item);
        ca.notifyDataSetChanged();
        //webname.setText("");
        //price.setText("");
    }

}
//https://www.google.com/search?sa=X&biw=1517&bih=697&tbm=shop&ei=GUBlX7uhIMia_QaPhJ6oDA&q="+query+"&oq="+query+"&gs_lcp=Cg5tb2JpbGUtc2gtc2VycBADMgIIADICCAAyAggAMgIIADIECAAQGDIECAAQGDIECAAQGDIECAAQGDoICAAQsQMQgwFQgDhY_ztgqD9oAHAAeACAAXGIAdECkgEDNC4xmAEAoAEBqgESbW9iaWxlLXNoLXdpei1zZXJwwAEB&sclient=mobile-sh-serp