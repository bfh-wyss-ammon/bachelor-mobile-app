package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import util.Paillier;

import static junit.framework.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PaillierTest {
    private BigInteger v1;
    private BigInteger v2;
    private BigInteger v1e;
    private BigInteger v2e;

    Paillier providerPailler;

    @Before
    public void Setup() {
        providerPailler = new Paillier();
        v1 = BigInteger.valueOf(5);
        v2 = BigInteger.valueOf(205);
        v1e = providerPailler.encryption(v1);
        v2e = providerPailler.encryption(v2);

    }

    @Test
    public void DecryptionTest() {
        assertEquals(v1, providerPailler.decryption(v1e));
    }

    @Test
    public void MobileTest() {
        Paillier pailler = new Paillier(providerPailler.getPublicKey(), null);
        assertEquals(providerPailler.homomorphicAdd(v1e, v2e), pailler.homomorphicAdd(v1e, v2e));
    }
}
