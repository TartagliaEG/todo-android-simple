package test.br.todolist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import test.br.todolist.domain.daos.SQLTodoDAO;
import test.br.todolist.models.Todo;

/**
 * Created by erik on 02/06/17.
 * ...
 */

public class TodoListFragment extends Fragment {
  private static final DateFormat mDtFormat = SimpleDateFormat.getDateTimeInstance();

  private ListView mListView;
  private TodoListAdapter mAdapter;
  private SQLTodoDAO mTodoDAO;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_todo_list, container, false);
    setHasOptionsMenu(true);

    mTodoDAO = new SQLTodoDAO(getContext());
    mAdapter = new TodoListAdapter(getContext(), mTodoDAO.read());

    Log.d("TEST", "onCreateView: " + mTodoDAO.read().size());

    mListView = (ListView) v.findViewById(R.id.todo_list_view);
    mListView.setAdapter(mAdapter);

    return v;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.todo_list, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.new_todo)
      mCallback.onCreateTodo();
    return super.onOptionsItemSelected(item);
  }

  private class TodoListAdapter extends ArrayAdapter<Todo> {
    private List<Todo> mTodos;

    public TodoListAdapter(Context context, List<Todo> todos) {
      super(context, -1);
      mTodos = todos;
    }

    @Override public int getCount() {
      return mTodos.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      Todo todo = mTodos.get(position);

      convertView = (convertView == null)
        ? LayoutInflater.from(getContext()).inflate(R.layout.todo_item, parent, false)
        : convertView;

      TodoViewHolder holder = (convertView.getTag() != null)
        ? (TodoViewHolder) convertView.getTag()
        : new TodoViewHolder(getContext(), (ViewFlipper) convertView);

      convertView.setTag(holder);
      holder.setTodo(todo);

      return convertView;
    }

    private class TodoViewHolder {
      Todo mTodo;
      TextView mTxtSubject;
      TextView mTxtCreationDate;
      TextView mTxtCompletionDate;
      ViewFlipper mViewFlipper;

      TodoViewHolder(Context context, final ViewFlipper flipper) {
        mViewFlipper = flipper;

        mTxtSubject = (TextView) flipper.findViewById(R.id.todo_edt_subject);
        mTxtCreationDate = (TextView) flipper.findViewById(R.id.todo_creation_date);
        mTxtCompletionDate = (TextView) flipper.findViewById(R.id.todo_completion_date);

        flipper.setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right));

        flipper.findViewById(R.id.todo_action_back).setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            flipper.setDisplayedChild(0);
          }
        });

        flipper.findViewById(R.id.todo_action_view).setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            TodoListFragment.this.mCallback.onViewTodo(mTodo);
          }
        });

        flipper.findViewById(R.id.todo_action_edit).setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            TodoListFragment.this.mCallback.onEditTodo(mTodo);
          }
        });

        flipper.findViewById(R.id.todo_action_delete).setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            SQLTodoDAO dao = new SQLTodoDAO(getContext());
            dao.delete(mTodo);
            mTodos.remove(mTodo);
            mAdapter.notifyDataSetChanged();
          }
        });

        flipper.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            if (flipper.getDisplayedChild() == 0)
              flipper.setDisplayedChild(1);
          }
        });
      }

      void setTodo(Todo todo) {
        mTodo = todo;
        mTxtSubject.setText(todo.getSubject());
        mTxtCompletionDate.setText(todo.isDone() ? "Completed at: " + mDtFormat.format(todo.getCreationDate()) : "<Pending TODO>");
        mTxtCreationDate.setText("Created at: " + mDtFormat.format(todo.getCreationDate()));
      }

    }
  }


  private ActionsCallback mCallback;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mCallback = (ActionsCallback) context;
  }

  public interface ActionsCallback {
    void onViewTodo(Todo todo);

    void onEditTodo(Todo todo);

    void onCreateTodo();
  }

}
