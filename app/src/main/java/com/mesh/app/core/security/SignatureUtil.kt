package com.mesh.app.core.security

import android.util.Base64
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.security.PrivateKey
import java.security.PublicKey

object SignatureUtil {

    /**
     * Signs [data] with the given [privateKey].
     *
     * BouncyCastle's Ed25519 low-level API needs the raw 32-byte seed, not the
     * full PKCS#8 DER encoding. We try two strategies:
     *  1. If it's a BC key, call getEncoded() which gives us PKCS#8 and we take
     *     the last 32 bytes of the key material section (seed).
     *  2. Fall back to the standard encoded form and take the last 32 bytes.
     *
     * The PKCS#8 structure for Ed25519 is:
     *   30 2e 02 01 00 30 05 06 03 2b 65 70 04 22 04 20 <32 bytes seed>
     *   total = 48 bytes, seed starts at offset 16.
     */
    fun sign(data: String, privateKey: PrivateKey): String {
        val signer = Ed25519Signer()
        val rawSeed = extractRawPrivateKey(privateKey.encoded)
        val pk = Ed25519PrivateKeyParameters(rawSeed, 0)
        signer.init(true, pk)
        val bytes = data.toByteArray(Charsets.UTF_8)
        signer.update(bytes, 0, bytes.size)
        return Base64.encodeToString(signer.generateSignature(), Base64.NO_WRAP)
    }

    fun verify(data: String, signature: String, publicKey: PublicKey): Boolean {
        val signer = Ed25519Signer()
        val rawPub = extractRawPublicKey(publicKey.encoded)
        val pub = Ed25519PublicKeyParameters(rawPub, 0)
        signer.init(false, pub)
        val bytes = data.toByteArray(Charsets.UTF_8)
        signer.update(bytes, 0, bytes.size)
        return runCatching {
            signer.verifySignature(Base64.decode(signature, Base64.NO_WRAP))
        }.getOrDefault(false)
    }

    /**
     * Extract the 32-byte raw seed from a PKCS#8 DER-encoded Ed25519 private key.
     * PKCS#8 Ed25519 is always 48 bytes; seed is always the last 32.
     */
    private fun extractRawPrivateKey(encoded: ByteArray): ByteArray {
        require(encoded.size >= 32) { "Private key encoding too short: ${encoded.size}" }
        return encoded.copyOfRange(encoded.size - 32, encoded.size)
    }

    /**
     * Extract the 32-byte raw public key from an X.509 SubjectPublicKeyInfo DER
     * encoding for Ed25519. Structure is always 44 bytes; key is last 32.
     */
    private fun extractRawPublicKey(encoded: ByteArray): ByteArray {
        require(encoded.size >= 32) { "Public key encoding too short: ${encoded.size}" }
        return encoded.copyOfRange(encoded.size - 32, encoded.size)
    }
}
