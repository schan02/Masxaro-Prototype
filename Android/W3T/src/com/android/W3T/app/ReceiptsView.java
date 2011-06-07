/*
 * ReceiptView.java -- View the details of the receipts 
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
 *  NUM_RECEIPT receipts will be listed in the activity. The detail of one of them 
 *  will be shown.
 */

package com.android.W3T.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class ReceiptsView extends Activity {
	public final static int NUM_RECEIPT = 7;
	public final static int NUM_RECEIPT_ITEM = 4;
	
	public final static String[][] FakeReceipts = {
		{"ID@1234", "06-01-2011", "Wendy's", "12.32USD"},
		{"ID@1234", "06-02-2011", "Starbucks", "4.63USD"},
		{"ID@1234", "06-02-2011", "J Street", "10.02USD"},
		{"ID@1234", "06-03-2011", "Starbucks", "4.63USD"},
		{"ID@1234", "06-03-2011", "Penn Grill", "8.76USD"},
		{"ID@1234", "06-04-2011", "Starbucks", "7.56USD"},
		{"ID@1234", "06-04-2011", "Wendy's", "6.22USD"}
	};
	
	public final static int[] ReceiptViewElements = {
		R.id.id_text, R.id.date_text, R.id.store_name_text, R.id.total_cost_text
	};
	
	private int mCurReceipt = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_view);
	}
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			getPrevReceipt(mCurReceipt);
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			getNextReceipt(mCurReceipt);
			return true;
		default:
			return false;
		}
	}
	
	private void getPrevReceipt(int num) {
		fillReceiptView((num+6)/NUM_RECEIPT);
		mCurReceipt = (num+6)%NUM_RECEIPT;
		System.out.println(mCurReceipt);
	}
	
	private void getNextReceipt(int num) {
		fillReceiptView((num+1)/NUM_RECEIPT);
		mCurReceipt = (num+1)%NUM_RECEIPT;
		System.out.println(mCurReceipt);
	}
	
	private void fillReceiptView(int item_num) {
		for (int i = 0;i < NUM_RECEIPT_ITEM;i++) {
			((TextView)findViewById(ReceiptViewElements[i]))
				.setText(FakeReceipts[item_num][i]);
		}
	}
}
