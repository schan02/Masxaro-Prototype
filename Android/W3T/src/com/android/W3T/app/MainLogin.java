/*
 * MainLogin.java -- Android app's entry and login screen 
 *
 *  Copyright 2011 World Three Technologies, Inc. 
 *  All Rights Reserved.
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Written by Yichao Yu <yichao@Masxaro>
 * 
 *  This activity is the main entry. User login in our system on this screen.
 */

package com.android.W3T.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainLogin extends Activity {
	public static final String VALIDATION_PASSED = "com.android.W3T.app.intent.action.VALIDATION_PASSED";
	private Button reset, submit;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_login);
        
        submit = (Button)findViewById(R.id.submit_button);
        reset = (Button)findViewById(R.id.reset_button);
        
        submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent submit_intent = new Intent();
				submit_intent.setAction(VALIDATION_PASSED);
				startActivity(submit_intent);
			}
        });
    }
}