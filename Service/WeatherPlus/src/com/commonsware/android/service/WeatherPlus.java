/***
	Copyright (c) 2008-2009 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.commonsware.android.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

public class WeatherPlus extends Activity {
	private static final int CLOSE_ID = Menu.FIRST+1;
	private IWeather service=null;
	private Intent serviceIntent=null;
	private WebView browser;
	private ServiceConnection svcConn=new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
										IBinder binder) {
			service=IWeather.Stub.asInterface(binder);
			
			browser.postDelayed(new Runnable() {
				public void run() {
					updateForecast();
				}
			}, 1000);
		}

		public void onServiceDisconnected(ComponentName className) {
			service=null;
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		browser=(WebView)findViewById(R.id.webkit);
		serviceIntent=new Intent(this, WeatherPlusService.class);
		bindService(serviceIntent, svcConn, BIND_AUTO_CREATE);
	}
	
	@Override
	public void onResume() {
		super.onResume();

		registerReceiver(receiver,
					new IntentFilter(WeatherPlusService.BROADCAST_ACTION));
	}
	
	@Override
	public void onPause() {
		super.onPause();

		unregisterReceiver(receiver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unbindService(svcConn);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, CLOSE_ID, Menu.NONE, "Close")
				.setIcon(R.drawable.eject)
				.setAlphabeticShortcut('c');

		return(super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case CLOSE_ID:
				finish();
				return(true);
		}

		return(super.onOptionsItemSelected(item));
	}
	
	private void updateForecast() {
		try {
			String page=service.getForecastPage();
			
			if (page==null) {
				browser.postDelayed(new Runnable() {
					public void run() {
						updateForecast();
					}
				}, 4000);
			
				Toast
					.makeText(this, "No forecast available", 2500)
					.show();
			}
			else {
				browser.loadDataWithBaseURL(null, page, "text/html",
																		"UTF-8", null);
			}
		}
		catch (final Throwable t) {
			svcConn.onServiceDisconnected(null);

			runOnUiThread(new Runnable() {
				public void run() {
					goBlooey(t);
				}
			});
		}
	}
	
	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		
		builder
			.setTitle("Exception!")
			.setMessage(t.toString())
			.setPositiveButton("OK", null)
			.show();
	}
	
	private BroadcastReceiver receiver=new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			runOnUiThread(new Runnable() {
				public void run() {
					updateForecast();
				}
			});
		}
	};
}

