package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.InvoiceItems;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.TollTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Error;
import util.HashHelper;

import static android.util.Base64.NO_WRAP;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("mps.bachelor2017.bfh.ti.ch.mobiltypricing", appContext.getPackageName());
    }

    @Test
    public void cast() {
        String i = "{\"items\":{\"Go53zn3tGJlGidk2kmSchaMW+tVSL1OsnV9T9OsU1z4\\u003d\":1,\"xEq8YMlaZT5Bs/k1GxYQZPSAFXjJUp4pO2AmjcRjB/Q\\u003d\":1,\"PN9WXg8iVVxL4Xm3UCd+v1c8dnEUgRZVykCxLoDj6KI\\u003d\":1,\"UEzvoa12ti1cXPdkZzUjC3iFKEnAYcCxnglgbmDs6AY\\u003d\":1,\"22kf/MQzDcSyYmjAycIi8V5uD9Q5qIqUVZvI45XF2tM\\u003d\":1,\"bvYnwHccyV9tamKWkmCx8yWdkQGOdwAlg9cKc4ve92M\\u003d\":1,\"sUKUfv0b7USPuXsCncKdVy87aLGkKaYj8opVVYxowGo\\u003d\":1,\"ROkLBo/grHMF38QgEiLehoyV++h4p8sdnhiABoeg7Qc\\u003d\":1,\"ovnBMYFxfVIKUxXY++ZsKuetoAQeCKZZK9jDKrk1NBs\\u003d\":1,\"32khqzPUEoP2EjICQcAdbJCdQfYQtqZw7JalJaDj28U\\u003d\":1,\"BXzy2Kfdc0hb3xR+JSKDcbfqFgUKLkmrAncFCFGBISI\\u003d\":1,\"WNZRxr1nEKbW9ZnaPPoWaQFkj2KitSa3R1lqzNT8ZdQ\\u003d\":1,\"qz+pzkLvIS1QDfw6Wr7OmlPwmc49d6aGnD+SphbMaaI\\u003d\":1,\"TxtLfo6545n2nPrMsk5byIciTFLCMtcj62vXUHoRopI\\u003d\":1,\"lzOfCCNsGx/E+5pxD4H7eshCmLTbqXPS07MMWxjy2F8\\u003d\":1,\"4JDK0SR2PmwigtoXdQgJx67RLDgXwoGrP9aQjYkqAoU\\u003d\":1,\"3eliVd7s2nNG0F0w465iVbkijX7UBPSH+qoung29+j8\\u003d\":1,\"LZp0ClsudZgoBN+1SXKOdAGkTjrqvpzgoAGDNh965hI\\u003d\":1,\"2dx6BPtDgW5qUZSqUuFW539qUuK6dpRjYMFsZuqQooY\\u003d\":1,\"qlONeXocIC557BQi//9DCsw3jrDwxpTNOtFviWev2Lo\\u003d\":1,\"iVp12rnMz0x29teyB2doqXoOPDgaqThdWvITY+zIwVs\\u003d\":1,\"8yFjMknKEkARV08V/vHofmlqWsZ1ojhq4m0VUMN59ns\\u003d\":1,\"BEcpKaxug4admY7olzqIgZEHAZB+A8HWEASXcQ+75nU\\u003d\":1,\"IUZ4H+52gir1Z380N9yPMRn3ooq1vcSeiXZhE/r2wVQ\\u003d\":1,\"gs/hFcv5jX7ILuGlZ9lzi2YdbR/7UuyO5GJGuHbwGWA\\u003d\":1,\"bOwf9kwgOunJ7UWGA2DZr0Ntc9GOw6lkiKQMfioZ1d4\\u003d\":1,\"cFekIrG/IC1mohIGVyC9tVFiB/xQHdLKhII+qEcG6kI\\u003d\":1,\"uvycBJP4Oo6TeUW5biEJyM9OzCI2Mgyn/zFK1fcWSv0\\u003d\":1,\"xhkmQS1MwCLiThmzsHtvU6Qwa5ChD0m+XXoAuew1dlI\\u003d\":1,\"fJVOPmUEuXz8nlQCnl62O8nsCr6m14ODZSV/xGNa/rc\\u003d\":1},\"signature\":\"MDwCHGiiX3LHM9Lc6ZB9sLAyS6FKKZibk9jmOQHyG0kCHCSa4ckDvy3c+vx/Cg7ksxx+VCB2z5il0tLd9KU\\u003d\",\"sessionId\":\"f91240d8-d7f0-4963-ae2f-3516eddbe96c\"}";

        InvoiceItems z = new Gson().fromJson(i, InvoiceItems.class);

        assertNotNull(z);
    }

    @Test
    public void TollTest() {
        SharedPreferences settings = InstrumentationRegistry.getTargetContext().getSharedPreferences(Const.PreferenceKey, 0);

        TollTask task = new TollTask(new TollTask.TollTaskListener() {
            @Override
            public void onTollError(Error error) {
                assertTrue(false);
            }

            @Override
            public void onTollSuccessfull() {
                assertTrue(true);
            }
        }, InstrumentationRegistry.getTargetContext(), new MobileGroup(settings),new MobileSecretKey(settings));

        task.execute();

        while(!task.isCancelled()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void SerializeTest() {
        InvoiceItems invoiceItems = new InvoiceItems();

        Map<String, Integer> items = new HashMap<String, Integer>();

        items.put("hash", 2);
        items.put("hash2", 2);
        items.put("hash3", 2);
        items.put("hash4", 2);

        invoiceItems.setItems(items);

        String res = new Gson().toJson(invoiceItems);

        invoiceItems = new Gson().fromJson(res, InvoiceItems.class);

    }
}
