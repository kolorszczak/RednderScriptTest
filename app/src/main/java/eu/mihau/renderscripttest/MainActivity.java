package eu.mihau.renderscripttest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.widget.ImageView;


/**
 * Created by mihau on 24.11.2016.
 */

public class MainActivity extends Activity {
    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;

    private RenderScript mRS;
    private Allocation mInAllocation;
    private Allocation mOutAllocation;
    private ScriptC_mono mScript;
    private ImageView in;
    private ImageView out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        in = (ImageView) findViewById(R.id.displayin);
        out = (ImageView) findViewById(R.id.displayout);

        mBitmapIn = loadBitmap(R.drawable.data);
        in.setImageBitmap(mBitmapIn);

        mRS = RenderScript.create(this);
        Background b = new Background();
        b.execute();
    }


    private Bitmap loadBitmap(int resource) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(getResources(), resource, options);
    }

    class Background extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            mBitmapOut = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(),
                    mBitmapIn.getConfig());

            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            mScript = new ScriptC_mono(mRS);

            mScript.forEach_root(mInAllocation, mOutAllocation);
            mOutAllocation.copyTo(mBitmapOut);
            return mBitmapOut;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            out.setImageBitmap(bitmap);
        }
    }

}
