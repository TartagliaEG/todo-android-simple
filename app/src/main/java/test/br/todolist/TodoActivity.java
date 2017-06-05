package test.br.todolist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import test.br.todolist.models.Todo;

public class TodoActivity extends AppCompatActivity implements TodoListFragment.ActionsCallback, TodoEditCreateFragment.ActionsCallback {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FragmentManager fm = getSupportFragmentManager();
    Fragment frag = fm.findFragmentById(R.id.fragment_container);
    getLoaderManager();

    if (frag == null) {
      fm.beginTransaction().replace(R.id.fragment_container, new TodoListFragment()).commit();
    }
  }

  @Override
  public void onViewTodo(Todo todo) {
    getSupportFragmentManager()
      .beginTransaction()
      .addToBackStack("UC_TODO")
      .setCustomAnimations(
        android.R.anim.slide_in_left,
        android.R.anim.slide_out_right)
      .replace(R.id.fragment_container, TodoEditCreateFragment.newInstance(todo, false))
      .commit();
  }

  @Override
  public void onEditTodo(Todo todo) {
    getSupportFragmentManager()
      .beginTransaction()
      .addToBackStack("UC_TODO")
      .setCustomAnimations(
        android.R.anim.slide_in_left,
        android.R.anim.slide_out_right)
      .replace(R.id.fragment_container, TodoEditCreateFragment.newInstance(todo, true))
      .commit();
  }

  @Override
  public void onCreateTodo() {
    getSupportFragmentManager()
      .beginTransaction()
      .addToBackStack("UC_TODO")
      .setCustomAnimations(
        android.R.anim.slide_in_left,
        android.R.anim.slide_out_right)
      .replace(R.id.fragment_container, TodoEditCreateFragment.newInstance())
      .commit();
  }

  @Override
  public void onConfirmTodo(Todo todo) {
    getSupportFragmentManager()
      .popBackStack();
  }
}
