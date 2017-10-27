package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.InvoiceItems;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;

import static org.junit.Assert.*;

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
    public void TupleQueryTest() {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        DatabaseHelper dbHelper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());

        MobileTuple mt = new MobileTuple(1, new BigDecimal("1.222"), new BigDecimal("1.2"), new Date());
        String hash = "hahahah";
        mt.setHash(hash);
        dbHelper.save(mt);

        dbHelper.setTupleStatus(hash, MobileTuple.TupleStatus.REMOTE);

        assertTrue(dbHelper.hasRemoteTupleInPeriode("10-10-2000"));
        assertFalse(dbHelper.hasRemoteTupleInPeriode("10-10-2100"));

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
