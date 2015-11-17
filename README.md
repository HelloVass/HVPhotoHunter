# HVPhotoHunter

一个**选取图片** Material Design 风格的 Dialog，参考了 [EasyImage](https://github.com/jkwiecien/EasyImage) 这个项目。

## 功能
- 从相册中选取图片
- 拍照获取图片

## 截图
<img src="/screenshot/device-2015-11-16-184918.png" width="320px"/>

## 如何使用

### 打开相册


```java
// 构建 HVGalleryHunter 的实例
mHVGalleryHunter = new HVGalleryHunter(this);

dialog.setOnChooseGalleryListener(new HVChosePicDialog.OnChooseGalleryListener() {
          @Override public void chooseGallery() {
            mHVGalleryHunter.openGallery();
          }
        });
```




### 拍照

```java
// 构建 HVCameraHunter 的实例
mCameraHunter = new HVCameraHunter(this);

dialog.setOnChooseCameraListener(new HVChosePicDialog.OnChooseCameraListener() {
          @Override public void chooseCamera() {
            mCameraHunter.openCamera();
          }
        });
```


### 获得图片 File

```java
// 在 onActivityResult 方法中得到 File
@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // 捕获拍照的结果
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
    // 捕获相册返回的结果
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


```





