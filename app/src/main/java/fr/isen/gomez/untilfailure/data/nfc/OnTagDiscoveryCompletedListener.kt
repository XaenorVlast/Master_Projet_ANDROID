package fr.isen.gomez.untilfailure.data.nfc



import android.nfc.Tag

interface OnTagDiscoveryCompletedListener {
    fun onTagDiscoveryCompleted(nfcTag: Tag?, productId: TagHelper.ProductID?)
}
