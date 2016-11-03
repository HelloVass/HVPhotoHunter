# HVPhotoHunter

一个**选取图片** Material Design 风格的 Dialog，参考了 [EasyImage](https://github.com/jkwiecien/EasyImage) 这个项目。

## 功能

- 从相册中选取图片
- 拍照获取图片

## 演示

### 从相册中获取图片

<img src="/screenshot/相册中获取图片.gif" />

### 拍照获取图片

<img src="/screenshot/拍照获取图片.gif" />



## 使用

### 从相册获取图片

#### Setp1

构建 HVGalleryHunter 的实例

``` java

    mCameraHunter = new HVCameraHunter(this, new HVCameraHunter.Callback() {

      @Override public void onSucceed(File imageFile) {
        Glide.with(MainActivity.this).load(imageFile).centerCrop().into(mPhotoImageView);
      }

      @Override public void onFailed(Exception error) {

      }

      @Override public void onCanceled() {

      }
    });

```

#### Step2

重写 `onActivityResult` 方法

``` java

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    mHVGalleryHunter.handleActivityResult(requestCode, resultCode, data);

  }

```

#### Step3

在适当的位置，调用 `HVGalleryHunter` 的 `openGallery()` 方法，具体参见 Demo 中的栗子。

### 使用相机获取图片

#### Step1

创建 HVCameraHunter 的实例

``` java

    mCameraHunter = new HVCameraHunter(this, new HVCameraHunter.Callback() {

      @Override public void onSucceed(File imageFile) {
        Glide.with(MainActivity.this).load(imageFile).centerCrop().into(mPhotoImageView);
      }

      @Override public void onFailed(Exception error) {

      }

      @Override public void onCanceled() {

      }
    });

```




#### Step2

重写 `onActivityResult` 方法

``` java

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    mHVGalleryHunter.handleActivityResult(requestCode, resultCode, data);

    mCameraHunter.handleActivityResult(requestCode, resultCode);
  }

```

#### Step3

在适当的位置，调用 `HVCameraHunter` 的 `openCamera()` 方法，具体参见 Demo 中的栗子。






