package com.idt.test.app.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.idt.test.app.R;
import com.idt.test.app.custom.revealview.CircularRevealView;
import com.idt.test.app.function.DownloadHelper;
import com.idt.test.app.function.ImageHelper;
import com.idt.test.app.function.MyPreference;
import com.nineoldandroids.animation.Animator;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.FileInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fab) FloatingActionButton                                fabLink;
    @BindView(R.id.activity_main_revealView) CircularRevealView             revealView;
    @BindView(R.id.activity_main_getLinkView) View                          linkView;
    @BindView(R.id.activity_main_cardView_link) CardView                    cardViewLink;
    @BindView(R.id.item_card) CardView                                      item_card;
    @BindView(R.id.activity_main_materialEditText_link) MaterialEditText    editTextLink;
    @BindView(R.id.item_image) ImageView                                    itemImage;
    @BindView(R.id.item_fab_share) FloatingActionButton                     itemFab;
    @BindView(R.id.item_text) TextView                                      itemText;
    @BindView(R.id.activity_main_showImageView) View                        showImageView;


    DownloadHelper dHelper;
    Boolean getLinkView=false;
    Boolean firstEnter=true;
    int IMAGE_WIDTH=0;
    int IMAGE_HEIGHT=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dHelper=new DownloadHelper(getActivity());

        fabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRevealMenu(view);
            }
        });

        // to calculate width and height of image and alter use it for making images smaller
        itemImage.post(new Runnable() {
            @Override
            public void run() {
                IMAGE_WIDTH=itemImage.getWidth();
                IMAGE_HEIGHT=itemImage.getHeight();
                checkToShowLastUrl();
            }
        });

    }

    /**
     * To get Context from everywhere
     * @return Context
     */
    public Context getActivity(){
        return this;
    }


    /**
     * called when clicked on copy button
     * it get the data in clipboard and fill the editText
     * @param v
     */
    @OnClick(R.id.activity_main_button_copy)
    public void copyFromClipboard(View v){
        String txt=getLastDataInClipboard();
        if(!TextUtils.isEmpty(txt))
            editTextLink.setText(txt);
    }

    /**
     * called wehen clicked on Download Button
     * @param v
     */
    @OnClick(R.id.activity_main_button_download)
    public void downloadLink(View v){
        String url=getDownloadLink();
        downloadFile(url);
    }

    /**
     * Read Link From Edit Text
     * @return String url entered by user
     */
    private String getDownloadLink(){
        return editTextLink.getText().toString();
    }

    /**
     * Download any url given to it
     * @param url url of an image
     */
    private void downloadFile(final String url){
        reverseToMain();
        if(dHelper.isDownloaded(url)){
            showImageInformation(url);
        } else {
            dHelper.setListener(new DownloadHelper.downloadListener() {
                @Override
                public void complete() {
                    showImageInformation(url);
                }

                @Override
                public void errorInDownload(String txt) {
                    UIToaster(txt);
                }
            });
            dHelper.downloadFile(url);
            UIToaster("Your File Is Downloading");
        }
    }

    /**
     * Check whether we have downloaded a picture before or we should show the reveal animation
     */
    public void checkToShowLastUrl(){
        String url= MyPreference.getInstance(getActivity()).getLastUrl();
        if(TextUtils.isEmpty(url)){
            showImageView.setVisibility(View.INVISIBLE);
            showRevealMenu(fabLink);
        } else {
            if(firstEnter) {
                showImageView.setVisibility(View.VISIBLE);
                showImageInformation(url);
            }
        }
        firstEnter=false;
    }

    /**
     * show image and set title for downloaded image
     * @param url
     */
    public void showImageInformation(final String url){
        final File imgFile = new File(dHelper.getDownloadedPath(url));
        if(imgFile.exists()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ImageHelper iHelper=new ImageHelper(getActivity());
                        Bitmap myBitmap=iHelper.decodeBitmapFromFile(imgFile.getAbsolutePath(),IMAGE_WIDTH,IMAGE_HEIGHT);
                        itemImage.setImageBitmap(myBitmap);
                        showImageView.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FlipInX).playOn(showImageView);
                        itemText.setText(url);
                        MyPreference.getInstance(getActivity()).setLastUrl(url);
                    } catch (Exception e){

                    }
                }
            });
        }
    }

    /**
     * Get Last String copied in clipboard
     * @return String copiedText
     */
    private String getLastDataInClipboard(){
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData == null || clipData.getItemCount() == 0) {
                UIToaster("You Clipboard Is Empty");
                return "";
            }
            String txt = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
            return txt;
        } catch (Exception e){
            UIToaster("Problem In Getting Data From Clipboard");
            return "";
        }
    }

    /**
     * Always toast on main ui
     * @param text
     */
    private void UIToaster(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),text,Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * show reveal animation
     * @param v
     */
    public void showRevealMenu(View v){

        YoYo.with(Techniques.FadeOut).duration(200).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showImageView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(item_card);

        getLinkView=true;
        YoYo.with(Techniques.SlideOutDown).playOn(fabLink);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                fabLink.getLocationInWindow(location);
                final Point p = new Point(location[0], location[1]);
                Display mdisp = getWindowManager().getDefaultDisplay();
                Point mdispSize = new Point();
                mdisp.getSize(mdispSize);
                revealView.reveal(p.x + 26, p.y + 26, ContextCompat.getColor(getActivity(), R.color.colorPrimary), 56, 600, null);
            }
        }, 300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linkView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp).playOn(cardViewLink);
            }
        }, 800);
    }

    /**
     * reverse back the reveal animation
     */
    public void reverseToMain(){
        getLinkView=false;
        YoYo.with(Techniques.SlideInUp).playOn(fabLink);
        YoYo.with(Techniques.FadeOut).duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                linkView.setVisibility(View.INVISIBLE);
                int[] location=new int[2];
                fabLink.getLocationInWindow(location);
                final Point p=new Point(location[0],location[1]);
                Display mdisp = getWindowManager().getDefaultDisplay();
                Point mdispSize = new Point();
                mdisp.getSize(mdispSize);
                revealView.hide(mdispSize.x,mdispSize.y, Color.parseColor("#00000000"), 0, 600, null);

                showImageView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn).playOn(item_card);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(cardViewLink);
    }


    @Override
    public void onBackPressed() {
        if(getLinkView){
            reverseToMain();
            return;
        }
        super.onBackPressed();
    }
}
