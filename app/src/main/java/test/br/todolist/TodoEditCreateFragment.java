package test.br.todolist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import test.br.todolist.domain.daos.SQLTodoDAO;
import test.br.todolist.models.Todo;


public class TodoEditCreateFragment extends Fragment {
  private static final String ARG_TODO = "TODO_ITEM";
  private static final String ARG_TYPE = "IS_NEW_TODO_ITEM";

  private static final int VIEW = 1;
  private static final int EDIT = 2;
  private static final int NEW = 3;

  private AppCompatCheckBox mChkDone;
  private AppCompatEditText mEdtSubject;
  private AppCompatButton mBtnConfirm;
  private AppCompatTextView mTxtCreation;
  private AppCompatTextView mTxtCompletion;

  private ActionsCallback mCallbacks;
  private Todo mTodo;

  public static TodoEditCreateFragment newInstance(Todo todo, boolean allowEdit) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(ARG_TODO, todo);
    bundle.putInt(ARG_TYPE, allowEdit ? EDIT : VIEW);

    TodoEditCreateFragment fragment = new TodoEditCreateFragment();
    fragment.setArguments(bundle);
    return fragment;
  }


  public static TodoEditCreateFragment newInstance() {
    Bundle bundle = new Bundle();
    bundle.putInt(ARG_TYPE, NEW);

    TodoEditCreateFragment fragment = new TodoEditCreateFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_todo_edit_create, container, false);
    final Bundle bundle = getArguments();

    mChkDone = (AppCompatCheckBox) view.findViewById(R.id.todo_chk_done);
    mEdtSubject = (AppCompatEditText) view.findViewById(R.id.todo_edt_subject);
    mBtnConfirm = (AppCompatButton) view.findViewById(R.id.todo_action_confirm);
    mTxtCompletion = (AppCompatTextView) view.findViewById(R.id.todo_txt_completion_date);
    mTxtCreation = (AppCompatTextView) view.findViewById(R.id.todo_txt_creation_date);

    mTodo = bundle.getParcelable(ARG_TODO);
    mTodo = mTodo == null ? new Todo() : mTodo;
    final int actionType = bundle.getInt(ARG_TYPE);

    mEdtSubject.setText(mTodo.getSubject());
    mChkDone.setChecked(mTodo.isDone());

    if (actionType == VIEW) {
      mEdtSubject.setEnabled(false);
      mEdtSubject.setFocusable(false);
      mEdtSubject.setClickable(false);
      mChkDone.setEnabled(false);
      mBtnConfirm.setVisibility(View.GONE);
    }

    mTxtCreation.setText(mTodo.getCreationDate().toString());

    if (mTodo.isDone())
      mTxtCompletion.setText(mTodo.getCompletionDate().toString());
    else
      mTxtCompletion.setText("Not completed yet");

    mBtnConfirm.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mTodo.getSubject() != null && mTodo.getSubject().trim().length() < 3) {
          mEdtSubject.setError("You must specify at least 3 characters");
          return;
        }

        mBtnConfirm.setEnabled(false);

        SQLTodoDAO dao = new SQLTodoDAO(getContext());
        if (actionType == NEW)
          dao.insert(mTodo);
        else if (actionType == EDIT)
          dao.update(mTodo);
        else
          throw new RuntimeException("Not supposed to get here");

        mCallbacks.onConfirmTodo(mTodo);
      }
    });


    mEdtSubject.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEdtSubject.setError(null);
        mTodo.setSubject(s.toString());
      }

      @Override public void afterTextChanged(Editable s) {
      }
    });

    mChkDone.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mTodo.setDone(mChkDone.isChecked());
        mTxtCompletion.setText(mTodo.isDone() ? mTodo.getCompletionDate().toString() : "Not completed yet");
      }
    });

    return view;
  }


  @Override public void onAttach(Context context) {
    super.onAttach(context);
    mCallbacks = (ActionsCallback) context;
  }

  public interface ActionsCallback {
    void onConfirmTodo(Todo todo);
  }
}
