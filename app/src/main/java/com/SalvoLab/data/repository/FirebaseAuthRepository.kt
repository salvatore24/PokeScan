package com.SalvoLab.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.SalvoLab.domain.model.User
import com.SalvoLab.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa ->
            trySend(fa.currentUser?.toDomain())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInAnonymously(): Result<User> = runCatching {
        auth.signInAnonymously().await().user!!.toDomain()
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await().user!!.toDomain()
    }

    override suspend fun registerWithEmail(email: String, password: String): Result<User> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await().user!!.toDomain()
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await().user!!.toDomain()
    }

    override suspend fun signOut() = auth.signOut()

    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        auth.currentUser?.delete()?.await() ?: error("No user logged in")
    }

    private fun FirebaseUser.toDomain() = User(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString(),
        isAnonymous = isAnonymous
    )
}
