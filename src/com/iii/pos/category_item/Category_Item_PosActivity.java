package com.iii.pos.category_item;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.iii.pos.R;
import com.iii.pos.adapter.Adapter_List_Dishes;
import com.iii.pos.adapter.Adapter_list_Category;
import com.iii.pos.config.JSONParser;
import com.iii.pos.down_upload.FileDownloadThread;
import com.iii.pos.model.Category;
import com.iii.pos.model.Items;

//-----------add to right menu on main activity--------//
public class Category_Item_PosActivity extends Fragment {

	// JSON Node names ---- Constants---------------//
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_CATEGORIES = "categories";
	private static final String TAG_ITEMS = "items";
	private static final String TAG_CID = "category_id";
	private static final String TAG_IID = "item_id";
	private static final String TAG_NAME = "name";
	private static final String TAG_IMG_CATEGORY = "POS/Media/category";
	// ---------------Fields------------------//
	private ListView lv;
	private ArrayList<Category> arr;
	private Adapter_list_Category adb;

	private ArrayList<Items> arr1;
	private Adapter_List_Dishes adb1;
	private TextView tvtitle;
	private ImageButton imbuttom;
	private TextView tvgach;

	// Progress Dialog
	private ProgressDialog pDialog;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	JSONParser jParserItem = new JSONParser();
	ArrayList<HashMap<String, String>> categoriesList;
	// url to get all categories list
	private static String url_all_categories = "";
	private static String url_all_items = "";
	// categories JSONArray
	JSONArray categories = null;
	JSONArray items = null;
	private ArrayList<Bitmap> imgCategoryList = new ArrayList<Bitmap>();
	private ArrayList<String> listimgname= new ArrayList<String>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View categoryLayout = inflater.inflate(R.layout.category_item,
				container, false);
		url_all_categories = getResources().getString(R.string.wsgetcategrory);
		url_all_items = getResources().getString(R.string.wsgetitems);

		try {

			imbuttom = (ImageButton) categoryLayout.findViewById(R.id.btnback);
			tvgach = (TextView) categoryLayout.findViewById(R.id.tvgach);
			tvgach.setVisibility(TextView.GONE);
			imbuttom.setVisibility(ImageButton.GONE);

			tvtitle = (TextView) categoryLayout
					.findViewById(R.id.tvDisplayCateAndItem);
			lv = (ListView) categoryLayout.findViewById(R.id.listView1);
			// loadCategoryInfo();
			new LoadAllCategories().execute();
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View v,
						int position, long arg3) {

					int cate_id = arr.get(position).getCtegory_id();
					Log.i("Log : ", "Cate ID : " + cate_id);
					tvtitle.setText(String.valueOf(arr.get(position).getName()));
					imbuttom.setVisibility(ImageButton.VISIBLE);
					tvgach.setVisibility(TextView.VISIBLE);

					new LoadAllItems(cate_id).execute();
					// loadItemInfo(cate_id);

				}
			});

			imbuttom.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					loadCategoryInfo();
					imbuttom.setVisibility(ImageButton.GONE);
					tvgach.setVisibility(TextView.GONE);
					tvtitle.setText("Category");
				}
			});

		} catch (Exception e) {
			Log.i("Log : ", "Exception : " + e.getMessage());
		}
		return categoryLayout;
	}

	private void loadCategoryInfo() {
		// Category cate1 = new Category();
		// cate1.setName("Các MónLẩu");
		// cate1.setDescription("Miền Bắc, Miền Trung, Miền Nam");
		// cate1.setCtegory_id(1);
		// Category cate2 = new Category();
		// cate2.setName("Các Món Nhậu");
		// cate2.setDescription("Miền Bắc, Miền Trung, Miền Nam");
		// cate2.setCtegory_id(2);
		// Category cate3 = new Category();
		// cate3.setName("Các Món Súp");
		// cate3.setDescription("Miền Bắc, Miền Trung, Miền Nam");
		// cate3.setCtegory_id(3);
		// Category cate4 = new Category();
		// cate4.setName("Các Món Vịt");
		// cate4.setDescription("Miền Bắc, Miền Trung, Miền Nam");
		// cate4.setCtegory_id(4);
		// Category cate5 = new Category();
		// cate5.setName("Các Món Thịt Chó");
		// cate5.setDescription("Miền Bắc, Miền Trung, Miền Nam");
		// cate5.setCtegory_id(5);
		// Category cate6 = new Category();
		// cate6.setName("Các Món Thịt Trâu");
		// cate6.setDescription("Miền Bắc, Miền Trung, Miền Nam");
		// cate6.setCtegory_id(6);
		//
		// arr = new ArrayList<Category>();
		// arr.add(cate1);
		// arr.add(cate2);
		// arr.add(cate3);
		// arr.add(cate4);
		// arr.add(cate5);
		// arr.add(cate6);

		if (arr != null) {
			
			adb = new Adapter_list_Category(getActivity()
					.getApplicationContext(), R.layout.category_item, arr);
			adb.setImg_category(listimgname);
			lv.setAdapter(adb);
		}

	}

	private void loadItemInfo(int cate_id) {

		// Items item1 = new Items();
		// item1.setName("Súp Gà");
		// item1.setPrice(123);
		// item1.setDescription("Súp gà là món ăn bổ dưỡng thơm ngon");
		// Items item2 = new Items();
		// item2.setName("Cá Kho");
		// item2.setPrice(42);
		// item2.setDescription("Cá kho ngon ngọt tự nhiên với hương vị cá đồng");
		// Items item3 = new Items();
		// item3.setName("Canh rau Muống");
		// item3.setPrice(63);
		// item3.setDescription("Rau Muống là món ăn dân tộc có hương vị của quê hương..");
		// Items item4 = new Items();
		// item4.setName("Lẩu Mắm");
		// item4.setPrice(23);
		// item4.setDescription("Lẩu mắm có hương vị đậm đà của miền sông nước");

		// arr1.add(item1);
		// arr1.add(item2);
		// arr1.add(item3);
		// arr1.add(item4);
		if (arr1 != null) {
			adb1 = new Adapter_List_Dishes(getActivity(),
					R.layout.category_item, arr1);
			lv.setAdapter(adb1);
		}
	}

	// download img from file name
	private void downloadImageCategory(String filename) {
		String urlDownload = getResources().getString(R.string.URLdownloading);
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, TAG_IMG_CATEGORY);
		if (!file.exists()) {
			file.mkdirs();
		}
		new FileDownloadThread(urlDownload + filename, file.getAbsolutePath());
	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllCategories extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			arr = new ArrayList<Category>();
			// pDialog = new
			// ProgressDialog(getActivity().getApplicationContext());
			// pDialog.setMessage("Loading categories. Please wait...");
			// pDialog.setIndeterminate(false);
			// pDialog.setCancelable(false);
			// pDialog.show();
		}

		/**
		 * getting All categories from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// getting JSON string from URL
			try {

				JSONObject json = jParser.makeHttpRequest(url_all_categories,
						"GET", params);

				int index = 1;
				// Check your log cat for JSON reponse
				Log.d("All categories: ", json.toString());
				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// categories found
						// Getting Array of categories
						categories = json.getJSONArray(TAG_CATEGORIES);

						// looping through All categories
						for (int i = 0; i < categories.length(); i++) {
							JSONObject c = categories.getJSONObject(i);

							// Storing each json item in variable
							String category_id = c.getString(TAG_CID);
							String name = c.getString(TAG_NAME);
							String description = c.getString("description");

							String img_category = c.getString("img_category");
							listimgname.add(img_category);
							// ------download image_category----//
							downloadImageCategory(img_category + ".png");
							// creating new HashMap
							HashMap<String, String> map = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							Category cate = new Category();
							cate.setName(name);
							cate.setDescription(description);
							cate.setCtegory_id(Integer.parseInt(category_id));

							System.out.println(cate.getName() + "    "
									+ cate.getDescription() + "    "
									+ cate.getCtegory_id());
							arr.add(cate);
							// adding HashList to ArrayList
							// categoriesList.add(map);
						}
					} else {
						// no products found
						// Launch Add New product Activity
						// ---Intent i = new Intent(getApplicationContext(),
						// --- NewProductActivity.class);
						// --- Closing all previous activities
						// ---i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// ---startActivity(i);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {

			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all categories
			// pDialog.dismiss();
			// updating UI from Background Thread

			/*
			 * Updating parsed JSON data into ListView
			 */
			loadCategoryInfo();
			// ListAdapter adapter = new
			// SimpleAdapter(AllcategoriesActivity.this,
			// categoriesList, R.layout.list_item, new String[] { TAG_CID,
			// TAG_NAME }, new int[] { R.id.pid, R.id.name });
			// // updating listview
			// setListAdapter(adapter);

		}
	}

	// ---------this class use to load items for --------------//
	class LoadAllItems extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		private int category_id;

		public LoadAllItems(int category_id) {
			System.out.println("000000000000000000000000000000000:  "
					+ this.category_id);
			this.category_id = category_id;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			arr1 = new ArrayList<Items>();
			// pDialog = new
			// ProgressDialog(getActivity().getApplicationContext());
			// pDialog.setMessage("Loading categories. Please wait...");
			// pDialog.setIndeterminate(false);
			// pDialog.setCancelable(false);
			// pDialog.show();
		}

		/**
		 * getting All categories from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			try {
				JSONObject json = jParserItem.makeHttpRequest(url_all_items,
						"GET", params);

				int index = 1;
				// Check your log cat for JSON reponse
				Log.d("All items: ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// categories found
						// Getting Array of categories
						items = json.getJSONArray(TAG_ITEMS);

						// looping through All categories
						for (int i = 0; i < items.length(); i++) {
							JSONObject c = items.getJSONObject(i);

							// Storing each json item in variable
							// String item_id = c.getString(TAG_IID);
							String name = c.getString(TAG_NAME);
							String description = c.getString("description");
							// String price = c.getString("price");
							Items item = new Items();
							item.setName(name);
							item.setDescription(description);
							item.setPrice(20);
							// item.setItem_id(Integer.parseInt(item_id));

							System.out.println(item.getName() + "    "
									+ item.getDescription() + "    "
									+ item.getItem_id());
							arr1.add(item);
							// adding HashList to ArrayList
							// categoriesList.add(map);
						}
					} else {
						// no products found
						// Launch Add New product Activity
						// ---Intent i = new Intent(getApplicationContext(),
						// --- NewProductActivity.class);
						// --- Closing all previous activities
						// ---i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// ---startActivity(i);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception ee) {

			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all categories
			// pDialog.dismiss();
			// updating UI from Background Thread

			/*
			 * Updating parsed JSON data into ListView
			 */
			loadItemInfo(category_id);
			// ListAdapter adapter = new
			// SimpleAdapter(AllcategoriesActivity.this,
			// categoriesList, R.layout.list_item, new String[] { TAG_CID,
			// TAG_NAME }, new int[] { R.id.pid, R.id.name });
			// // updating listview
			// setListAdapter(adapter);

		}
	}

	

}