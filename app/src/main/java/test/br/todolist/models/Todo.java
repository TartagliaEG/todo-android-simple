package test.br.todolist.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

/**
 * Created by erik on 01/06/17.
 * ...
 */
public class Todo implements Parcelable {
  private final UUID mId;
  private Date mCreationDate;
  private String mSubject;

  private boolean mDone;
  private Date mCompletionDate;

  public Todo() {
    this.mSubject = "";
    this.mId = UUID.randomUUID();
    this.mCreationDate = new Date();
  }

  public Todo(UUID id, @NonNull String subject, Date creationDate, Date completionDate, boolean done) {
    this.mId = id;
    this.mSubject = subject;
    this.mCreationDate = creationDate;
    this.mCompletionDate = completionDate;
    this.mDone = done;
  }

  public UUID getId() {
    return mId;
  }

  public Date getCompletionDate() {
    return mCompletionDate;
  }

  public Date getCreationDate() {
    return mCreationDate;
  }

  public boolean isDone() {
    return mDone;
  }

  public void setDone(boolean done) {
    mDone = done;
    mCompletionDate = done ? new Date() : null;
  }

  public String getSubject() {
    return mSubject;
  }

  public void setCreationDate(Date creationDate) {
    mCreationDate = creationDate;
  }

  public void setSubject(String subject) {
    mSubject = subject;
  }


  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.mId.toString());
    dest.writeLong(this.mCreationDate != null ? this.mCreationDate.getTime() : -1);
    dest.writeString(this.mSubject);
    dest.writeByte(this.mDone ? (byte) 1 : (byte) 0);
    dest.writeLong(this.mCompletionDate != null ? this.mCompletionDate.getTime() : -1);
  }

  protected Todo(Parcel in) {
    this.mId = UUID.fromString(in.readString());
    long tmpMCreationDate = in.readLong();
    this.mCreationDate = tmpMCreationDate == -1 ? null : new Date(tmpMCreationDate);
    this.mSubject = in.readString();
    this.mDone = in.readByte() != 0;
    long tmpMCompletionDate = in.readLong();
    this.mCompletionDate = tmpMCompletionDate == -1 ? null : new Date(tmpMCompletionDate);
  }

  public static final Creator<Todo> CREATOR = new Creator<Todo>() {
    @Override public Todo createFromParcel(Parcel source) {
      return new Todo(source);
    }

    @Override public Todo[] newArray(int size) {
      return new Todo[size];
    }
  };

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Todo todo = (Todo) o;

    return mId.equals(todo.mId);

  }

  @Override public int hashCode() {
    return mId.hashCode();
  }
}
