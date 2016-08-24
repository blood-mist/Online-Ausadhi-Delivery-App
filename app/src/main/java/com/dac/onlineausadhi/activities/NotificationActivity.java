package com.dac.onlineausadhi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dac.onlineausadhi.adapters.ItemTouchHelperAdapter;
import com.dac.onlineausadhi.adapters.SimpleItemTouchHelperCallback;
import com.dac.onlineausadhi.adapters.UploadAdapter;
import com.dac.onlineausadhi.classes.NotificationModel;
import com.dac.onlineausadhi.classes.OnLoadMoreListener;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by blood-mist on 5/19/16.
 */
public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "Notification";

    /* List<NotificationListModel> values = new ArrayList<>();*/
    List<NotificationModel> values = new ArrayList<>();

    RecyclerView recycleView;
    SharedPreferences sharedPref;
    String token;
    Boolean read = false;
    Toolbar toolbar;
    TextView clearNotification, emptyView;
    RecycleAdapter recyclerAdapter;
    SharedPreferences.Editor editor;
    protected Handler handler;
    int mCurrentPage = 1;
    int unread;
    private ProgressDialog progressDialog;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_fragment);

        sharedPref = this.getSharedPreferences(getString(R.string.sharedPref), 0);
        token = sharedPref.getString("token", "");
        setupProgressDialog();
        toolbar = (Toolbar) findViewById(R.id.notificationToolbar);
        recycleView = (RecyclerView) findViewById(R.id.notificationRecycle);
        clearNotification = (TextView) findViewById(R.id.clear);
        emptyView = (TextView) findViewById(R.id.empty_view);

        handler = new Handler();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notifications");
            toolbar.setTitleTextColor(Color.WHITE);
        }


        clearNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new clearNotify().execute();

            }
        });

        loadData();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecycleAdapter(this, values, recycleView);
        recycleView.setAdapter(recyclerAdapter);
        recycleView.scrollToPosition(values.size());
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(this, recyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recycleView);

        recyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mCurrentPage++;
                values.add(null);
                recyclerAdapter.notifyItemInserted(values.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new AsyncNotification().execute(getString(R.string.notification_url) + "/" + mCurrentPage);


                    }
                }, 2000);
            }
        });
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void loadData() {
        new AsyncNotification().execute(getString(R.string.notification_url) + "/" + mCurrentPage);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {


        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class AsyncNotification extends AsyncTask<String, Void, String> {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (mCurrentPage == 1) {
                progressDialog.setMessage("Loading Notifications.. Please Wait.");
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("URL", params[0]);
            try {
                UploadAdapter uploadAdapter = new UploadAdapter(params[0], "UTF-8", token, "GET");
                return uploadAdapter.finish("GET");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Log.d(TAG, s);
                progressDialog.dismiss();
                try {
                    JSONObject notifyObj = new JSONObject(s);
                    if (notifyObj.getBoolean("success")) {

                        JSONArray notifyArray = notifyObj.getJSONArray("_data");
                        if (notifyArray != null) {

                            if (mCurrentPage > 1) {
                                values.remove(values.size() - 1);
                                recyclerAdapter.notifyItemRemoved(values.size());
                            }

                            for (int i = 0; i < notifyArray.length(); i++) {
                                JSONObject json_data = notifyArray.getJSONObject(i);
                                NotificationModel notifyModel = new NotificationModel();
                                notifyModel.notification = json_data.getString("message_name");
                                notifyModel.id = json_data.getInt(String.valueOf("id"));
                                notifyModel.status = json_data.getInt(String.valueOf("status"));
                                notifyModel.sales_id = json_data.getInt(String.valueOf("sales_id"));
                                String time = json_data.getString("created_at");
                                notifyModel.type = json_data.getString("notification_type");

                                date = formatter1.parse(time);
                                long epoch = date.getTime();
                                CharSequence timePassedString = DateUtils.getRelativeTimeSpanString(epoch, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                                Log.d(TAG, (String) timePassedString);
                                Log.d("toggleValue", "" + sharedPref.getBoolean("toggleValue", false));
                                if (timePassedString.equals("0 minutes ago")) {
                                    timePassedString = "Just now";
                                }
                                notifyModel.date = (String) timePassedString;

                                notifyModel.notification_body = json_data.getString("message_body");
                                values.add(notifyModel);

                                Log.d(TAG, "Success " + values.size());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerAdapter.notifyItemInserted(values.size() - 1);
                                    }
                                });


                            }
                            if (values.isEmpty()) {
                                recycleView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);

                            } else {
                                recycleView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);

                            }
                            if (sharedPref.getInt("unreadCount", 0) == notifyObj.getInt("count")) {
                                unread = sharedPref.getInt("unreadCount", 0);
                            } else {
                              editor=sharedPref.edit();
                                editor.putInt("unreadCount",notifyObj.getInt("count"));
                                editor.apply();

                            }
                        }
                            count = values.size();
                            recyclerAdapter.setLoaded();

                    } else {
                        Toast.makeText(NotificationActivity.this,notifyObj.getString("msg"), Toast.LENGTH_LONG).show();
                        finish();
                        Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else
                Log.d(TAG, "Error in async task");
        }
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

        private final int VIEW_ITEM = 1;
        private final int VIEW_PROG = 0;
        private LayoutInflater inflater;
        private Context context;

        private List<NotificationModel> notify;


        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 2;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;

        public RecycleAdapter(Context context, List<NotificationModel> notify, RecyclerView recycleview) {
            this.context = context;
            this.notify = notify;
            inflater = LayoutInflater.from(context);

            if (recycleview.getLayoutManager() instanceof LinearLayoutManager) {

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycleview
                        .getLayoutManager();


                recycleview
                        .addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView,
                                                   int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (count < 10) {
                                    loading = false;
                                } else {
                                    totalItemCount = linearLayoutManager.getItemCount();

                                    lastVisibleItem = linearLayoutManager
                                            .findLastVisibleItemPosition();
                                    if (!loading
                                            && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                        // End has been reached
                                        // Do something
                                        if (onLoadMoreListener != null) {
                                            onLoadMoreListener.onLoadMore();
                                        }
                                        loading = true;
                                    }
                                }
                            }
                        });
            }
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            if (viewType == VIEW_ITEM) {
                View view = inflater.inflate(R.layout.item_notification, parent, false);
                holder = new NotificationHolder(view);
            } else {
                View view = inflater.inflate(R.layout.loading_item, parent, false);
                holder = new LoadingHolder(view);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof NotificationHolder) {
                final NotificationModel notification = notify.get(position);
                ((NotificationHolder) holder).notificationName.setText(notification.notification);
                ((NotificationHolder) holder).date.setText(notification.date);
                final int id = notification.id;
                final int status = notification.status;
                final int sales_id = notification.sales_id;
                Log.d("salesid", "" + sales_id);
                if (status == 1) {

                    ((NotificationHolder) holder).itemLayout.setBackgroundColor(Color.WHITE);

                } else {
                    ((NotificationHolder) holder).itemLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.primary_light));


                }

                ((NotificationHolder) holder).itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((NotificationHolder) holder).itemLayout.setBackgroundColor(Color.WHITE);
                        if (status == 0) {

                            new AsyncRead().execute(getString(R.string.notification_read_url) + "/" + id);

                            SharedPreferences sf = context.getSharedPreferences(getString(R.string.sharedPref), 0);
                            unread = sf.getInt("unreadCount", 0);
                            unread--;
                            editor = sf.edit();
                            editor.putInt("unreadCount", unread);
                            editor.commit();
                            Log.d("unread", "" + unread);
                            Log.d("unread", "" + sf.getInt("unreadCount", 0));
                            notification.status = 1;
                            notifyItemChanged(position);


                        }
                        Intent intent;
                        Bundle bundle = new Bundle();
                        bundle.putInt("sales_id", sales_id);
                        if ((notification.type).equals("refill")) {
                            intent = new Intent(NotificationActivity.this, NotificationDetailsActivity.class);
                        } else {
                            intent = new Intent(NotificationActivity.this, DeliveryDetailsActivity.class);
                        }
                        intent.putExtras(bundle);
                        startActivity(intent);


                    }
                });

            } else {
                ((LoadingHolder) holder).progressBar.setIndeterminate(true);
            }


        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemViewType(int position) {
            return notify.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }


        @Override
        public int getItemCount() {
            return notify.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {

        }

        @Override
        public void onItemDismiss(int position) {
            int id = notify.get(position).id;
            new SingleNotificationDelete().execute(getString(R.string.notification_delete_url) + "/" + id);
            notify.remove(position);
            notifyDataSetChanged();
            if(notify.isEmpty()){
                recycleView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }

        }

        public class NotificationHolder extends RecyclerView.ViewHolder {
            public TextView notificationName;
            public TextView date;
            public LinearLayout itemLayout;

            public NotificationHolder(View itemView) {
                super(itemView);
                itemLayout = (LinearLayout) itemView.findViewById(R.id.notification_container);
                notificationName = (TextView) itemView.findViewById(R.id.notification);
                date = (TextView) itemView.findViewById(R.id.date);
            }
        }

        public class LoadingHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public LoadingHolder(View itemView) {
                super(itemView);
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            }
        }


    }

    private class clearNotify extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                UploadAdapter uploadAdapter = new UploadAdapter(getString(R.string.notification_delete_url), "UTF-8", token, "DELETE");
                return uploadAdapter.finish("DELETE");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(NotificationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            } else {
                Log.d(TAG, result);
                try {
                    JSONObject clearNotiObj = new JSONObject(result);
                    if (clearNotiObj.getBoolean("success")) {
                        int size = values.size();
                        values.clear();
                        editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();
                        recyclerAdapter.notifyItemRangeRemoved(0, size);
                        recycleView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(NotificationActivity.this,clearNotiObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private class AsyncRead extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Log.d("readUrl", params[0]);
                UploadAdapter uploadAdapter = new UploadAdapter(params[0], "UTF-8", token, "GET");
                return uploadAdapter.finish("GET");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG, result);
                try {
                    JSONObject readObj = new JSONObject(result);
                    if (readObj.getBoolean("success")) {

                    } else {
                        Toast.makeText(NotificationActivity.this, readObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SingleNotificationDelete extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.d("url", params[0]);
            try {
                UploadAdapter uploadAdapter = new UploadAdapter(params[0], "UTF-8", token, "DELETE");
                return uploadAdapter.finish("DELETE");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(NotificationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            } else {
                Log.d(TAG, result);
                JSONObject singleDelObj = null;
                try {
                    singleDelObj = new JSONObject(result);
                    if(singleDelObj.getBoolean("success")) {
                        unread = singleDelObj.getInt("unreadCount");
                        editor = sharedPref.edit();
                        editor.putInt("unreadCount", unread);
                        editor.apply();
                    }else{
                        Toast.makeText(NotificationActivity.this, singleDelObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

