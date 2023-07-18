package com.example.merobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BlurDialogFragment extends DialogFragment{

        private static final float BLUR_RADIUS = 10f;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Set the style of the dialog to be a fullscreen dialog with no title
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for the fragment
            View view = inflater.inflate(R.layout.dialog_comment_add, container, false);

            // Set the background of the dialog to be a blurred version of the current screen
            Bitmap screenshot = takeScreenshot();
            Bitmap blurredBitmap = blurBitmap(screenshot, BLUR_RADIUS);
            Drawable blurDrawable = new BitmapDrawable(getResources(), blurredBitmap);
            view.setBackground(blurDrawable);

            return view;
        }

        private Bitmap takeScreenshot() {
            // Get the current screen contents as a bitmap
            View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            Bitmap screenshot = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(screenshot);
            rootView.draw(canvas);
            return screenshot;
        }

        private Bitmap blurBitmap(Bitmap bitmap, float radius) {
            // Create a blurred version of the bitmap using RenderScript
            RenderScript rs = RenderScript.create(getActivity());
            Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            rs.destroy();
            return bitmap;
        }

}