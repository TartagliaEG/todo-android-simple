package test.br.todolist.domain.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;


public abstract class SQLiteSchema extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "project.db";
  private static final int DATABASE_VERSION = 1;

  public SQLiteSchema(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
}
