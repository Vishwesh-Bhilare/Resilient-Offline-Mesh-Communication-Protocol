package com.mesh.app.data.local.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mesh.app.data.local.entity.InProgressEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class InProgressDao_Impl implements InProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<InProgressEntity> __insertionAdapterOfInProgressEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public InProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfInProgressEntity = new EntityInsertionAdapter<InProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `in_progress` (`messageId`,`receivedChunksJson`,`totalChunks`,`firstChunkAt`,`lastChunkAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InProgressEntity entity) {
        statement.bindString(1, entity.getMessageId());
        statement.bindString(2, entity.getReceivedChunksJson());
        statement.bindLong(3, entity.getTotalChunks());
        statement.bindLong(4, entity.getFirstChunkAt());
        statement.bindLong(5, entity.getLastChunkAt());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM in_progress WHERE messageId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM in_progress WHERE lastChunkAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final InProgressEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfInProgressEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOlderThan(final long cutoff, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoff);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id, final Continuation<? super InProgressEntity> $completion) {
    final String _sql = "SELECT * FROM in_progress WHERE messageId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<InProgressEntity>() {
      @Override
      @Nullable
      public InProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "messageId");
          final int _cursorIndexOfReceivedChunksJson = CursorUtil.getColumnIndexOrThrow(_cursor, "receivedChunksJson");
          final int _cursorIndexOfTotalChunks = CursorUtil.getColumnIndexOrThrow(_cursor, "totalChunks");
          final int _cursorIndexOfFirstChunkAt = CursorUtil.getColumnIndexOrThrow(_cursor, "firstChunkAt");
          final int _cursorIndexOfLastChunkAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastChunkAt");
          final InProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpMessageId;
            _tmpMessageId = _cursor.getString(_cursorIndexOfMessageId);
            final String _tmpReceivedChunksJson;
            _tmpReceivedChunksJson = _cursor.getString(_cursorIndexOfReceivedChunksJson);
            final int _tmpTotalChunks;
            _tmpTotalChunks = _cursor.getInt(_cursorIndexOfTotalChunks);
            final long _tmpFirstChunkAt;
            _tmpFirstChunkAt = _cursor.getLong(_cursorIndexOfFirstChunkAt);
            final long _tmpLastChunkAt;
            _tmpLastChunkAt = _cursor.getLong(_cursorIndexOfLastChunkAt);
            _result = new InProgressEntity(_tmpMessageId,_tmpReceivedChunksJson,_tmpTotalChunks,_tmpFirstChunkAt,_tmpLastChunkAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
