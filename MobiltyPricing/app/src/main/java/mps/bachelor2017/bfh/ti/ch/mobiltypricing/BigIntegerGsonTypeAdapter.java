package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by Pascal on 29.09.2017.
 */

public class BigIntegerGsonTypeAdapter extends TypeAdapter<BigInteger> {

    @Override
    public BigInteger read(JsonReader reader) throws IOException {
        // TODO Auto-generated method stub
        return new BigInteger(reader.nextString());
    }

    @Override
    public void write(JsonWriter writer, BigInteger bigInteger) throws IOException {
        // TODO Auto-generated method stub
        writer.value(bigInteger.toString());
    }

}
