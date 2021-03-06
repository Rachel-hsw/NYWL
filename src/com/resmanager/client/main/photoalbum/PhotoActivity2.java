package com.resmanager.client.main.photoalbum;

import java.util.ArrayList;

import com.resmanager.client.R;
import com.resmanager.client.model.ResultModel;
import com.resmanager.client.model.ScanBimpModel;
import com.resmanager.client.user.order.UploadCache;
import com.resmanager.client.user.order.delivery.DeleteDeliveryPhotoAsyncTask;
import com.resmanager.client.user.order.delivery.DeliveryActivity;
import com.resmanager.client.user.order.delivery.DeleteDeliveryPhotoAsyncTask.DelDeliveryListener;
import com.resmanager.client.user.order.unloading.DeleteRecylePhotoAsyncTask;
import com.resmanager.client.user.order.unloading.DeleteRecylePhotoAsyncTask.DelRecyleListener;
import com.resmanager.client.user.order.unloading.DeleteUploadingPhotoAsyncTask;
import com.resmanager.client.user.order.unloading.DeleteUploadingPhotoAsyncTask.DelUploadingListener;
import com.resmanager.client.user.order.unloading.UploadingActivity;
import com.resmanager.client.utils.ContactsUtils;
import com.resmanager.client.utils.DESUtils;
import com.resmanager.client.utils.Tools;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@SuppressLint("UseSparseArrays")
public class PhotoActivity2 extends Activity {

	private ArrayList<View> listViews = null;
	private ViewPager pager;
	private MyPageAdapter adapter;
	private int count;
	private int photoType;
	private ArrayList<ScanBimpModel> scanBimpModels;
	private String workId;
	RelativeLayout photo_relativeLayout;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		workId = getIntent().getExtras().getString("workId");
		photoType = getIntent().getExtras().getInt("photoType");
		count = getIntent().getExtras().getInt("count");
		scanBimpModels = UploadCache.scanBimpModels;
		photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
		photo_relativeLayout.setBackgroundColor(0x70000000);
		Button photo_bt_exit = (Button) findViewById(R.id.photo_bt_exit);
		photo_bt_exit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		Button photo_bt_del = (Button) findViewById(R.id.photo_bt_del);
		photo_bt_del.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 如果上传成功的二维码中包含这张图片，则通知服务器删除
				switch (photoType) {
				case ContactsUtils.PHOTO_TYPE_DELIVERY:
					// 删除发货时上传的图片
					delDeliveryPhoto(workId, scanBimpModels.get(count));
					break;
				case ContactsUtils.PHOTO_TYPE_UPLOADING:
					// 删除卸货时上传的图片
					delUploadingPhoto(workId, scanBimpModels.get(count));
					break;
				case ContactsUtils.PHOTO_TYPE_RECYLE:
					delRecylePhoto(workId);
					// 删除回收桶上传时的图片
					break;
				default:
					break;
				}

			}
		});
		Button photo_bt_enter = (Button) findViewById(R.id.photo_bt_enter);
		photo_bt_enter.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < scanBimpModels.size(); i++) {
			initListViews(scanBimpModels.get(i).getBmp(), scanBimpModels.get(i).getBmpPath(), scanBimpModels.get(i).getThumbPath());//
		}

		adapter = new MyPageAdapter(listViews);// 构造adapter
		pager.setAdapter(adapter);// 设置适配器
 		pager.setCurrentItem(count);
	}

	@SuppressWarnings("deprecation")
	private void initListViews(Bitmap bm, String path, String thumbPath) {
		if (listViews == null) {
			listViews = new ArrayList<View>();
		}
		ImageView img = new ImageView(this);// 构造textView对象
		img.setBackgroundColor(0xff000000);
		if (thumbPath != null && !thumbPath.equals("")) {
			Picasso.with(this).load(path).error(R.drawable.default_img).error(R.drawable.default_img).into(img);
		} else {
			img.setImageBitmap(bm);
		}
		img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		listViews.add(img);// 添加view
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {// 页面选择响应函数
			count = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

		}

		public void onPageScrollStateChanged(int arg0) {// 滑动状态改变

		}
	};

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content

		private int size;// 页数

		public MyPageAdapter(ArrayList<View> listViews) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {// 自己写的一个方法用来添加数据
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 返回数量
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	/**
	 * 删除发货图片
	 */
	private void delDeliveryPhoto(String workID, final ScanBimpModel scanBimpModel) {
		DeleteDeliveryPhotoAsyncTask deleteDeliveryPhotoAsyncTask = new DeleteDeliveryPhotoAsyncTask(PhotoActivity2.this, workID,
				DESUtils.decrypt(scanBimpModel.getLabelCode()));
		deleteDeliveryPhotoAsyncTask.setDelDeliveryListener(new DelDeliveryListener() {

			@Override
			public void delDeliveryResult(ResultModel resultModel) {
				if (resultModel != null) {
					if (resultModel.getResult().equals("true")) {
						if (DeliveryActivity.selectSkuMap.containsKey(scanBimpModel.getResourceTypeId())) {
							int num = DeliveryActivity.selectSkuMap.get(scanBimpModel.getResourceTypeId());
							num -= 1;
							DeliveryActivity.selectSkuMap.put(scanBimpModel.getResourceTypeId(), num);
						}
						Tools.showToast(PhotoActivity2.this, "删除成功");
						del();
					} else {
						Tools.showToast(PhotoActivity2.this, resultModel.getDescription());
					}
				} else {
					Tools.showToast(PhotoActivity2.this, "图片删除失败，请检查网络");
				}
			}
		});
		deleteDeliveryPhotoAsyncTask.execute();
	}

	/**
	 * 删除卸货图片
	 */
	private void delUploadingPhoto(String workID, final ScanBimpModel scanBimpModel) {
		DeleteUploadingPhotoAsyncTask deleteUploadingPhotoAsyncTask = new DeleteUploadingPhotoAsyncTask(this, workID, DESUtils.decrypt(scanBimpModel
				.getLabelCode()));
		deleteUploadingPhotoAsyncTask.setDelUploadingListener(new DelUploadingListener() {

			@Override
			public void delUploadingResult(ResultModel resultModel) {
				if (resultModel != null) {
					if (resultModel.getResult().equals("true")) {
						if (UploadingActivity.selectSkuMap.containsKey(scanBimpModel.getResourceTypeId())) {
							int num = UploadingActivity.selectSkuMap.get(scanBimpModel.getResourceTypeId());
							num -= 1;
							UploadingActivity.selectSkuMap.put(scanBimpModel.getResourceTypeId(), num);
						}
						Tools.showToast(PhotoActivity2.this, "删除成功");
						del();
					} else {
						Tools.showToast(PhotoActivity2.this, resultModel.getDescription());
					}
				} else {
					Tools.showToast(PhotoActivity2.this, "图片删除失败，请检查网络");
				}
			}
		});
		deleteUploadingPhotoAsyncTask.execute();
	}

	/**
	 * 删除卸货图片
	 */
	private void delRecylePhoto(String recPID) {
		DeleteRecylePhotoAsyncTask deleteRecylePhotoAsyncTask = new DeleteRecylePhotoAsyncTask(this, recPID);
		deleteRecylePhotoAsyncTask.setDelRecyleListener(new DelRecyleListener() {

			@Override
			public void delRecyleResult(ResultModel resultModel) {
				if (resultModel != null) {
					if (resultModel.getResult().equals("true")) {
						Tools.showToast(PhotoActivity2.this, "删除成功");
						del();
					} else {
						Tools.showToast(PhotoActivity2.this, resultModel.getDescription());
					}
				} else {
					Tools.showToast(PhotoActivity2.this, "图片删除失败，请检查网络");
				}

			}
		});
		deleteRecylePhotoAsyncTask.execute();
	}

	private void del() {
		UploadCache.scanBimpModels.remove(count);
		if (UploadCache.scanBimpModels.size() > 0) {
			pager.removeAllViews();
			listViews.remove(count);
			adapter.setListViews(listViews);
			adapter.notifyDataSetChanged();
		} else {
			finish();
		}
	}
}
