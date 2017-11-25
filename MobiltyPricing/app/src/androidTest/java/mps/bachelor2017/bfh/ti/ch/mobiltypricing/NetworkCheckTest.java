package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import data.Tuple;
import demo.DemoManagerKey;
import demo.DemoPublicKey;
import demo.DemoSecretKey;
import demo.DemoSignature;
import keys.ManagerKey;
import keys.PublicKey;
import keys.SecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.InvoiceItems;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.NetworkCheck;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import requests.JoinRequest;
import responses.JoinResponse;
import util.Generator;
import util.HashHelper;
import util.JoinHelper;
import util.SignHelper;
import util.VerifyHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class NetworkCheckTest {
    @Test
    public void test() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        final boolean[] status = {false};

        NetworkCheck networkCheck = new NetworkCheck(new NetworkCheck.NetworkCheckEvents() {
            @Override
            public void onNetworkCheckSuccessfully() {
                status[0] = true;
            }

            @Override
            public void onNetworkCheckError(Exception ex) {
                fail(ex.getMessage());
            }
        }, appContext);

        networkCheck.execute();

        Thread.sleep(10 * 1000);
        assertTrue(status[0]);
    }
}
