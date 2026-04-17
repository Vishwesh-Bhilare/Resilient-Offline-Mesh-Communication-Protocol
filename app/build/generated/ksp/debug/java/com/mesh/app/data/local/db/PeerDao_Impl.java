package com.mesh.app.data.local.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mesh.app.data.local.entity.PeerEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PeerDao_Impl implements PeerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PeerEntity> __insertionAdapterOfPeerEntity;

  public PeerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPeerEntity = new EntityInsertionAdapter<PeerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `peers` (`peerId`,`lastSyncTime`,`syncHash`,`address`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PeerEntity entity) {
        statement.bindString(1, entity.getPeerId());
        statement.bindLong(2, entity.getLastSyncTime());
        statement.bindString(3, entity.getSyncHash());
        statement.bindString(4, entity.getAddress());
      }
    };
  }

  @Override
  public Object upsert(final PeerEntity peer, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPeerEntity.insert(peer);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id, final Continuation<? super PeerEntity> $completion) {
    final String _sql = "SELECT * FROM peers WHERE peerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PeerEntity>() {
      @Override
      @Nullable
      public PeerEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPeerId = CursorUtil.getColumnIndexOrThrow(_cursor, "peerId");
          final int _cursorIndexOfLastSyncTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTime");
          final int _cursorIndexOfSyncHash = CursorUtil.getColumnIndexOrThrow(_cursor, "syncHash");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final PeerEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPeerId;
            _tmpPeerId = _cursor.getString(_cursorIndexOfPeerId);
            final long _tmpLastSyncTime;
            _tmpLastSyncTime = _cursor.getLong(_cursorIndexOfLastSyncTime);
            final String _tmpSyncHash;
            _tmpSyncHash = _cursor.getString(_cursorIndexOfSyncHash);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            _result = new PeerEntity(_tmpPeerId,_tmpLastSyncTime,_tmpSyncHash,_tmpAddress);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PeerEntity>> getAll() {
    final String _sql = "SELECT * FROM peers";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"peers"}, new Callable<List<PeerEntity>>() {
      @Override
      @NonNull
      public List<PeerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPeerId = CursorUtil.getColumnIndexOrThrow(_cursor, "peerId");
          final int _cursorIndexOfLastSyncTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTime");
          final int _cursorIndexOfSyncHash = CursorUtil.getColumnIndexOrThrow(_cursor, "syncHash");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final List<PeerEntity> _result = new ArrayList<PeerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PeerEntity _item;
            final String _tmpPeerId;
            _tmpPeerId = _cursor.getString(_cursorIndexOfPeerId);
            final long _tmpLastSyncTime;
            _tmpLastSyncTime = _cursor.getLong(_cursorIndexOfLastSyncTime);
            final String _tmpSyncHash;
            _tmpSyncHash = _cursor.getString(_cursorIndexOfSyncHash);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            _item = new PeerEntity(_tmpPeerId,_tmpLastSyncTime,_tmpSyncHash,_tmpAddress);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
