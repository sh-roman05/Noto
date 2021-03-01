package com.roman.noto.ui.EditHashtags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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

public class EditHashtagsActivity extends AppCompatActivity implements EditHashtagsContract.View {

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



        presenter = new EditHashtagsPresenter(this, CacheRepository.getInstance(
                LocalRepository.getRepository(
                        new AppExecutors(), getApplicationContext()
                )
        ));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hashtagsRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new EditHashtagsAdapter(this);
        hashtagsRecyclerView.setAdapter(adapter);
        hashtagsRecyclerView.setEmptyView(emptyHashtagsList);

        presenter.loadHashtags();
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