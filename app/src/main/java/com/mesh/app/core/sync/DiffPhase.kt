package com.mesh.app.core.sync

import com.mesh.app.core.protocol.BloomFilter

class DiffPhase {
    fun calculateNeededIds(localIds: List<String>, peerBloom: BloomFilter): List<String> {
        return localIds.filter { !peerBloom.mightContain(it) }
    }
}
