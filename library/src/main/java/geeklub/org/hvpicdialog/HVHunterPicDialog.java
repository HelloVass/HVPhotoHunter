package geeklub.org.hvpicdialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import geeklub.org.library.R;

/**
 * Created by HelloVass on 15/9/25.
 */

public class HVHunterPicDialog extends DialogFragment {

  public final static String TAG = HVHunterPicDialog.class.getSimpleName();

  private ImageButton mCameraBtn;

  private ImageButton mGalleryBtn;

  private Button mCancelBtn;

  private OnChooseCameraListener mOnChooseCameraListener;

  private OnChooseGalleryListener mOnChooseGalleryListener;

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View view = inflater.inflate(R.layout.layout_chose_pic, null);
    setUpView(view);
    builder.setView(view);
    return builder.create();
  }

  private void setUpView(View view) {
    mCameraBtn = (ImageButton) view.findViewById(R.id.ib_camera);
    mGalleryBtn = (ImageButton) view.findViewById(R.id.ib_gallery);
    mCancelBtn = (Button) view.findViewById(R.id.btn_cancel);

    mGalleryBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mOnChooseGalleryListener != null) {
          dismiss();
          mOnChooseGalleryListener.chooseGallery();
        }
      }
    });

    mCameraBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mOnChooseCameraListener != null) {
          dismiss();
          mOnChooseCameraListener.chooseCamera();
        }
      }
    });

    mCancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dismiss();
      }
    });
  }

  public interface OnChooseGalleryListener {
    void chooseGallery();
  }

  public interface OnChooseCameraListener {
    void chooseCamera();
  }

  public void setOnChooseGalleryListener(OnChooseGalleryListener onChooseGalleryListener) {
    mOnChooseGalleryListener = onChooseGalleryListener;
  }

  public void setOnChooseCameraListener(OnChooseCameraListener onChooseCameraListener) {
    mOnChooseCameraListener = onChooseCameraListener;
  }
}
