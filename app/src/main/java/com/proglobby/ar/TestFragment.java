package com.proglobby.ar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class TestFragment extends ArFragment {
    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);
        AugmentedImageDatabase db = new AugmentedImageDatabase(session);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        db.addImage("image", bitmap);

        config.setAugmentedImageDatabase(db);

        this.getArSceneView().setupSession(session);

        return config;

    }

    @Override
    public View onCreateView(LayoutInflater inflater,@androidx.annotation.Nullable ViewGroup container,@androidx.annotation.Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);
        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);
        return frameLayout;
    }
}
