package test.br.todolist.domain.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.List;


public interface SQLiteDAO<Data> {

  // @formatter:off
  boolean insert(Data entity);
  boolean insert(List<Data> entities);
  boolean delete(Data entity);
  boolean update(Data entity);
  List<Data> read();
  List<Data> read(Selection selection);
  // @formatter:on

  interface Entity<T> {
    @NonNull ContentValues generateContentValues(@NonNull T entity);

    @NonNull T generateEntity(@NonNull Cursor cursor);
  }

  class Selection {
    private String[] columns;
    private String selection;
    private String[] selectionArgs;
    private String groupBy;
    private String having;
    private String orderBy;

    public Selection columns(String[] columns) {
      this.columns = columns;
      return this;
    }

    public Selection groupBy(String groupBy) {
      this.groupBy = groupBy;
      return this;
    }

    public Selection having(String having) {
      this.having = having;
      return this;
    }

    public Selection orderBy(String orderBy) {
      this.orderBy = orderBy;
      return this;
    }

    public Selection selection(String selection) {
      this.selection = selection;
      return this;
    }

    public Selection selectionArgs(String[] selectionArgs) {
      this.selectionArgs = selectionArgs;
      return this;
    }

    public String getGroupBy() {
      return groupBy;
    }

    public String getHaving() {
      return having;
    }

    public String getOrderBy() {
      return orderBy;
    }

    public String getSelection() {
      return selection;
    }

    public String[] getSelectionArgs() {
      return selectionArgs;
    }

    public String[] getColumns() {
      return columns;
    }
  }
}
