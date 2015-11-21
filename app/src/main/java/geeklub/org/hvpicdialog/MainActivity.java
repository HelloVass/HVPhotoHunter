package geeklub.org.hvpicdialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import geeklub.org.library.HVCameraHunter;
import geeklub.org.library.HVChosePicDialog;
import geeklub.org.library.HVGalleryHunter;
import java.io.File;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  private HVCameraHunter mCameraHunter;

  private HVGalleryHunter mHVGalleryHunter;

  private ImageView mPhotoImageView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mCameraHunter = new HVCameraHunter(this);
    mHVGalleryHunter = new HVGalleryHunter(this);

    mPhotoImageView = (ImageView) findViewById(R.id.iv_photo);

    findViewById(R.id.btn_show_dialog).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        HVChosePicDialog dialog = new HVChosePicDialog();

        dialog.setOnChooseCameraListener(new HVChosePicDialog.OnChooseCameraListener() {
          @Override public void chooseCamera() {
            mCameraHunter.openCamera();
          }
        });

        dialog.setOnChooseGalleryListener(new HVChosePicDialog.OnChooseGalleryListener() {
          @Override public void chooseGallery() {
            mHVGalleryHunter.openGallery();
          }
        });
        dialog.show(getSupportFragmentManager(), HVChosePicDialog.TAG);
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

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
}
