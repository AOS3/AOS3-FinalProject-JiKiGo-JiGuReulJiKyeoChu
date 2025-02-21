package com.protect.jikigo.data.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.protect.jikigo.data.model.UserInfo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun addUserInfo(userInfo: UserInfo, callback: (Boolean, String?) -> Unit) {
        val collection = firestore.collection("UserInfo")
        val userId = userInfo.userId // 이메일 또는 카카오 ID 사용

        // Firestore에서 기존 사용자 확인
        collection.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("Firestore", "기존 사용자 로그인: ${userInfo.userId}")
                    callback(true, "EXISTING_USER") // 기존 사용자면 즉시 성공 처리
                } else {
                    // 2. 새로운 사용자 Firestore에 저장 (set() 사용)
                    collection.document(userId).set(userInfo)
                        .addOnSuccessListener {
                            Log.d("Firestore", "새 사용자 저장 성공: ${userInfo.userId}")
                            callback(true, userId)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "사용자 저장 실패: ${e.message}")
                            callback(false, e.message)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Firestore 조회 실패: ${e.message}")
                callback(false, e.message)
            }
    }

    suspend fun getUserInfo(id: String): UserInfo? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val document = db.collection("UserInfo").document(id).get(Source.CACHE).await() // 캐시 우선 사용
                ?: db.collection("UserInfo").document(id).get().await() // 네트워크에서 가져오기

            if (document.exists()) {
                document.toObject(UserInfo::class.java)?.also {
                    Log.d("UserRepo", "UserInfo loaded successfully for id: $id")
                }
            } else {
                Log.e("UserRepo", "UserInfo not found for id: $id")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error fetching user info: ${e.message}", e)
            null
        }
    }


    // 아이디 중복 확인
    suspend fun isIdAvailable(id: String): Boolean {
        return try {
            val snapshot = firestore
                .collection("UserInfo")
                .whereEqualTo("userId", id)
                .get()
                .await()
            snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    // 닉네임 중복 확인
    suspend fun isNickNameAvailable(nickName: String): Boolean {
        return try {
            val snapshot = firestore
                .collection("UserInfo")
                .whereEqualTo("userNickName", nickName)
                .get()
                .await()
            snapshot.isEmpty
        } catch (e: Exception) {
            Log.d("isNickNameAvailable Error isNickNameAvailable: ${e.message}", e.toString())
            false
        }
    }
}