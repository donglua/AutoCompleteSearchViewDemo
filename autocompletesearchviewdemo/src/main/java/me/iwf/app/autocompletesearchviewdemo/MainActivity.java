package me.iwf.app.autocompletesearchviewdemo;

import android.app.SearchManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;
import me.iwf.app.autocompletesearchviewdemo.adapter.SearchSuggestionAdapter;

public class MainActivity extends AppCompatActivity {

  String[] columns = new String[]{"_ID", "NAME"};

  List<String> states;

  MatrixCursor cursor = new MatrixCursor(columns);

  SearchView searchView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);

    states = Arrays.asList(getResources().getStringArray(R.array.states));
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);

    MenuItem menuItem = menu.findItem(R.id.menu_search_view);
    searchView  = (SearchView) MenuItemCompat.getActionView(menuItem);
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

    searchView.setSuggestionsAdapter(new SearchSuggestionAdapter(this, cursor));

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        Toast.makeText(MainActivity.this, "do search", Toast.LENGTH_LONG).show();
        return true;
      }

      @Override public boolean onQueryTextChange(String newText) {
        autoComplete(newText);
        return true;
      }
    });

    searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
      @Override public boolean onSuggestionSelect(int position) {
        return false;
      }

      @Override public boolean onSuggestionClick(int position) {
        if (cursor.moveToPosition(position)) {
          String state = cursor.getString(cursor.getColumnIndex("NAME"));
          searchView.setQuery(state, true);
        }
        return true;
      }
    });

    return true;
  }

  private void autoComplete(String text) {
    int count = 0;
    cursor = new MatrixCursor(columns);
    for (String state : states) {
      if (state.toLowerCase().startsWith(text.trim().toLowerCase())) {
        cursor.addRow(new Object[] { count, state });
        ++count;
      }
    }
    searchView.getSuggestionsAdapter().changeCursor(cursor);
    searchView.getSuggestionsAdapter().notifyDataSetChanged();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.menu_search_view) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

}