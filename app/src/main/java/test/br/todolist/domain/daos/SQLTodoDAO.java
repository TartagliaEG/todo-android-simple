package test.br.todolist.domain.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import test.br.todolist.domain.sqlite.SQLiteDAO;
import test.br.todolist.domain.sqlite.SQLiteSchema;
import test.br.todolist.models.Todo;


@SuppressWarnings("WeakerAccess")
public class SQLTodoDAO extends SQLiteSchema implements SQLiteDAO<Todo>, SQLiteDAO.Entity<Todo> {
  public static final String TABLE_NAME = "TODO";
  public static final String C_ID = "_id";
  public static final String C_SUBJECT = "subject";
  public static final String C_CREATION_DATE = "creation_date";
  public static final String C_COMPLETION_DATE = "completion_date";
  public static final String C_DONE = "done";
  private static final String[] C_ALL = new String[]{C_ID, C_SUBJECT, C_CREATION_DATE, C_COMPLETION_DATE, C_DONE};

  public SQLTodoDAO(Context context) {
    super(context);
  }

  @Override public boolean insert(@NonNull Todo entity) {
    return getWritableDatabase().insert(TABLE_NAME, null, generateContentValues(entity)) != -1;
  }

  @Override public boolean insert(@NonNull List<Todo> entities) {
    SQLiteDatabase db = getWritableDatabase();

    for (Todo todo : entities)
      db.insert(TABLE_NAME, null, generateContentValues(todo));

    db.setTransactionSuccessful();
    db.endTransaction();

    return true;
  }

  @Override public boolean delete(@NonNull Todo entity) {
    return getWritableDatabase().delete(TABLE_NAME, C_ID + " = ?", new String[]{entity.getId().toString()}) != -1;
  }

  @Override public boolean update(@NonNull Todo entity) {
    return getWritableDatabase().update(TABLE_NAME, generateContentValues(entity), C_ID + " = ?", new String[]{entity.getId().toString()}) != -1;
  }

  @Override public List<Todo> read() {
    return read(new Selection().columns(C_ALL));
  }

  @Override public List<Todo> read(@NonNull Selection selection) {
    Cursor cursor = getReadableDatabase()
      .query(
        TABLE_NAME,
        selection.getColumns(),
        selection.getSelection(),
        selection.getSelectionArgs(),
        selection.getGroupBy(),
        selection.getHaving(),
        selection.getOrderBy());

    List<Todo> todos = new ArrayList<>();

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      todos.add(generateEntity(cursor));
      cursor.moveToNext();
    }

    cursor.close();
    return todos;
  }


  // UTILS

  @Override
  public @NonNull ContentValues generateContentValues(@NonNull Todo todo) {
    ContentValues values = new ContentValues();
    values.put(C_ID, todo.getId().toString());
    values.put(C_SUBJECT, todo.getSubject());
    values.put(C_CREATION_DATE, todo.getCreationDate().getTime());
    values.put(C_COMPLETION_DATE, todo.isDone() ? todo.getCreationDate().getTime() : null);
    values.put(C_DONE, todo.isDone());
    return values;
  }

  @Override
  public @NonNull Todo generateEntity(Cursor cursor) {
    boolean done = cursor.getInt(cursor.getColumnIndexOrThrow(C_DONE)) != 0;

    return new Todo(
      UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(C_ID))),
      cursor.getString(cursor.getColumnIndexOrThrow(C_SUBJECT)),
      new Date(cursor.getLong(cursor.getColumnIndexOrThrow(C_CREATION_DATE))),
      done ? new Date(cursor.getLong(cursor.getColumnIndexOrThrow(C_COMPLETION_DATE))) : null,
      done);
  }


  // DATABASE SPECIFICS

  @Override
  public void onCreate(android.database.sqlite.SQLiteDatabase db) {
    String createCommand = new StringBuilder()
      // @formatter:off
      .append("create table ")   .append(TABLE_NAME)
      .append(" (")
      .append(C_ID)              .append(" text primary key, ")
      .append(C_SUBJECT)         .append(" text not null, "   )
      .append(C_CREATION_DATE)   .append(" integer not null, ")
      .append(C_COMPLETION_DATE) .append(" integer null, "    )
      .append(C_DONE)            .append(" integer not null"  )
      .append(");")
      .toString();
      // @formatter:on
    db.execSQL(createCommand);
    Todo todo1 = new Todo(UUID.randomUUID(), "First TODO item", new Date(), null, false);
    Todo todo2 = new Todo(UUID.randomUUID(), "Second TODO item", new Date(), new Date(), true);
    db.insert(TABLE_NAME, null, generateContentValues(todo1));
    db.insert(TABLE_NAME, null, generateContentValues(todo2));
  }

  @Override
  public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
    // do nothing
  }
}

