package fr.isen.gomez.untilfailure.data.nfc

sealed class NfcState {
    object NotAvailable : NfcState()
    object Available : NfcState()
}