package com.example.retrofit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    RecyclerView mRecyclerView;
    List<UPost> mPosts;
    private TextView mTextView;
    private EditText editText;
    private String x = "";

    List<Flower> mFlowers;

    TextView mDateTextView;
    TextView mTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        editText = (EditText) findViewById(R.id.editText);

        mDateTextView = (TextView) findViewById(R.id.textview_date);
        mTimeTextView = (TextView) findViewById(R.id.textview_time);
        fetchDateTime();


    }



    void fetchDateTime() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://date.jsontest.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsontestAPI api = retrofit.create(JsontestAPI.class);
        Call<ServerTime> serverTimeCall = api.getServerDateTime();
        serverTimeCall.enqueue(new Callback<ServerTime>() {
            @Override
            public void onResponse(Call<ServerTime> call, Response<ServerTime> response) {
                ServerTime serverTime = response.body();
                mDateTextView.setText("Дата: " + serverTime.getDate());
                mTimeTextView.setText("Время: " + serverTime.getTime());
            }

            @Override
            public void onFailure(Call<ServerTime> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        "Ошибка!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onClick(View view) {
        mProgressBar.setVisibility(View.VISIBLE);

        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);

        final Call<List<Repos>> call = gitHubService.getRepos("MadWilson");

        call.enqueue(new Callback<List<Repos>>() {
                         @Override
                         public void onResponse(Call<List<Repos>> call, Response<List<Repos>> response) {
                             // response.isSuccessfull() is true if the response code is 2xx
                             if (response.isSuccessful()) {
                                 // Выводим массив имён
                                 mTextView.setText(response.body().toString() + "\n");
                                 for (int i = 0; i < response.body().size(); i++) {
                                     // Выводим имена по отдельности
                                     mTextView.append(response.body().get(i).getName() + "\n");
                                 }

                                 mProgressBar.setVisibility(View.INVISIBLE);
                             } else {
                                 int statusCode = response.code();
                                 // Обрабатываем ошибку
                                 ResponseBody errorBody = response.errorBody();
                                 try {
                                     mTextView.setText(errorBody.string());
                                     mProgressBar.setVisibility(View.INVISIBLE);
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<List<Repos>> call, Throwable throwable) {
                             mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
                         }
                     }
        );
    }

    public void onClick1(View view) {
        mProgressBar.setVisibility(View.VISIBLE);

        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        final Call<User> call =
                gitHubService.getUser("MadWilson");

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // response.isSuccessfull() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    User user = response.body();

                    // Получаем json из github-сервера и конвертируем его в удобный вид
                    mTextView.setText("Аккаунт Github: " + user.getName() +
                            "\nСайт: " + user.getBlog() +
                            "\nКомпания: " + user.getCompany());

                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    try {
                        mTextView.setText(errorBody.string());
                        mProgressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
            }
        });
    }

    public void onClick2(View view) {
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        final Call<List<Contributor>> call =
                gitHubService.repoContributors("square", "picasso");

        call.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                final TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(response.body().toString());
            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable throwable) {
                final TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("Что-то пошло не так: " + throwable.getMessage());
            }
        });
    }

    public void onClick3(View view) {
        mProgressBar.setVisibility(View.VISIBLE);
        String result = editText.getText().toString();
        //x.setText(result);

        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        // часть слова
        final Call<GitResult> call =
                gitHubService.getUsers(result);

        call.enqueue(new Callback<GitResult>() {
            @Override
            public void onResponse(Call<GitResult> call, Response<GitResult> response) {
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    GitResult result = response.body();

                    // Получаем json из github-сервера и конвертируем его в удобный вид
                    // Покажем только первого пользователя
                    String user = "Аккаунт Github: " + result.getItems().get(0).getLogin();
                    mTextView.setText(user);
                    Log.i("Git", String.valueOf(result.getItems().size()));

                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    try {
                        mTextView.setText(errorBody.string());
                        mProgressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GitResult> call, Throwable throwable) {
                mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
            }
        });
    }

    public void onClick4(View view) {
        mProgressBar.setVisibility(View.VISIBLE);
        mPosts = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        UmoriliAdapter adapter = new UmoriliAdapter(mPosts);
        mRecyclerView.setAdapter(adapter);

        UmoriliService umoriliService = UmoriliService.retrofit.create(UmoriliService.class);

        final Call<List<UPost>> call = umoriliService.getData("bash", 50);

        call.enqueue((new Callback<List<UPost>>() {
            @Override
            public void onResponse(Call<List<UPost>> call, Response<List<UPost>> response) {
                // response.isSuccessfull() возвращает true если код ответа 2xx
                if (response.isSuccessful()) {
                    // Выводим массив имён
                    mPosts.addAll(response.body());
                    mRecyclerView.getAdapter().notifyDataSetChanged();

                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    // Обрабатываем ошибку
                    ResponseBody errorBody = response.errorBody();
                    try {
                        Toast.makeText(MainActivity.this, errorBody.string(),
                                Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UPost>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Что-то пошло не так",
                        Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }));
    }

        public void onClick5(View view) {
            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);
            mFlowers = new ArrayList<>();

            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);

            FlowerAdapter adapter = new FlowerAdapter(mFlowers);
            mRecyclerView.setAdapter(adapter);

            mProgressBar.setVisibility(View.VISIBLE);

            FlowersAPI flowersAPI = FlowersAPI.retrofit.create(FlowersAPI.class);
            final Call<List<Flower>> call = flowersAPI.getData();
            call.enqueue(new Callback<List<Flower>>() {
                             @Override
                             public void onResponse(Call<List<Flower>> call, Response<List<Flower>> response) {
                                 // response.isSuccessfull() возвращает true если код ответа 2xx
                                 if (response.isSuccessful()) {
                                     mFlowers.addAll(response.body());
                                     mRecyclerView.getAdapter().notifyDataSetChanged();
                                     mProgressBar.setVisibility(View.INVISIBLE);
                                 } else {
                                     // Обрабатываем ошибку
                                     ResponseBody errorBody = response.errorBody();
                                     try {
                                         Toast.makeText(MainActivity.this, errorBody.string(),
                                                 Toast.LENGTH_SHORT).show();
                                         mProgressBar.setVisibility(View.INVISIBLE);
                                     } catch (IOException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call<List<Flower>> call, Throwable throwable) {
                                 Toast.makeText(MainActivity.this, "Что-то пошло не так",
                                         Toast.LENGTH_SHORT).show();
                                 mProgressBar.setVisibility(View.INVISIBLE);
                             }
                         }
            );
        }
    }



