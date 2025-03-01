package com.protect.jikigo.data.repo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.protect.jikigo.data.model.UserPaymentHistory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class  PointHistoryRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun loadPointHistory(userId: String, selectedDate: String, item: (MutableList<UserPaymentHistory>) -> Unit) {
        firestore.collection("UserInfo").document(userId)
            .collection("Calendar").document(selectedDate)
            .collection("PaymentHistory").get()
            .addOnSuccessListener { documents ->
                val paymentHistoryList = mutableListOf<UserPaymentHistory>()
                for (document in documents) {
                    val paymentData = document.toObject(UserPaymentHistory::class.java).copy(docId = document.id)
                    paymentHistoryList.add(paymentData)
                }
                paymentHistoryList.sortByDescending { it.paymentDate }
                item(paymentHistoryList)
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreError", "Error getting documents: ", exception)
            }
    }


    fun loadCalendar(userId: String, year: Int, month: Int, item: (MutableList<String>, MutableList<UserPaymentHistory>) -> Unit) {
        firestore.collection("UserInfo").document(userId).collection("Calendar").get()
            .addOnSuccessListener {
                val dateList = mutableListOf<String>()
                val date = String.format("%04d-%02d", year, month)
                for(document in it.documents) {
                    if(document.id.substring(0, 7) == date) {
                        dateList.add(document.id)
                    }
                    Log.d("test", document.id.substring(0, 7))
                    Log.d("test", dateList.toString())
                }
                val pointList = mutableListOf<UserPaymentHistory>()
                dateList.forEach {
                    firestore.collection("UserInfo").document(userId).collection("Calendar").document(it).collection("PaymentHistory").get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                            val paymentData = document.toObject(UserPaymentHistory::class.java).copy(docId = document.id)
                            pointList.add(paymentData)
                            }
                            Log.d("test", pointList.toString())
                            item(dateList, pointList)
                        }
                        .addOnFailureListener {  }

                }
            }
            .addOnFailureListener { Log.w("FirestoreError", "Error getting documents: ", it) }
        // 날짜 값 문서를 다 들고오고
        // 날짜 값에 해당하는 값 리스트를 따로 들고 와 처리한다?

        // 날짜 값과 리스트가 저장되어 있는 맵 리스트를 만들고
        // 한번에 처리한다?

        // -> 원하는 달에 맞는 값을 들고와야 함 어떻게??
        // -> 원하는 달에 맞는 값을 들고온다면 위에 두 방법으로 처리할 수 있나?

    }
}