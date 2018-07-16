package com.example.fab.nytimesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.fab.nytimesapp.Article;
import com.example.fab.nytimesapp.ArticleArrayAdapter;
import com.example.fab.nytimesapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    //EditText  etQuery;
    GridView gvResults;
    //Button btnSearch;
    ArrayList articles;
    ArticleArrayAdapter adapter;
    String search;
    int page=0;
    private MenuItem searchAction, settingsAction;
    private SearchView searchView;
    static  RequestParams params = new RequestParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpViews();

    }

    public void setUpViews(){
        //etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setNumColumns(2);
        //btnSearch = (Button) findViewById(R.id.btnSearch);
        //btnSearch.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        search = etQuery.getText().toString();
        //        onArticleSearch(search,page);
        //    }
        //});
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);


        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String url = getIntent().getStringExtra("url");
                WebView webView = (WebView) findViewById(R.id.wvArticle);

                Intent i = new Intent(getApplicationContext(),ArticleActivity.class);
                Article article = (Article) articles.get(position);
                i.putExtra("url",article.getWebUrl());
                startActivity(i);

            }
        });

        gvResults.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (i + i1 >= i2){
                    page++;
                    if (!TextUtils.isEmpty(search)){
                        onArticleSearch(search, page);
                    }

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchAction = menu.findItem(R.id.action_search);
        settingsAction = menu.findItem(R.id.action_settings);

        searchView = (SearchView) MenuItemCompat.getActionView(searchAction);
        searchView.setQueryHint("Type to search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                searchView.clearFocus();
                articles.clear();
                page = 0;
                onArticleSearch(query, page);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(SearchActivity.this,FilterActivity.class));
            return true;
        }
        if (id == R.id.action_search){
            searchAction.expandActionView();
            searchView.requestFocus();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(String search, int page) {
        //String query = etQuery.getText().toString();
        //Toast.makeText(this," searching for " + query,Toast.LENGTH_LONG).show();
      AsyncHttpClient client = new AsyncHttpClient();
      String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
      if (TextUtils.isEmpty(params.toString())){
          params = new RequestParams();
          params.put("api-key","c49d3dfaf67d4e10b088e3bb1fdf787f");
          params.put("page",page);
          params.put("q",search);

      }
      else {
          params.put("api-key","c49d3dfaf67d4e10b088e3bb1fdf787f");
          params.put("page",page);
          params.put("q",search);
      }
      if (params.toString().contains("sort")) {

          Log.d("PARAMS", params.toString());
          client.get(url, params, new JsonHttpResponseHandler() {
              @Override
              public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  JSONArray articleJsonResults;
                  try {
                      articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                      // adapter.notifyDataSetChanged();
                      //Log.d("DEBUG",articleJsonResults.toString());
                      adapter.addAll(Article.fromJSONArray(articleJsonResults));
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }
          });
      }
    }
}
