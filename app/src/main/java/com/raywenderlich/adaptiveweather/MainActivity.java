package com.raywenderlich.adaptiveweather;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2017 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class MainActivity extends AppCompatActivity {

  private RecyclerView mRecyclerView;
  private List<Location> mLocations = new ArrayList<Location>();
  private LocationAdapter mLocationAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mRecyclerView = (RecyclerView) findViewById(R.id.list);
    mRecyclerView.setHasFixedSize(true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
    mRecyclerView.addItemDecoration(dividerItemDecoration);
    mRecyclerView.setLayoutManager(layoutManager);

    Log.d("App", String.format("Dimensions %f", Resources.getSystem().getDisplayMetrics().density));
    loadData();

    mLocationAdapter = new LocationAdapter(mLocations, new LocationAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(Location location) {
        FlexboxLayout forecastView = (FlexboxLayout) findViewById(R.id.forecast);
        for (int i = 0; i < forecastView.getChildCount(); i++) {
          AppCompatImageView dayView = (AppCompatImageView) forecastView.getChildAt(i);
          dayView.setImageDrawable(mapWeatherToDrawable(location.getForecast().get(i)));
        }
      }
    });
    if (savedInstanceState != null) {
      mLocationAdapter.setSelectedLocation(savedInstanceState.getString("locations"));
    }
    mRecyclerView.setAdapter(mLocationAdapter);
  }

  private Drawable mapWeatherToDrawable(String weather) {
    int drawableId = 0;
    switch (weather) {
      case "sun":
        drawableId = R.drawable.ic_sun;
        break;
      case "rain":
        drawableId = R.drawable.ic_rain;
        break;
      case "fog":
        drawableId = R.drawable.ic_fog;
        break;
      case "thunder":
        drawableId = R.drawable.ic_thunder;
        break;
      case "cloud":
        drawableId = R.drawable.ic_cloud;
        break;
      case "snow":
        drawableId = R.drawable.ic_snow;
        break;
    }
    return getResources().getDrawable(drawableId);
  }

  private void loadData() {
    String json = null;
    try {
      InputStream is = getAssets().open("data.json");
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    JSONArray array = null;
    try {
      array = new JSONArray(json);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    for (int i = 0; i < array.length(); i++) {
      try {
        JSONObject object = (JSONObject) array.get(i);
        JSONArray stringArray = (JSONArray) object.get("forecast");
        List<String> forecast = new ArrayList<String>();
        for (int j = 0; j < stringArray.length(); j++) {
          forecast.add(stringArray.getString(j));
        }
        Location location = new Location((String) object.get("name"), forecast);
        mLocations.add(location);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("locations", mLocationAdapter.getSelectedLocation());
  }

}
