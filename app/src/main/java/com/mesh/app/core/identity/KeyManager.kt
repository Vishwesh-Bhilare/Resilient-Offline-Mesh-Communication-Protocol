package com.mesh.app.core.identity

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mesh.app.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Ed25519 OID — required on Android since "Ed25519" alias is not always registered
        private const val ED25519_OID = "1.3.101.112"
        private const val BC_PROVIDER = "BC"
    }

    // Lazy init to avoid crashing during DI graph construction
    private val prefs by lazy { createEncryptedPrefs() }

    init {
        ensureBouncyCastle()
    }

    private fun ensureBouncyCastle() {
        // FIX: 4 — avoid provider replacement when full BC is already present; guard restricted provider mutation failures.
        try {
            val existing = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            when {
                existing == null -> {
                    Security.insertProviderAt(BouncyCastleProvider(), 1)
                    Logger.i("Inserted BouncyCastle provider")
                }
                existing.javaClass.name == BouncyCastleProvider::class.java.name -> {
                    // Already the correct full BC provider — no action needed
                    Logger.d("BouncyCastle provider already present and correct")
                }
                else -> {
                    Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
                    Security.insertProviderAt(BouncyCastleProvider(), 1)
                    Logger.i("Replaced Android BC stub with full BouncyCastle provider")
                }
            }
        } catch (t: Throwable) {
            Logger.w("BouncyCastle provider setup failed — crypto operations may fail", t)
        }
    }

    private fun createEncryptedPrefs(): android.content.SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                "mesh_keys",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (t: Throwable) {
            // Fallback: if EncryptedSharedPreferences fails (e.g., keystore wipe),
            // clear and recreate. This loses the identity but prevents a crash loop.
            Logger.w("EncryptedSharedPreferences init failed, falling back to plain prefs", t)
            context.getSharedPreferences("mesh_keys_fallback", Context.MODE_PRIVATE)
        }
    }

    @Synchronized
    fun getOrCreateKeyPair(): KeyPair {
        val privateB64 = prefs.getString("private", null)
        val publicB64 = prefs.getString("public", null)
        if (privateB64 != null && publicB64 != null) {
            val pair = runCatching { decodeKeyPair(privateB64, publicB64) }.getOrNull()
            if (pair != null) return pair
            // Corrupt keys — regenerate
            Logger.w("Stored keys could not be decoded, regenerating identity")
            prefs.edit().clear().apply()
        }
        return generateAndPersistKeyPair()
    }

    private fun generateAndPersistKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance(ED25519_OID, BC_PROVIDER)
        val kp = generator.generateKeyPair()
        prefs.edit()
            .putString("private", Base64.encodeToString(kp.private.encoded, Base64.NO_WRAP))
            .putString("public", Base64.encodeToString(kp.public.encoded, Base64.NO_WRAP))
            .apply()
        Logger.i("Generated new Ed25519 identity")
        return kp
    }

    fun getDeviceId(): String {
        val pub = getOrCreateKeyPair().public.encoded
        val digest = MessageDigest.getInstance("SHA-256").digest(pub)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun getIdentity(): DeviceIdentity {
        val keyPair = getOrCreateKeyPair()
        return DeviceIdentity(
            deviceId = getDeviceId(),
            publicKeyBase64 = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
        )
    }

    private fun decodeKeyPair(privateB64: String, publicB64: String): KeyPair {
        val kf = KeyFactory.getInstance(ED25519_OID, BC_PROVIDER)
        val publicKey: PublicKey = kf.generatePublic(
            X509EncodedKeySpec(Base64.decode(publicB64, Base64.NO_WRAP))
        )
        val privateKey: PrivateKey = kf.generatePrivate(
            PKCS8EncodedKeySpec(Base64.decode(privateB64, Base64.NO_WRAP))
        )
        return KeyPair(publicKey, privateKey)
    }
}
