package geeklub.org.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HelloVass on 15/11/15.
 */
public class HVCameraHunter {

  private Context mContext;

  public static final int REQUEST_TAKE_PHOTO = 34;

  public static final String TEMP_PHOTO_FILE_URI = "temp_photo_file_uri";

  public interface Callback {

    void onCapturePhotoFailed(Exception error);

    void onCaptureSucceed(File imageFile);

    void onCanceled(File imageFile);
  }

  public HVCameraHunter(Context context) {
    this.mContext = context;
  }

  public void openCamera() {
    Intent intent = createCameraIntent();
    ((Activity) mContext).startActivityForResult(intent, REQUEST_TAKE_PHOTO);
  }

  public void handleActivityResult(int requestCode, int resultCode, Callback callback) {

    switch (requestCode) {
      case REQUEST_TAKE_PHOTO:
        if (resultCode == Activity.RESULT_OK) {
          onCapturePhotoFromCamera(callback);
        } else if (resultCode == Activity.RESULT_CANCELED) {
          onCancelCapturePhoto(callback);
        }
        break;
      default:
        break;
    }
  }

  private void onCancelCapturePhoto(Callback callback) {
    try {
      File photoFile = getPhotoFile();
      callback.onCanceled(photoFile);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  private void onCapturePhotoFromCamera(Callback callback) {
    try {
      File photoFile = getPhotoFile();
      callback.onCaptureSucceed(photoFile);
    } catch (URISyntaxException e) {
      callback.onCapturePhotoFailed(e);
    }
  }

  private Intent createCameraIntent() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    try {
      Uri capturedPhotoUri = createCameraPictureFileUri();
      intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedPhotoUri);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return intent;
  }

  private Uri createCameraPictureFileUri() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date());
    // 图片前缀
    String tempFilePrefix = "JPEG_" + timeStamp + "_";
    // 图片目录
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File photoFile = File.createTempFile(tempFilePrefix, ".jpg", storageDir);
    Uri photoUri = Uri.fromFile(photoFile);
    savePhotoFileUri(photoUri);
    return photoUri;
  }

  private void savePhotoFileUri(Uri fileUri) {
    SharedPreferences.Editor editor =
        PreferenceManager.getDefaultSharedPreferences(mContext).edit();
    editor.putString(TEMP_PHOTO_FILE_URI, fileUri.toString()).commit();
  }

  private File getPhotoFile() throws URISyntaxException {
    URI photoUri = new URI(
        PreferenceManager.getDefaultSharedPreferences(mContext).getString(TEMP_PHOTO_FILE_URI, ""));
    addPhotoToGallery(photoUri);
    return new File(photoUri);
  }

  private void addPhotoToGallery(URI photoUri) {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    File file = new File(photoUri);
    Uri contentUri = Uri.fromFile(file);
    mediaScanIntent.setData(contentUri);
    mContext.sendBroadcast(mediaScanIntent);
  }
}
