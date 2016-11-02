package geeklub.org.hvhunter.sample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import geeklub.org.hvhunter.HVCameraHunter;
import geeklub.org.hvhunter.HVGalleryHunter;
import java.io.File;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 223;

  private HVCameraHunter mCameraHunter;

  private HVGalleryHunter mHVGalleryHunter;

  private ImageView mPhotoImageView;

  private Button mShowDialogBtn;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    setupContentView();

    setupCallback();
  }

  private void setupContentView() {
    mPhotoImageView = (ImageView) findViewById(R.id.iv_photo);
    mShowDialogBtn = (Button) findViewById(R.id.btn_show_dialog);
  }

  private void setupCallback() {

    // 构建 HVGalleryHunter 的实例
    mHVGalleryHunter = new HVGalleryHunter(this, new HVGalleryHunter.Callback() {

      @Override public void onSucceed(String imagePath) {
        Glide.with(MainActivity.this).load(imagePath).centerCrop().into(mPhotoImageView);
      }

      @Override public void onFailed() {

      }

      @Override public void onCanceled() {

      }
    });

    // 创建 HVCameraHunter 的实例
    mCameraHunter = new HVCameraHunter(this, new HVCameraHunter.Callback() {

      @Override public void onSucceed(File imageFile) {
        Glide.with(MainActivity.this).load(imageFile).centerCrop().into(mPhotoImageView);
      }

      @Override public void onFailed(Exception error) {

      }

      @Override public void onCanceled() {

      }
    });

    mShowDialogBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        showDialog();
      }
    });
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

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

    mHVGalleryHunter.handleActivityResult(requestCode, resultCode, data);

    mCameraHunter.handleActivityResult(requestCode, resultCode);
  }

  private void showDialog() {

    new AlertDialog.Builder(this).setTitle("图片选择器")
        .setItems(new String[] { "图库", "拍照" }, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {

            switch (which) {

              case 0:
                mHVGalleryHunter.openGallery();
                break;

              case 1:
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                  mCameraHunter.openCamera();
                } else {
                  ActivityCompat.requestPermissions(MainActivity.this,
                      new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                      REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
                }
                break;

              default:
                break;
            }
          }
        })
        .setCancelable(true)
        .create()
        .show();
  }
}
