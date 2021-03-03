package com.roman.noto.ui.EditHashtags;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.roman.noto.R;
import com.roman.noto.RecyclerViewEmptySupport;
import com.roman.noto.data.Hashtag;
import com.roman.noto.data.repository.CacheRepository;
import com.roman.noto.data.repository.LocalRepository;
import com.roman.noto.ui.ChooseHashtags.ChooseHashtagsAdapter;
import com.roman.noto.ui.ChooseHashtags.ChooseHashtagsContract;
import com.roman.noto.ui.ChooseHashtags.ChooseHashtagsPresenter;
import com.roman.noto.ui.Notes.NotesActivity;
import com.roman.noto.util.AppExecutors;

import java.util.List;

public class EditHashtagsActivity extends AppCompatActivity implements EditHashtagsContract.View, OnKeyboardVisibilityListener  {

    static final String TAG = "EditHashtagsActivity";

    public EditHashtagsContract.Presenter presenter;

    EditHashtagsAdapter adapter;
    RecyclerViewEmptySupport hashtagsRecyclerView;
    Toolbar toolbar;
    SearchView searchView;
    View containerAddHashtag, containerHashtagsList;
    View emptyHashtagsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hashtags);
        hashtagsRecyclerView = findViewById(R.id.activity_edit_hashtags_list);
        toolbar = findViewById(R.id.activity_edit_hashtag_toolbar);
        searchView = findViewById(R.id.activity_edit_hashtag_search_view);
        containerAddHashtag = findViewById(R.id.activity_edit_hashtag_container_add);
        containerHashtagsList = findViewById(R.id.activity_edit_hashtag_container_list);
        emptyHashtagsList = findViewById(R.id.activity_edit_hashtag_empty_view);
        setKeyboardVisibilityListener(this);


        presenter = new EditHashtagsPresenter(this, CacheRepository.getInstance(
                LocalRepository.getRepository(
                        new AppExecutors(), getApplicationContext()
                )
        ));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addHashtag();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    //добавление хештега
                    containerAddHashtag.setVisibility(View.VISIBLE);
                    containerHashtagsList.setVisibility(View.INVISIBLE);
                } else {
                    //вернуть
                    containerAddHashtag.setVisibility(View.INVISIBLE);
                    containerHashtagsList.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        containerAddHashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHashtag();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hashtagsRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new EditHashtagsAdapter(this, presenter);
        hashtagsRecyclerView.setAdapter(adapter);
        hashtagsRecyclerView.setEmptyView(emptyHashtagsList);

        presenter.loadHashtags();

    }





    @Override
    public void onVisibilityChanged(boolean visible) {
        adapter.setKeyboardStatus(visible);
    }


    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    //Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }



    private void addHashtag() {
        presenter.addNewHashtag(searchView.getQuery().toString());
        searchView.setQuery("", false);
        presenter.loadHashtags();



        searchView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

    }


    //Проверить на наличие физической клавиатуры
    private boolean isHardwareKeyboardAvailable(Context context) {
        return context.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        //Загрузить хештеги
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Сохранить изменения
        List<Hashtag> list = adapter.getList();
        presenter.saveHashtags(list);
    }

    @Override
    public void showHashtags(List<Hashtag> hashtagList) {
        adapter.setList(hashtagList);
    }


}