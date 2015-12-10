package geeklub.org.hvpicdialog;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;

/**
 * Created by HelloVass on 15/11/15.
 */
public class HVGalleryHunter implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final String TAG = HVGalleryHunter.class.getSimpleName();

  private static final int GALLERY_HUNTER_LOADER_ID = 2333;

  public static final int REQUEST_CAPTURE_PHOTO_FROM_GALLERY = 33;

  private static final String EXTRAS_GALLERY_SELECTED_PHOTO_URI =
      "extras_gallery_selected_photo_uri";

  private Context mContext;

  private Callback mCallback;

  public interface Callback {

    void onCapturePhotoFailed(Exception error);

    void onCaptureSucceed(File path);

    void onCanceled();
  }

  public HVGalleryHunter(Context context) {
    this.mContext = context;
  }

  /**
   * 启动系统中的 Gallery
   */
  public void openGallery() {
    Intent intent = createGalleryIntent();
    ((Activity) mContext).startActivityForResult(intent, REQUEST_CAPTURE_PHOTO_FROM_GALLERY);
  }

  /**
   * 在 onActivityResult 中调用
   *
   * @param requestCode 请求码
   * @param resultCode 结果码
   * @param callback 回调接口
   */
  public void handleActivityResult(int requestCode, int resultCode, Intent data,
      Callback callback) {
    switch (requestCode) {
      case REQUEST_CAPTURE_PHOTO_FROM_GALLERY:
        mCallback = callback;
        if (resultCode == Activity.RESULT_OK && data != null) {
          Log.i(TAG, "data -->>" + data.toString());
          Log.i(TAG, "data.getData() -->>" + data.getData().toString());
          parsePhotoUri(data.getData());
        } else if (resultCode == Activity.RESULT_CANCELED) {
          mCallback.onCanceled();
        }
        break;

      default:
        break;
    }
  }

  /**
   * 解析 Uri
   */
  private void parsePhotoUri(Uri uri) {
    Log.i(TAG, "parsePhotoUri -->>");
    Bundle args = new Bundle();
    args.putParcelable(EXTRAS_GALLERY_SELECTED_PHOTO_URI, uri);
    ((Activity) mContext).getLoaderManager().initLoader(GALLERY_HUNTER_LOADER_ID, args, this);
  }

  /**
   * 创建 loader 查询图片的地址
   *
   * @param id loader 的编号
   * @param args 参数
   */
  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Log.i(TAG, "onCreateLoader -->>");
    String[] projections = { MediaStore.Images.Media.DATA };
    Uri imageUri = args.getParcelable(EXTRAS_GALLERY_SELECTED_PHOTO_URI);
    return new CursorLoader(mContext, imageUri, projections, null, null, null);
  }

  /**
   * 查询结束后，得到图片的真实路径
   */
  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    Log.i(TAG, "onLoadFinished -->>");
    if (cursor != null) {
      int imagePathColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      File filePath = new File(cursor.getString(imagePathColumnIndex));
      mCallback.onCaptureSucceed(filePath);
      // 销毁当前这个 loader
      ((Activity) mContext).getLoaderManager().destroyLoader(GALLERY_HUNTER_LOADER_ID);
    } else {
      mCallback.onCapturePhotoFailed(new NoSuchFieldException());
    }
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {
    Log.i(TAG, "onLoaderReset -->>");
  }

  /**
   * 创建打开相册的 Intent
   */
  private Intent createGalleryIntent() {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    return intent;
  }
}
