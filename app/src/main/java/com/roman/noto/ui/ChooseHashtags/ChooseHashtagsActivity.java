package com.roman.noto.ui.ChooseHashtags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.roman.noto.R;
import com.roman.noto.RecyclerViewEmptySupport;
import com.roman.noto.data.Note;
import com.roman.noto.data.repository.CacheRepository;
import com.roman.noto.data.repository.LocalRepository;
import com.roman.noto.ui.About.AboutPresenter;
import com.roman.noto.util.AppExecutors;

import java.util.List;
import java.util.UUID;

public class ChooseHashtagsActivity extends AppCompatActivity implements ChooseHashtagsContract.View {

    static final String TAG = "ChooseHashtagsActivity";
    public ChooseHashtagsContract.Presenter presenter;

    private Note note;




    ChooseHashtagsAdapter adapter;
    RecyclerViewEmptySupport hashtagsRecyclerView;
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hashtags);
        hashtagsRecyclerView = findViewById(R.id.activity_choose_hashtags_list);
        emptyView = findViewById(R.id.activity_choose_hashtag_empty_view);

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


    }

    //Сгенерировать уникальный id
    public static int generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str = idOne.toString();
        return Math.abs(str.hashCode());
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