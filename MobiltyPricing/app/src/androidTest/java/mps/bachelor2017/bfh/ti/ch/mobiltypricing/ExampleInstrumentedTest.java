package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;
import requests.JoinRequest;
import responses.JoinResponse;
import util.Generator;
import util.HashHelper;
import util.JoinHelper;
import util.SignHelper;
import util.VerifyHelper;

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

//    @Test
//    public void TollTest() {
//        SharedPreferences settings = InstrumentationRegistry.getTargetContext().getSharedPreferences(Const.PreferenceKey, 0);
//
//        PublicKey pk =  new MobileGroup(settings).getPublicKey();
//        SecretKey sk = new MobileSecretKey(settings);
//
//        Tuple a = new Tuple();
//        Date d = new Date();
//
//        a.setLatitude(new BigDecimal("9").setScale(10, RoundingMode.HALF_UP));
//        a.setLongitude(new BigDecimal("8").setScale(10, RoundingMode.HALF_UP));
//        a.setCreated(d);
//
//
//        byte[] testmessage = HashHelper.getHash(a);
//
//        a.setLongitude(new BigDecimal("10").setScale(10, RoundingMode.HALF_UP));
//        byte[] testmessage2 = HashHelper.getHash(a);
//
//        DemoSignature signature = new DemoSignature();
//        SignHelper.sign(sk, pk, testmessage, signature);
//
//
////
////
////        TollTask task = new TollTask(new TollTask.TollTaskListener() {
////            @Override
////            public void onTollError(Error error) {
////                assertTrue(false);
////            }
////
////            @Override
////            public void onTollSuccessfull() {
////                assertTrue(true);
////            }
////        }, InstrumentationRegistry.getTargetContext(), new MobileGroup(settings),new MobileSecretKey(settings));
////
////        task.execute();
////
////        while(!task.isCancelled()) {
////            try {
////                Thread.sleep(100);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }
//    }

    @Test
    public void SecGroup() {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Log.i("CRYPTO","provider: "+provider.getName());
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                Log.i("CRYPTO","  algorithm: "+service.getAlgorithm());
            }
        }
    }



    @Test
    public void VerifyTest() {
        String signature = "MD4CHQCQJnB+SIVC+vvT/7sHdl0oM8Rmw4nA2W7HQcnkAh0AmEF7zQwFp4AB4aO2QyBAioJx3naIeEHU56at9A==";
        String messageTxt = "YrrZNmpHRGoJVcKvLlQfORNwNZRswJu7cPBEETDRI7M=";




       boolean res = Helper.verifyProviderMessage(Base64.getDecoder().decode(messageTxt), signature, InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void SerializeTest() {
        InvoiceItems invoiceItems = new InvoiceItems();

        Map<String, BigInteger> items = new HashMap<String, BigInteger>();

        items.put("hash", new BigInteger("2"));
        items.put("hash2", new BigInteger("2"));
        items.put("hash3", new BigInteger("2"));
        items.put("hash4", new BigInteger("2"));

        invoiceItems.setItems(items);

        String res = new Gson().toJson(invoiceItems);

        invoiceItems = new Gson().fromJson(res, InvoiceItems.class);

    }


    @Test
    public void testSignAndVerify() {

        PublicKey publicKey = new DemoPublicKey();
        ManagerKey managerKey = new DemoManagerKey();

        Generator.generate(publicKey, managerKey);

        SecretKey memberKey = new DemoSecretKey();
        JoinHelper.init(publicKey, memberKey);

        JoinRequest joinRequest = new JoinRequest(memberKey);

        JoinResponse joinResponse = JoinHelper.join(publicKey, managerKey, joinRequest);

        memberKey.maintainResponse(joinResponse);


        Tuple a = new Tuple();
        Date d = new Date();

        a.setLatitude(new BigDecimal("9").setScale(10, RoundingMode.HALF_UP));
        a.setLongitude(new BigDecimal("8").setScale(10, RoundingMode.HALF_UP));
        a.setCreated(d);


        byte[] testmessage = HashHelper.getHash(a);

        a.setLongitude(new BigDecimal("10").setScale(10, RoundingMode.HALF_UP));
        byte[] testmessage2 = HashHelper.getHash(a);

        DemoSignature signature = new DemoSignature();
        SignHelper.sign(memberKey, publicKey, testmessage, signature);

        System.out.println("sig:" + Arrays.toString(HashHelper.getHash(signature)));

        DemoSignature signature2 = new DemoSignature();

        SignHelper.sign(memberKey, publicKey, testmessage, signature2);
        System.out.println("sig2:" + Arrays.toString(HashHelper.getHash(signature2)));

        Boolean res = VerifyHelper.verify(publicKey, signature, testmessage);
        assertTrue(res);

        Boolean res2 = VerifyHelper.verify(publicKey, signature, HashHelper.getHash(a));
        assertTrue(res);
    }
}
