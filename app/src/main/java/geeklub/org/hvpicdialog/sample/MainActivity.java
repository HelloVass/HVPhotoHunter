package geeklub.org.hvpicdialog.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import geeklub.org.hvpicdialog.HVCameraHunter;
import geeklub.org.hvpicdialog.HVChosePicDialog;
import geeklub.org.hvpicdialog.HVGalleryHunter;
import java.io.File;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 223;

  private HVCameraHunter mCameraHunter;

  private HVGalleryHunter mHVGalleryHunter;

  private ImageView mPhotoImageView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "onCreate -->>");

    setContentView(R.layout.activity_main);

    mCameraHunter = new HVCameraHunter(this);
    mHVGalleryHunter = new HVGalleryHunter(this);

    mPhotoImageView = (ImageView) findViewById(R.id.iv_photo);

    findViewById(R.id.btn_show_dialog).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        showHVDialog();
      }
    });
  }

  @Override protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume -->>");
  }

  @Override protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause -->>");
  }

  private void showHVDialog() {
    Log.d(TAG, "showHVDialog -->>");
    HVChosePicDialog dialog = new HVChosePicDialog();

    dialog.setOnChooseCameraListener(new HVChosePicDialog.OnChooseCameraListener() {
      @Override public void chooseCamera() {

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
          mCameraHunter.openCamera();
        } else {
          ActivityCompat.requestPermissions(MainActivity.this,
              new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
              REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
      }
    });

    dialog.setOnChooseGalleryListener(new HVChosePicDialog.OnChooseGalleryListener() {
      @Override public void chooseGallery() {
        mHVGalleryHunter.openGallery();
      }
    });
    dialog.show(getSupportFragmentManager(), HVChosePicDialog.TAG);
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    Log.d(TAG, "onRequestPermissionsResult -->>");
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mCameraHunter.openCamera();
      } else {
        Toast.makeText(MainActivity.this, "permission denied, boo!", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    handleCameraResult(requestCode, resultCode);

    handleGalleryResult(requestCode, resultCode, data);
  }

  private void handleGalleryResult(int requestCode, int resultCode, Intent data) {
    mHVGalleryHunter.handleActivityResult(requestCode, resultCode, data,
        new HVGalleryHunter.Callback() {
          @Override public void onCapturePhotoFailed(Exception error) {

          }

          @Override public void onCaptureSucceed(File imageFile) {
            Log.i(TAG, "HVGalleryHunter onCaptureSucceed -->>" + imageFile.getAbsolutePath());
            Glide.with(MainActivity.this).load(imageFile).centerCrop().into(mPhotoImageView);
          }

          @Override public void onCanceled() {
            Log.i(TAG, "HVGalleryHunter onCanceled -->>");
          }
        });
  }

  private void handleCameraResult(int requestCode, int resultCode) {
    mCameraHunter.handleActivityResult(requestCode, resultCode, new HVCameraHunter.Callback() {

      @Override public void onCapturePhotoFailed(Exception e) {

      }

      @Override public void onCaptureSucceed(File imageFile) {
        Log.i(TAG, "CameraHunter onCaptureSucceed -->>" + imageFile.getAbsolutePath());
        Glide.with(MainActivity.this).load(imageFile).centerCrop().into(mPhotoImageView);
      }

      @Override public void onCanceled(File imageFile) {
        Log.i(TAG, "CameraHunter onCanceled -->>" + imageFile.getAbsolutePath());
      }
    });
  }

  @Override protected void onStop() {
    super.onStop();
    Log.d(TAG, "onStop -->>");
  }
}
