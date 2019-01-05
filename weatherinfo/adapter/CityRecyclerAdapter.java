package us.ait.android.weatherinfo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import us.ait.android.weatherinfo.MainActivity;
import us.ait.android.weatherinfo.R;
import us.ait.android.weatherinfo.WeatherInfoActivity;
import us.ait.android.weatherinfo.data.AppDatabase;
import us.ait.android.weatherinfo.data.City;
import us.ait.android.weatherinfo.touch.CityTouchHelperAdapter;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.ViewHolder> implements CityTouchHelperAdapter {

    public static final String KEY_CITY = "KEY_CITY";

    private List<City> cityList;
    private Context context;

    public CityRecyclerAdapter(Context context, List<City> cityList){
        this.context = context;
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cityRowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_row, parent, false);
        return new ViewHolder(cityRowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final City city = cityList.get(position);
        holder.tvCityName.setText(city.getCityName());
        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResult = new Intent();
                intentResult.setClass(context, WeatherInfoActivity.class);
                intentResult.putExtra(KEY_CITY, city.getCityName());
                context.startActivity(intentResult);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCity(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void addCity(City city) {
        cityList.add(city);
        notifyDataSetChanged();
    }

    public void removeCity(int position){
        final City cityToDelete = cityList.get(position);
        new Thread(){
            @Override
            public void run() {
                AppDatabase.getAppDatabase(context).cityDao().deleteCity(cityToDelete);
            }
        }.start();

        cityList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemDismissed(int position) {
        removeCity(position);
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(cityList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(cityList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }


    private int findCityIndexByCityId(long cityId){
        for (int i = 0; i < cityList.size(); i++) {
            if(cityList.get(i).getCityId() == cityId){
                return i;
            }
        }
        return -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvCityName;
        private ImageButton btnDelete;
        private ConstraintLayout rowLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            rowLayout = itemView.findViewById(R.id.rowLayout);
        }
    }
}
