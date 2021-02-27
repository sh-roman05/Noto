package com.roman.noto.ui.ChooseHashtags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.roman.noto.R;
import com.roman.noto.RecyclerViewEmptySupport;
import com.roman.noto.data.Note;
import com.roman.noto.data.repository.CacheRepository;
import com.roman.noto.data.repository.LocalRepository;
import com.roman.noto.ui.About.AboutPresenter;
import com.roman.noto.ui.Notes.NotesActivity;
import com.roman.noto.util.AppExecutors;

import java.util.List;
import java.util.UUID;

public class ChooseHashtagsActivity extends AppCompatActivity implements ChooseHashtagsContract.View {

    static final String TAG = "ChooseHashtagsActivity";
    public ChooseHashtagsContract.Presenter presenter;

    private Note note;



    Toolbar toolbar;
    SearchView searchView;

    View containerAddHashtag, containerListHashtag;

    ChooseHashtagsAdapter adapter;
    RecyclerViewEmptySupport hashtagsRecyclerView;
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hashtags);
        hashtagsRecyclerView = findViewById(R.id.activity_choose_hashtags_list);
        emptyView = findViewById(R.id.activity_choose_hashtag_empty_view);
        toolbar = findViewById(R.id.activity_choose_hashtag_toolbar);
        searchView = findViewById(R.id.activity_choose_hashtag_search_view);

        containerAddHashtag = findViewById(R.id.activity_choose_hashtag_container_add);
        containerListHashtag = findViewById(R.id.activity_choose_hashtag_container_list);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        note = getIntent().getParcelableExtra("note");


        presenter = new ChooseHashtagsPresenter(this, CacheRepository.getInstance(
                LocalRepository.getRepository(
                        new AppExecutors(), getApplicationContext()
                )
        ));


        //Для чоздания нового хештега
        //Log.d(TAG, "" + generateUniqueId());

        //получить хештеги все

        hashtagsRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ChooseHashtagsAdapter();
        hashtagsRecyclerView.setAdapter(adapter);
        hashtagsRecyclerView.setEmptyView(emptyView);

        presenter.selectHashtagsShow(note);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //todo сохранить хештег
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0){
                    //добавление хештега
                    containerAddHashtag.setVisibility(View.VISIBLE);
                    containerListHashtag.setVisibility(View.INVISIBLE);
                }else {
                    //вернуть
                    containerAddHashtag.setVisibility(View.INVISIBLE);
                    containerListHashtag.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });


        containerAddHashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addNewHashtag(searchView.getQuery().toString());
                searchView.setQuery("", false);
                presenter.selectHashtagsShow(note);
            }
        });


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
    public void hashtagShow(List<ChooseHashtag> hashtagList) {
        //Передать список хэштегов адаптеру
        adapter.setList(hashtagList);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.saveSelectHashtags(note, adapter.getList());
    }
}