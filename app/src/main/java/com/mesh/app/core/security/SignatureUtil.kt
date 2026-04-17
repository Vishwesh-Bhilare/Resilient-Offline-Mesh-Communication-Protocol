package com.mesh.app.core.security

import android.util.Base64
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.security.PrivateKey
import java.security.PublicKey

object SignatureUtil {
    fun sign(data: String, privateKey: PrivateKey): String {
        val signer = Ed25519Signer()
        val pk = Ed25519PrivateKeyParameters(privateKey.encoded.takeLast(32).toByteArray(), 0)
        signer.init(true, pk)
        val bytes = data.toByteArray(Charsets.UTF_8)
        signer.update(bytes, 0, bytes.size)
        return Base64.encodeToString(signer.generateSignature(), Base64.NO_WRAP)
    }

    fun verify(data: String, signature: String, publicKey: PublicKey): Boolean {
        val signer = Ed25519Signer()
        val pub = Ed25519PublicKeyParameters(publicKey.encoded.takeLast(32).toByteArray(), 0)
        signer.init(false, pub)
        val bytes = data.toByteArray(Charsets.UTF_8)
        signer.update(bytes, 0, bytes.size)
        return signer.verifySignature(Base64.decode(signature, Base64.NO_WRAP))
    }
}
