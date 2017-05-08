package blakeolsen.cmu.design2.icrutch.crutchcontrol;

import android.provider.ContactsContract;

/**
 * every time the data is updated
 */

public interface DataAnalyzer {

    /**
     * receive new data, and update data map
     *
     * @param data the data point received
     * @param type the type of data point
     */
    void onReceive(double data, DataStreamType type);

}
