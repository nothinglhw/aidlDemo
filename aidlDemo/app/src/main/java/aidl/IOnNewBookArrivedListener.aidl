// IOnNewBookArrivedListener.aidl
package aidl;
import aidl.Book;
// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
    void onNewBookArrivedListener(in Book book);
}
