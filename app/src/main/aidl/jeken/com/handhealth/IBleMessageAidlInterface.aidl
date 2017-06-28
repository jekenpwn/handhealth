// IBleMessageAidlInterface.aidl
package jeken.com.handhealth;

// Declare any non-default types here with import statements

interface IBleMessageAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    void setBleMessage(String message);
    String getBleMessage();
}
