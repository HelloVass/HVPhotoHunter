package geeklub.org.hvhunter;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.util.List;

/**
 * Created by HelloVass on 15/11/15.
 */
public class HVGalleryHunter implements LoaderManager.LoaderCallbacks<String> {

  private static final String TAG = HVGalleryHunter.class.getSimpleName();

  private static final int GALLERY_HUNTER_LOADER_ID = 23;

  private static final int REQUEST_CAPTURE_PHOTO_FROM_GALLERY = 33;

  private static final String[] PROJECTIONS = new String[] { MediaStore.Images.Media.DATA };

  private Context mContext;

  private Callback mCallback;

  private Uri mUri;

  public HVGalleryHunter(Context context, Callback callback) {
    this.mContext = context;
    this.mCallback = callback;
  }

  /**
   * 启动系统中的 Gallery
   */
  public void openGallery() {
    Intent intent = createGalleryIntent();
    ((Activity) mContext).startActivityForResult(intent, REQUEST_CAPTURE_PHOTO_FROM_GALLERY);
  }

  /**
   * 在 Activity 中调用
   */
  public void handleActivityResult(int requestCode, int resultCode, Intent data) {

    switch (requestCode) {

      case REQUEST_CAPTURE_PHOTO_FROM_GALLERY:

        if (resultCode == Activity.RESULT_OK && data != null) {
          parseArgs(data);
          resetLoader();
        } else if (resultCode == Activity.RESULT_CANCELED && mCallback != null) {
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
  private void parseArgs(Intent data) {
    mUri = data.getData();
  }

  /**
   * 重置 Loader
   */
  private void resetLoader() {
    ((Activity) mContext).getLoaderManager().restartLoader(GALLERY_HUNTER_LOADER_ID, null, this);
  }

  /**
   * 创建 loader 查询图片的地址
   *
   * @param id loader 的编号
   * @param args 参数
   */
  @Override public Loader<String> onCreateLoader(int id, Bundle args) {
    return new GalleryLoader(mContext, mUri, PROJECTIONS);
  }

  @Override public void onLoadFinished(Loader<String> loader, String imagePath) {

    if (imagePath != null && mCallback != null) {
      mCallback.onSucceed(imagePath);
    } else if (mCallback != null) {
      mCallback.onFailed();
    }
  }

  @Override public void onLoaderReset(Loader<String> loader) {
    Log.d(TAG, "onLoaderReset ===>>");
  }

  public interface Callback {

    void onSucceed(String imagePath);

    void onCanceled();

    void onFailed();
  }

  /**
   * 创建打开相册的 Intent
   */
  private Intent createGalleryIntent() {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    return intent;
  }

  private static class GalleryLoader extends AsyncTaskLoader<String> {

    private String mImagePath;

    private Uri mUri;

    private String[] mProjections;

    GalleryLoader(Context context, Uri uri, String[] projections) {
      super(context);

      mUri = uri;
      mProjections = projections;
    }

    @Override public String loadInBackground() {

      Cursor cursor = getContext().getContentResolver().query(mUri, mProjections, null, null, null);

      String imagePath = null;

      if (cursor != null) {

        try {
          cursor.moveToFirst();
          imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          cursor.close();
        }
      }

      return imagePath;
    }

    @Override public void deliverResult(String imagePath) {

      if (isReset()) {
        if (imagePath != null) {
          onReleaseResources(imagePath);
        }
        return;
      }

      String oldImagePath = mImagePath;
      mImagePath = imagePath;

      if (isStarted()) {
        super.deliverResult(imagePath);
      }

      if (oldImagePath != null) {
        onReleaseResources(oldImagePath);
      }
    }

    @Override protected void onStartLoading() {

      if (mImagePath != null) {
        deliverResult(mImagePath);
      }

      if (takeContentChanged() || mImagePath == null) {
        forceLoad();
      }
    }

    @Override protected void onStopLoading() {
      cancelLoad();
    }

    @Override public void onCanceled(String imagePath) {
      super.onCanceled(imagePath);

      onReleaseResources(imagePath);
    }

    @Override protected void onReset() {
      super.onReset();

      onStopLoading();

      if (mImagePath != null) {
        onReleaseResources(mImagePath);
        mImagePath = null;
      }
    }

    private void onReleaseResources(String imagePath) {
      //  do nothing
    }
  }
}
