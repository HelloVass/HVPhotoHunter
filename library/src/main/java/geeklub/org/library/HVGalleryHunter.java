package geeklub.org.library;

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

  private Context mContext;

  public static final int REQUEST_CAPTURE_PHOTO_FROM_GALLERY = 33;

  private static final String EXTRAS_GALLERY_SELECTED_PHOTO_URI =
      "extras_gallery_selected_photo_uri";

  private Callback mCallback;

  public interface Callback {

    void onCapturePhotoFailed(Exception error);

    void onCaptureSucceed(File path);

    void onCanceled();
  }

  public HVGalleryHunter(Context context) {
    this.mContext = context;
  }

  public void openGallery() {
    Intent intent = createGalleryIntent();
    ((Activity) mContext).startActivityForResult(intent, REQUEST_CAPTURE_PHOTO_FROM_GALLERY);
  }

  public void handleActivityResult(int requestCode, int resultCode, Intent data,
      Callback callback) {
    switch (requestCode) {
      case REQUEST_CAPTURE_PHOTO_FROM_GALLERY:
        mCallback = callback;
        if (resultCode == Activity.RESULT_OK && data != null) {
          parsePhotoUri(data.getData());
        } else if (resultCode == Activity.RESULT_CANCELED) {
          mCallback.onCanceled();
        }
        break;

      default:
        break;
    }
  }

  private void parsePhotoUri(Uri uri) {
    Log.i(TAG, "parsePhotoUri -->>");
    Bundle args = new Bundle();
    args.putParcelable(EXTRAS_GALLERY_SELECTED_PHOTO_URI, uri);
    ((Activity) mContext).getLoaderManager().initLoader(GALLERY_HUNTER_LOADER_ID, args, this);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Log.i(TAG, "onCreateLoader -->>");
    String[] projections = { MediaStore.MediaColumns.DATA };
    Uri imageUri = args.getParcelable(EXTRAS_GALLERY_SELECTED_PHOTO_URI);
    return new CursorLoader(mContext, imageUri, projections, null, null, null);
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    Log.i(TAG, "onLoadFinished -->>");
    if (cursor != null) {
      int imagePathColumnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
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

  private Intent createGalleryIntent() {
    Intent intent =
        new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    intent.setType("image/*");
    return intent;
  }
}
