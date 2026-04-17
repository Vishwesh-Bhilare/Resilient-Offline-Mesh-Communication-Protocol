package com.mesh.app.data.repository

import com.mesh.app.data.local.db.PeerDao
import com.mesh.app.data.local.entity.PeerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeerRepository @Inject constructor(
    private val peerDao: PeerDao
) {
    fun observePeers(): Flow<List<PeerEntity>> = peerDao.getAll()
    suspend fun getPeer(id: String): PeerEntity? = peerDao.getById(id)
    suspend fun upsert(peer: PeerEntity) = peerDao.upsert(peer)
}
