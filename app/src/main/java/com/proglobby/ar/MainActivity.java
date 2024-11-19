package com.proglobby.ar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {
    ExternalTexture texture;
    MediaPlayer mediaPlayer;
    TestFragment fragment;
    Scene scene;

    boolean isImageDetected = false;

    TextView isVisible;
    ModelRenderable renderable;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        texture = new ExternalTexture();
        isVisible = this.findViewById(R.id.isVisible);
        mediaPlayer = MediaPlayer.create(this, R.raw.video);
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);

        ModelRenderable.builder().setSource(this, Uri.parse("video_screen.sfb")).build().thenAccept(modelRenderable -> {
            modelRenderable.getMaterial().setExternalTexture("videoTexture", texture);
            modelRenderable.getMaterial().setFloat4("keyColor",
                    new Color(0.01843f, 1f, 0.098f));
            renderable = modelRenderable;
        });

        fragment = (TestFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        scene = fragment.getArSceneView().getScene();

        scene.addOnUpdateListener(this::onUpdate);

    }

    private void onUpdate(FrameTime frameTime) {


        Frame frame = fragment.getArSceneView().getArFrame();

        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage image: augmentedImages){
            if (image.getTrackingState() == TrackingState.TRACKING){
                if (isImageDetected){
                    return;
                }
                if (image.getName().equals("image")){
                    isVisible.setText("Visible");
                    isImageDetected = true;
                    playVideo(image.createAnchor(image.getCenterPose()), image.getExtentX(), image.getExtentZ());
                    break;
                }
            }else{
                isImageDetected = false;
                isVisible.setText("Not Visible");
            }
        }
    }

    private void playVideo(Anchor anchor, float extentX, float extentZ) {

        mediaPlayer.start();
        AnchorNode anchorNode = new AnchorNode(anchor);
        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });
        anchorNode.setWorldScale(new Vector3(extentX, 1f, extentZ));
        scene.addChild(anchorNode);
    }
}