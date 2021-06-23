package systems.omnic.shareboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RecyclingActivity extends AppCompatActivity {

    private List<Note> content = new ArrayList<>();
    private RecyclerView recyclerView;
    private CustomRecyclerviewAdapter adapter;
    private ItemTouchHelper.SimpleCallback touch;
    private ArrayList<Note> restored = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling);
        content = DataContainer.getInstance().getRecyclingBin();

        initializeRecyclingView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.recyclingbin));
    }

    public void initializeRecyclingView(){
        adapter = new CustomRecyclerviewAdapter(content, RecyclingActivity.this, null);
        recyclerView = findViewById(R.id.recBinRecView);
        recyclerView.addItemDecoration(new DividerItemDecoration(RecyclingActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(RecyclingActivity.this));

        touch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                switch (direction){
                    case ItemTouchHelper.RIGHT:{
                        Note note = content.remove(pos);
                        adapter.notifyItemRemoved(pos);
                        Snackbar.make(recyclerView, note.getContent(), Snackbar.LENGTH_LONG)
                                .setAction("Undo delete", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        content.add(pos, note);
                                        adapter.notifyItemInserted(pos);
                                    }
                                }).show();
                        break;
                    }
                    case ItemTouchHelper.LEFT:{
                        Note note = content.remove(pos);
                        adapter.notifyItemRemoved(pos);
                        restored.add(note);
                        Snackbar.make(recyclerView, note.getContent(), Snackbar.LENGTH_LONG)
                                .setAction("Undo restores", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        content.add(pos, note);
                                        restored.remove(note);
                                        adapter.notifyItemInserted(pos);
                                    }
                                }).show();
                        break;
                    }
                }


            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(getResources().getColor(R.color.red))
                        .addSwipeLeftBackgroundColor(getResources().getColor(R.color.green))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_forever)
                        .addSwipeLeftActionIcon(R.drawable.ic_restore)
                        .create().decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(touch);
        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    @Override
    public void onBackPressed(){
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("restored", restored);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        for (Note n:restored)
            n.setDateTime(null);
        DataContainer.getInstance().setRecyclingBin(content);
        finish();
        super.onBackPressed();
    }
}