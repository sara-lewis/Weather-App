package us.ait.android.weatherinfo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import us.ait.android.weatherinfo.adapter.CityRecyclerAdapter;
import us.ait.android.weatherinfo.data.AppDatabase;
import us.ait.android.weatherinfo.data.City;
import us.ait.android.weatherinfo.touch.CityItemTouchHelperCallback;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewCityDialog.CityHandler {

    private CityRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initFab();
        initActionBar(toolbar);
        initViews();
    }

    private void initActionBar(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewCityDialog().show(getFragmentManager(), getString(R.string.Null));
            }
        });
    }

    private void initViews() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set up recycler view stuff ...
        RecyclerView recyclerViewCity = findViewById(R.id.recyclerCity);
        recyclerViewCity.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCity.setHasFixedSize(true);
        initRecyclerView(recyclerViewCity);
    }

    private void initRecyclerView(final RecyclerView recyclerViewCity) {
        new Thread(){
            @Override
            public void run() {
                final List<City> cities = AppDatabase.getAppDatabase(MainActivity.this).cityDao().getAllCities();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new CityRecyclerAdapter(MainActivity.this, cities);
                        recyclerViewCity.setAdapter(adapter);
                        // lets recycleView know that it should use the touchHelper
                        ItemTouchHelper.Callback callback = new CityItemTouchHelperCallback(adapter);
                        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(recyclerViewCity);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addCity) {
            new NewCityDialog().show(getFragmentManager(), getString(R.string.Null));
        } else if (id == R.id.aboutApp) {
            Toast.makeText(this, R.string.about_app, Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void cityCreated(final City city) {
        new Thread(){
            @Override
            public void run() {
                long id = AppDatabase.getAppDatabase(MainActivity.this).cityDao().insertCity(city);
                city.setCityId(id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addCity(city);
                    }
                });
            }
        }.start();
    }
}
