package com.android.W3T.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.W3T.app.network.NetworkUtil;
import com.android.W3T.app.rmanager.BasicInfo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class SearchView extends Activity implements OnClickListener {
	public static final String TAG = "SearchViewActivity";

	public static final String TITLE_TEXT = "entry_title";
	public static final String TIME_TEXT = "entry_time";
	public static final String TOTAL_TEXT = "entry_total";
	
	public static final int STORE_ITEM = 0;
	public static final int TAG_NAME = 1;
	public static final int DATE = 2;
	
	private static final int SEVEN_DAYS = 7;
	private static final int FOURTEEN_DAYS = 14;
	private static final int ONE_MONTH = 1;
	private static final int THREE_MONTHS = 3;
	
	private static final int START_DATE_DIALOG_ID = 1;
	private static final int END_DATE_DIALOG_ID = 2;
//	private TextView t;
	
	
	private int mSearchBy = 0;
	private int mSearchRange = SEVEN_DAYS;
	private LinearLayout mDynamicSearchRange;
	private Spinner mSearchBySpinner;
	private Spinner mSearchRangeSpinner;
	private EditText mSearchTerms;
	private ImageButton mSearchBtn;
	private EditText mStartText;
	private String mStartDate;
	private EditText mEndText;
	private String mEndDate;
	private ListView mResultList;
	private ProgressDialog mSearchProgress;
	private Handler mSearchHandler = new Handler();
	private Runnable mSearchThread = new Runnable() {
		@Override
		public void run() {
			// TODO: upload the receipt with FROM_NFC flag
			// Upload non-uploaded receipts
			String result = new String();
			String text = mSearchTerms.getText().toString();
			String[] terms;
//            NetworkUtil.syncUnsentReceipts();
			switch (mSearchBy) {
			case STORE_ITEM:
				Log.i(TAG, "search the terms by key word");
				
				if (!text.equals("")) {
					terms = text.split(" ");
					result = NetworkUtil.attemptSearch("key_search", 0-mSearchRange, terms);
//					t.setText(NetworkUtil.attemptSearch("key_search", 0-mSearchRange, terms));
				}
				else {
					result = NetworkUtil.attemptSearch("key_search", 0-mSearchRange, null);
//					t.setText(NetworkUtil.attemptSearch("key_search", 0-mSearchRange, null));
				}
				createSearchResultList(searchResultDecode(result));
				break;
			case TAG_NAME:
				Log.i(TAG, "search the terms by tag");
				
				break;
			case DATE:
				Log.i(TAG, "search the terms by date");
				// TODO: add if statement for no date selecting.
				if (!mStartDate.equals("") && !mEndDate.equals("")) {
					text = text + " " + mStartDate + " " + mEndDate;
					terms = text.split(" ");
					result = NetworkUtil.attemptSearch("time_search", 0-mSearchRange, terms);
					// Get the basic info of the hit receipts from the result
					// Create the result list.
					createSearchResultList(searchResultDecode(result));
				}
				else {
					Toast.makeText(SearchView.this, "Pleae select date", Toast.LENGTH_SHORT).show();
				}
				
				break;
			default:
				break;
			}
			
			mSearchProgress.dismiss();
		}
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.receipt_search);
	    mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
	    mSearchBtn.setOnClickListener(this);
	    
	    mSearchTerms = (EditText) findViewById(R.id.search_terms);
	    
//	    t=(TextView) findViewById(R.id.tmp);
	    mDynamicSearchRange = (LinearLayout) findViewById(R.id.dynamic_search_range);
	    setSearchSpinner();
	    
	    mResultList = (ListView) findViewById(R.id.search_result_list);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mStartDate = "";
	    mEndDate = "";
	}
	
	@Override
	public void onClick(View v) {
		if (v == mSearchBtn) {
			// Show a progress bar and do the search.
			mSearchProgress = new ProgressDialog(SearchView.this);
			mSearchProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mSearchProgress.setMessage("Syncing...");
			mSearchProgress.setCancelable(true);
			mSearchProgress.show();
			mSearchHandler.post(mSearchThread);
		}
	}
	
	private void setSearchSpinner() {
		LayoutInflater l = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View spinner = l.inflate(R.layout.search_range_spinner, null);
		// Set Search By spinner, which is not in the search_range_spinner.xml
		mSearchBySpinner = (Spinner) findViewById(R.id.search_by_spinner);
	    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
	            this, R.array.search_by, android.R.layout.simple_spinner_item);
	    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mSearchBySpinner.setAdapter(adapter1);
	    
	    mSearchBySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				mSearchBy = pos;
				if (pos == DATE) {
					mDynamicSearchRange.removeAllViews();
					View range =((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_range_view, null);
					mStartText = (EditText) range.findViewById(R.id.start_date);
					mStartText.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							showDialog(START_DATE_DIALOG_ID);
						}
					});
					mEndText = (EditText) range.findViewById(R.id.end_date);
					mEndText.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							showDialog(END_DATE_DIALOG_ID);
						}
					});
					mDynamicSearchRange.addView(range);
				}
				else {
					mDynamicSearchRange.removeAllViews();
					mDynamicSearchRange.addView(spinner);
				}
				Toast.makeText(parent.getContext(), "Search By " + parent.getItemAtPosition(pos).toString()
						, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
	    });
	    
	    // Set Search Range spinner
	    mSearchRangeSpinner = (Spinner) spinner.findViewById(R.id.search_range_spinner);
	    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
	            this, R.array.search_range, android.R.layout.simple_spinner_item);
	    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mSearchRangeSpinner.setAdapter(adapter2);
	    
	    mSearchRangeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				setDateRange(pos);
				Toast.makeText(parent.getContext(), "Search Range " + parent.getItemAtPosition(pos).toString()
						, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
	    });
	    mDynamicSearchRange.addView(spinner);
	}
	
	private void setDateRange(int pos) {
    	switch (pos) {
		case 0:
			mSearchRange = SEVEN_DAYS;
			break;
		case 1:
			mSearchRange = FOURTEEN_DAYS;
			break;
		case 2:
			mSearchRange = ONE_MONTH;
			break;
		case 3:
			mSearchRange = THREE_MONTHS;
			break;
		default:
			break;
		}
    }
	
	private ArrayList<BasicInfo> searchResultDecode(String r) {
		try {
			JSONArray result = new JSONArray(r);
			int num = result.length();
			ArrayList<BasicInfo> basics = new ArrayList<BasicInfo>();
			for (int i=0;i<num;i++) {
				JSONObject b = result.getJSONObject(i);
				basics.add(new BasicInfo(b));
			}
			return basics;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	private void createSearchResultList(ArrayList<BasicInfo> b) {
		Log.i(TAG, "add category entry");
		if (b.size() == 0) {
			Toast.makeText(SearchView.this, "No results returned", Toast.LENGTH_SHORT).show();
			mResultList.setAdapter(null);
		}
		else {
			ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
	        
	        // First, add the category line.
	        HashMap<String, Object> category = new HashMap<String, Object>();
	        category.put(TITLE_TEXT, "Merchant");
	        category.put(TIME_TEXT, "Date");
	        category.put(TOTAL_TEXT, "Total ($)");
	        listItem.add(category);
	        
	        // Secondly, add the result lines.
	        int num_result = b.size();
	        for(int i=0;i<num_result;i++) {
		        HashMap<String, Object> map = new HashMap<String, Object>();
		        map.put(TITLE_TEXT, b.get(i).getStoreName());
		        map.put(TIME_TEXT, b.get(i).getTime().split(" ")[0]);
		        map.put(TOTAL_TEXT, b.get(i).getTotal());
		        listItem.add(map);
	        }  
	        
	        // Create adapter's entries, which corresponds to the elements of the 
	        // above dynamic array.  
	        ResultListAdapter listAdapter = new ResultListAdapter(this, listItem, 
	            R.layout.search_result_entry,
	            new String[] {TITLE_TEXT, TIME_TEXT, TOTAL_TEXT},   
	            new int[] {R.id.list_search_title,R.id.list_search_time, R.id.list_search_total}
	        );
	        
	        mResultList.setAdapter(listAdapter);
	        
	        mResultList.setOnItemClickListener(new OnItemClickListener() {  
	  			@Override
				public void onItemClick(AdapterView<?> parent, View view, int pos,
						long id) {
	  				// TODO: Only view the result. So the receipt view activity should be only.
	  				final Intent receipt_view_intent = new Intent(SearchView.this, ReceiptsView.class);
	  				receipt_view_intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	  				receipt_view_intent.putExtra("pos", pos-1);
	  				startActivity(receipt_view_intent);
				}  
	        });	
		}
        
	}
	
	// the callback received when the user "sets" the date in the dialog
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case START_DATE_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    new OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								mStartDate = new StringBuilder()
								.append(year).append("-")
			                    // Month is 0 based so add 1
			                    .append(monthOfYear + 1).append("-")
			                    .append(dayOfMonth).toString();
			                    mStartText.setText(mStartDate);
							}
	        			},
	                    2011, 0, 1);
	    case END_DATE_DIALOG_ID:
	        return new DatePickerDialog(this,
		        		new OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								mEndDate = new StringBuilder()
								.append(year).append("-")
			                    // Month is 0 based so add 1
			                    .append(monthOfYear + 1).append("-")
			                    .append(dayOfMonth).toString();
								mEndText.setText(mEndDate);
							}
						},
		                2011, 0, 1);
	    }
	    return null;
	}
	
	// Set the category line's feature.
	private class ResultListAdapter extends SimpleAdapter {

		public ResultListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				View row = super.getView(position, convertView, parent);
		        TextView title = (TextView) row.findViewById(R.id.list_search_title);
		        title.setTextColor(getResources().getColor(R.color.white));
		        TextView date = (TextView) row.findViewById(R.id.list_search_time);
		        date.setTextColor(getResources().getColor(R.color.white));
		        TextView total = (TextView) row.findViewById(R.id.list_search_total);
		        total.setTextColor(getResources().getColor(R.color.white));
		        return super.getView(position, convertView, parent);
			}
			return super.getView(position, convertView, parent);
	    }
	}
}
