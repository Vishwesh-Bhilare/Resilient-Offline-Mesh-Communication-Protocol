package com.mesh.app.core.identity

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mesh.app.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val prefs by lazy {
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
    }

    init {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    @Synchronized
    fun getOrCreateKeyPair(): KeyPair {
        val privateB64 = prefs.getString("private", null)
        val publicB64 = prefs.getString("public", null)
        if (privateB64 != null && publicB64 != null) {
            return decodeKeyPair(privateB64, publicB64)
        }

        val generator = KeyPairGenerator.getInstance("Ed25519", BouncyCastleProvider.PROVIDER_NAME)
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
        return DeviceIdentity(getDeviceId(), Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP))
    }

    private fun decodeKeyPair(privateB64: String, publicB64: String): KeyPair {
        return try {
            val kf = KeyFactory.getInstance("Ed25519", BouncyCastleProvider.PROVIDER_NAME)
            val publicKey: PublicKey = kf.generatePublic(X509EncodedKeySpec(Base64.decode(publicB64, Base64.NO_WRAP)))
            val privateKey: PrivateKey = kf.generatePrivate(PKCS8EncodedKeySpec(Base64.decode(privateB64, Base64.NO_WRAP)))
            KeyPair(publicKey, privateKey)
        } catch (t: Throwable) {
            Logger.w("Failed to decode persisted keys, regenerating", t)
            prefs.edit().clear().apply()
            getOrCreateKeyPair()
        }
    }
}
