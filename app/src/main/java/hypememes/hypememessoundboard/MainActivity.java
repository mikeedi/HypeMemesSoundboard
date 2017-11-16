package hypememes.hypememessoundboard;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SoundPool mSoundPool;
    private AssetManager mAssetManager;
    private  int[] sound;
    private int mStreamID;

    public int COUNT_MEME = 41;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final int width = this.getResources().getDisplayMetrics().widthPixels;
        final int nColumns = width / 230;

        FloatingActionButton stop = (FloatingActionButton) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSoundPool.stop(mStreamID);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Layouts settings
        GridLayout layout = (GridLayout)findViewById(R.id.layout);
        layout.setColumnCount(nColumns);


        // create id of image resources
        int[] idImage = new int[COUNT_MEME+1];
        for (int i = 1; i <= COUNT_MEME; i++)
            idImage[i] = (getImageId(this, "i" + Integer.toString(i)));

        ImageButton[] butImage = new ImageButton[COUNT_MEME+1];
        for (int i = 1; i <= COUNT_MEME; i++){
            butImage[i] = new ImageButton(MainActivity.this);
            butImage[i].setLayoutParams(new RelativeLayout.LayoutParams(width/nColumns,
                    width/nColumns));
            butImage[i].setBackgroundColor(0);
            butImage[i].setImageResource(idImage[i]);
            butImage[i].setId(i);
            butImage[i].setOnClickListener(onClickListener);
            layout.addView(butImage[i]);
        }

    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playSound(v.getId());

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool() {

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(1)
                .build();
    }


    private int playSound(int sound) {
        if (sound > 0) {
            mStreamID = mSoundPool.play(sound, 1, 1, 1, 0, 1);
        }
        return mStreamID;
    }

    private int loadSound(String fileName) {
        AssetFileDescriptor afd;
        try {
            afd = mAssetManager.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Не могу загрузить файл " + fileName,
                    Toast.LENGTH_SHORT).show();
            return -1;
        }
        return mSoundPool.load(afd, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();


        createNewSoundPool();

        mAssetManager = getAssets();

        sound = new int[COUNT_MEME+1];

        for (int i = 1; i <= COUNT_MEME; i++){
            loadSound("m" + Integer.toString(i) + ".mp3");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundPool.release();
        mSoundPool = null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_send) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://t.me/siredukov"));
            startActivity(browserIntent);

        } else if(id == R.id.nav_donat){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://money.yandex.ru/to/410014388254073"));
            startActivity(browserIntent);
        } else if(id == R.id.nav_rate){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=hypememes.hypememessoundboard"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}
