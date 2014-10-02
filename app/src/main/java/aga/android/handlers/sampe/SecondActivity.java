package aga.android.handlers.sampe;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * @author artemgapchenko
 * Created on 10/2/14.
 */
public class SecondActivity extends Activity implements Handler.Callback {
    private HandlerThread mHandlerThread = new HandlerThread("BackgroundThread");
    private Handler mUIHandler;
    private Handler mBackgroundHandler;

    private TextView mLocationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mLocationView = (TextView) findViewById(R.id.location_name);

        mUIHandler = new Handler(getMainLooper(), this);

        mHandlerThread.start();
        mBackgroundHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    final Geocoder geocoder = new Geocoder(SecondActivity.this);

                    try {
                        final List<Address> results = geocoder.getFromLocation(53.539316, 49.396494, 1);

                        if (results != null && !results.isEmpty()) {
                            mUIHandler.dispatchMessage(Message.obtain(mUIHandler, 1, results.get(0)));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBackgroundHandler.dispatchMessage(Message.obtain(mBackgroundHandler, 0));
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            mLocationView.setText("I live in " + ((Address) msg.obj).getLocality());
            return true;
        }

        return false;
    }
}
